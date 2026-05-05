// =====================================================
// course-detail.js — Course Detail Page Logic
// =====================================================

let currentCourseId = null;
let currentEnrollmentId = null;
let currentEnrolled = false;
let lessons = [];
let completedLessonIds = new Set();

document.addEventListener('DOMContentLoaded', async () => {
  const params = new URLSearchParams(window.location.search);
  currentCourseId = parseInt(params.get('id'));

  if (!currentCourseId) {
    window.location.href = 'courses.html';
    return;
  }

  await loadCourseDetail();
});

async function loadCourseDetail() {
  try {
    const data = await apiGet(`/courses/${currentCourseId}`);
    const course = data.course;
    lessons = data.lessons;
    currentEnrolled = data.enrolled;
    currentEnrollmentId = data.enrollmentId || null;

    // Update document title
    document.title = `${course.title} — LearnHub`;

    renderCourseHero(course, data);
    renderLessons(data);

    document.getElementById('loadingState').classList.add('hidden');
    document.getElementById('courseDetailHero').classList.remove('hidden');
    document.getElementById('lessonsSection').style.display = 'block';

    // Load lesson progress if enrolled
    if (currentEnrolled && currentEnrollmentId) {
      await loadLessonProgress();
    }
  } catch (err) {
    document.getElementById('loadingState').innerHTML = `
      <div style="text-align:center;">
        <div style="font-size:3rem;margin-bottom:1rem;">⚠️</div>
        <p style="color:var(--text-muted);">Could not load course. Backend may not be running.</p>
        <a href="courses.html" class="btn-secondary" style="display:inline-flex;margin-top:1rem;">← Back to Courses</a>
      </div>`;
    console.error(err);
  }
}

function renderCourseHero(course, data) {
  const { emoji, gradient } = getCategoryInfo(course.category);

  document.getElementById('detailCategory').textContent = `${emoji} ${course.category}`;
  document.getElementById('detailTitle').textContent = course.title;
  document.getElementById('detailDesc').textContent = course.description;

  document.getElementById('detailMeta').innerHTML = `
    <div class="course-meta-item">⏱️ <strong>${course.duration}</strong></div>
    <div class="course-meta-item">📖 <strong>${course.totalLessons} lessons</strong></div>
    <div class="course-meta-item">👤 <strong>${course.instructor}</strong></div>
  `;

  // Enrollment card
  document.getElementById('detailThumbPreview').style.background = gradient;
  document.getElementById('detailThumbPreview').textContent = emoji;
  document.getElementById('cardDuration').textContent = course.duration;
  document.getElementById('cardLessons').textContent = `${course.totalLessons} lessons`;
  document.getElementById('cardInstructor').textContent = course.instructor;

  updateEnrollButton(data.enrolled, data.progressPercent || 0, course.totalLessons);
}

function updateEnrollButton(enrolled, progressPercent, totalLessons) {
  const btn = document.getElementById('enrollBtn');
  const msg = document.getElementById('enrollMessage');
  const progressSection = document.getElementById('progressSection');

  if (enrolled) {
    btn.innerHTML = '✅ Already Enrolled';
    btn.classList.add('enrolled');
    btn.disabled = true;
    msg.textContent = 'You are enrolled. Complete lessons below to track progress!';

    progressSection.classList.remove('hidden');
    updateProgressUI(progressPercent, 0, totalLessons);
  } else {
    btn.innerHTML = '🚀 Enroll Now — It\'s Free';
    btn.classList.remove('enrolled');
    btn.disabled = false;
    msg.textContent = 'Free enrollment. Start learning immediately.';
    progressSection.classList.add('hidden');

    // Show not-enrolled note
    document.getElementById('notEnrolledNote').classList.remove('hidden');
  }
}

function updateProgressUI(percent, completedCount, totalCount) {
  document.getElementById('progressPercent').textContent = `${percent}%`;
  document.getElementById('progressFill').style.width = `${percent}%`;
  document.getElementById('completedLessonsCount').textContent = completedCount;
  document.getElementById('totalLessonsCount').textContent = totalCount;
}

function renderLessons(data) {
  const list = document.getElementById('lessonList');
  if (!lessons || lessons.length === 0) {
    list.innerHTML = '<p style="color:var(--text-muted);">No lessons available for this course.</p>';
    return;
  }

  list.innerHTML = lessons.map(lesson => buildLessonItem(lesson, false, data.enrolled)).join('');
}

function buildLessonItem(lesson, completed, enrolled) {
  const completedClass = completed ? 'completed' : '';
  const checkIcon = completed ? '✓' : '';
  const statusText = completed ? '<span class="lesson-status">✅ Completed</span>' : '';
  const videoIcon = lesson.videoUrl ? '🎬 ' : '';
  
  // Status is now read-only. Completion is triggered by the video at the end of the course.
  return `
    <div class="lesson-item ${completedClass}" id="lesson-item-${lesson.id}">
      <div class="lesson-check" id="check-${lesson.id}"
        title="${completed ? 'Lesson Completed' : 'Watch video to complete course'}">
        ${checkIcon}
      </div>
      <div class="lesson-num">${lesson.lessonOrder}</div>
      <div class="lesson-info" style="cursor:pointer;" onclick="${enrolled ? `playLessonVideo(${lesson.id})` : 'showEnrollAlert()'}">
        <div class="lesson-title">${videoIcon}${lesson.title}</div>
        <div class="lesson-duration">⏱️ ${lesson.duration}</div>
      </div>
      ${statusText}
    </div>
  `;
}

function showVideoAlert() {
  showToast('Please watch the video to the end to complete this lesson! 🎥', 'info');
}
window.showVideoAlert = showVideoAlert;

// ---- Video Player Logic ----
function playLessonVideo(lessonId) {
  const lesson = lessons.find(l => l.id === lessonId);
  if (!lesson || !lesson.videoUrl) return;

  const section = document.getElementById('videoPlayerSection');
  const video = document.getElementById('courseVideo');
  const title = document.getElementById('nowPlayingTitle');

  title.textContent = lesson.title;
  video.src = lesson.videoUrl;
  section.classList.remove('hidden');
  
  // Scroll to video
  section.scrollIntoView({ behavior: 'smooth', block: 'center' });

  video.play();

  // Automatic completion when video ends
  video.onended = async () => {
    const isLastLesson = lesson.lessonOrder === lessons.length;
    
    if (isLastLesson) {
      showToast('Final video finished! Completing course... 🏆', 'success');
    } else {
      showToast(`'${lesson.title}' completed! 🎉`, 'success');
    }
    
    try {
      if (isLastLesson) {
        // Mark ALL lessons in this course as complete to finish the course instantly
        const completionPromises = lessons.map(l => 
          apiPost(`/lessons/${l.id}/complete`, { enrollmentId: currentEnrollmentId })
        );
        await Promise.all(completionPromises);
        lessons.forEach(l => completedLessonIds.add(l.id));
      } else {
        // Just mark this single lesson as complete
        await apiPost(`/lessons/${lessonId}/complete`, { enrollmentId: currentEnrollmentId });
        completedLessonIds.add(lessonId);
      }
      
      refreshLessonUI();
      
      const percent = Math.round((completedLessonIds.size / lessons.length) * 100);
      updateProgressUI(percent, completedLessonIds.size, lessons.length);
      
      if (percent === 100) {
        showCertificateLink();
        showToast('🏆 Course 100% complete! Congratulations!', 'success');
      }
    } catch (err) {
      console.error('Failed to complete lesson:', err);
      showToast('Error saving progress. Please try again.', 'error');
    }
  };
}

function closeVideoPlayer() {
  const section = document.getElementById('videoPlayerSection');
  const video = document.getElementById('courseVideo');
  video.pause();
  video.src = '';
  section.classList.add('hidden');
}

window.playLessonVideo = playLessonVideo;
window.closeVideoPlayer = closeVideoPlayer;

async function loadLessonProgress() {
  try {
    const progressList = await apiGet(`/lessons/progress/${currentEnrollmentId}`);
    completedLessonIds = new Set(
      progressList.filter(lp => lp.completed).map(lp => lp.lessonId)
    );
    refreshLessonUI();
    
    const percent = Math.round((completedLessonIds.size / lessons.length) * 100);
    updateProgressUI(
      percent,
      completedLessonIds.size,
      lessons.length
    );

    // If 100% complete, show certificate button if it exists
    if (percent === 100) {
      showCertificateLink();
    }
  } catch (err) {
    console.error('Failed to load lesson progress:', err);
  }
}

function showCertificateLink() {
  const btn = document.getElementById('enrollBtn');
  // Get course title from current UI
  const courseTitle = document.getElementById('detailTitle').textContent;
  
  btn.innerHTML = '🏆 Claim Certificate';
  btn.disabled = false;
  btn.classList.remove('enrolled');
  btn.style.background = 'linear-gradient(135deg, var(--accent2), #ffca28)';
  btn.onclick = () => window.location.href = `certificate.html?id=${currentCourseId}&course=${encodeURIComponent(courseTitle)}`;
}

function refreshLessonUI() {
  lessons.forEach(lesson => {
    const item = document.getElementById(`lesson-item-${lesson.id}`);
    const check = document.getElementById(`check-${lesson.id}`);
    if (!item || !check) return;

    const completed = completedLessonIds.has(lesson.id);
    if (completed) {
      item.classList.add('completed');
      check.textContent = '✓';
      // Update status text
      const existing = item.querySelector('.lesson-status');
      if (!existing) {
        const span = document.createElement('span');
        span.className = 'lesson-status';
        span.textContent = '✅ Completed';
        item.appendChild(span);
      }
    } else {
      item.classList.remove('completed');
      check.textContent = '';
      const existing = item.querySelector('.lesson-status');
      if (existing) existing.remove();
    }
  });
}

async function toggleLesson(lessonId) {
  if (!currentEnrollmentId) return;

  const wasCompleted = completedLessonIds.has(lessonId);
  const endpoint = wasCompleted
    ? `/lessons/${lessonId}/incomplete`
    : `/lessons/${lessonId}/complete`;

  try {
    await apiPost(endpoint, { enrollmentId: currentEnrollmentId });

    if (wasCompleted) {
      completedLessonIds.delete(lessonId);
      showToast('Lesson marked incomplete', 'info');
    } else {
      completedLessonIds.add(lessonId);
      showToast('Lesson completed! 🎉', 'success');
    }

    refreshLessonUI();

    const percent = Math.round((completedLessonIds.size / lessons.length) * 100);
    updateProgressUI(percent, completedLessonIds.size, lessons.length);

    if (percent === 100) {
      setTimeout(() => {
        showToast('🏆 Course completed! Congratulations!', 'success');
        showCertificateLink();
      }, 400);
    } else {
      // If no longer 100%, revert button
      const btn = document.getElementById('enrollBtn');
      btn.innerHTML = '✅ Already Enrolled';
      btn.disabled = true;
      btn.classList.add('enrolled');
      btn.style.background = '';
      btn.onclick = handleEnroll;
    }
  } catch (err) {
    showToast('Failed to update lesson. Try again.', 'error');
    console.error(err);
  }
}

async function handleEnroll() {
  if (currentEnrolled) return;
  const btn = document.getElementById('enrollBtn');
  btn.disabled = true;
  btn.innerHTML = '⏳ Enrolling...';

  try {
    const result = await apiPost('/enrollments', { courseId: currentCourseId });
    if (result.status === 201) {
      currentEnrolled = true;
      currentEnrollmentId = result.data.enrollmentId;
      showToast('Successfully enrolled! Start learning now 🎉', 'success');
      btn.innerHTML = '✅ Already Enrolled';
      btn.classList.add('enrolled');
      document.getElementById('enrollMessage').textContent = 'You are enrolled. Complete lessons below to track progress!';
      document.getElementById('progressSection').classList.remove('hidden');
      document.getElementById('notEnrolledNote').classList.add('hidden');
      updateProgressUI(0, 0, lessons.length);
      // Re-render lessons as enrolled
      renderLessons({ lessons, enrolled: true });
    } else if (result.status === 409) {
      showToast('You are already enrolled in this course.', 'info');
      btn.innerHTML = '✅ Already Enrolled';
      btn.classList.add('enrolled');
    } else {
      btn.disabled = false;
      btn.innerHTML = '🚀 Enroll Now — It\'s Free';
      showToast('Enrollment failed. Try again.', 'error');
    }
  } catch (err) {
    btn.disabled = false;
    btn.innerHTML = '🚀 Enroll Now — It\'s Free';
    showToast('Server error. Is the backend running?', 'error');
    console.error(err);
  }
}

function showEnrollAlert() {
  showToast('Please enroll in this course first to track progress!', 'info');
}

window.handleEnroll = handleEnroll;
window.toggleLesson = toggleLesson;
window.showEnrollAlert = showEnrollAlert;
