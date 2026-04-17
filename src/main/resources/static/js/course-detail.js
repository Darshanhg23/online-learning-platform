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

  return `
    <div class="lesson-item ${completedClass}" id="lesson-item-${lesson.id}">
      <div class="lesson-check" id="check-${lesson.id}"
        onclick="${enrolled ? `toggleLesson(${lesson.id})` : 'showEnrollAlert()'}"
        title="${enrolled ? 'Click to toggle completion' : 'Enroll to track progress'}">
        ${checkIcon}
      </div>
      <div class="lesson-num">${lesson.lessonOrder}</div>
      <div class="lesson-info">
        <div class="lesson-title">${lesson.title}</div>
        <div class="lesson-duration">⏱️ ${lesson.duration}</div>
      </div>
      ${statusText}
    </div>
  `;
}

async function loadLessonProgress() {
  try {
    const progressList = await apiGet(`/lessons/progress/${currentEnrollmentId}`);
    completedLessonIds = new Set(
      progressList.filter(lp => lp.completed).map(lp => lp.lessonId)
    );
    refreshLessonUI();
    updateProgressUI(
      Math.round((completedLessonIds.size / lessons.length) * 100),
      completedLessonIds.size,
      lessons.length
    );
  } catch (err) {
    console.error('Failed to load lesson progress:', err);
  }
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
      setTimeout(() => showToast('🏆 Course completed! Congratulations!', 'success'), 400);
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
