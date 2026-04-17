// =====================================================
// dashboard.js — My Courses Dashboard Logic
// =====================================================

document.addEventListener('DOMContentLoaded', async () => {
  await loadDashboard();
});

async function loadDashboard() {
  const loading = document.getElementById('dashboardLoading');
  const list = document.getElementById('enrolledList');
  const empty = document.getElementById('dashboardEmpty');

  try {
    const enrollments = await apiGet('/enrollments');

    loading.classList.add('hidden');

    if (enrollments.length === 0) {
      empty.classList.remove('hidden');
      updateStats(0, 0, 0, 0);
      return;
    }

    list.classList.remove('hidden');
    list.innerHTML = enrollments.map(e => buildEnrolledCard(e)).join('');

    // Calculate stats
    const total = enrollments.length;
    const completed = enrollments.filter(e => e.progressPercent === 100).length;
    const inProgress = enrollments.filter(e => e.progressPercent > 0 && e.progressPercent < 100).length;
    const avgProgress = total > 0
      ? Math.round(enrollments.reduce((sum, e) => sum + e.progressPercent, 0) / total)
      : 0;

    updateStats(total, completed, avgProgress, inProgress);

  } catch (err) {
    loading.innerHTML = `
      <div style="text-align:center; padding:3rem;">
        <div style="font-size:2.5rem; margin-bottom:1rem;">⚠️</div>
        <p style="color:var(--text-muted);">Cannot connect to backend. Make sure Spring Boot is running at <strong style="color:var(--primary-light);">localhost:8080</strong></p>
      </div>`;
    console.error('Dashboard load error:', err);
  }
}

function buildEnrolledCard(enrollment) {
  const { emoji, gradient } = getCategoryInfo(enrollment.courseCategory);
  const percent = enrollment.progressPercent || 0;

  let statusBadge = '';
  if (percent === 100) {
    statusBadge = `<span style="background:rgba(67,233,123,0.15);color:var(--accent);border:1px solid rgba(67,233,123,0.3);padding:3px 10px;border-radius:999px;font-size:0.72rem;font-weight:700;">✅ Completed</span>`;
  } else if (percent > 0) {
    statusBadge = `<span style="background:rgba(247,151,30,0.15);color:#f7971e;border:1px solid rgba(247,151,30,0.3);padding:3px 10px;border-radius:999px;font-size:0.72rem;font-weight:700;">🔥 In Progress</span>`;
  } else {
    statusBadge = `<span style="background:rgba(108,99,255,0.12);color:var(--primary-light);border:1px solid rgba(108,99,255,0.2);padding:3px 10px;border-radius:999px;font-size:0.72rem;font-weight:700;">📌 Not Started</span>`;
  }

  const completedLessons = Math.round((percent / 100) * (enrollment.courseTotalLessons || 0));

  return `
    <div class="enrolled-card" id="enrolled-card-${enrollment.id}">
      <div class="enrolled-thumb" style="background:${gradient}">${emoji}</div>
      <div class="enrolled-info">
        <div class="enrolled-category">${enrollment.courseCategory}</div>
        <div class="enrolled-title" title="${enrollment.courseTitle}">${enrollment.courseTitle}</div>
        <div class="enrolled-instructor">👤 ${enrollment.courseInstructor} · ⏱️ ${enrollment.courseDuration}</div>
        <div style="display:flex;align-items:center;gap:0.75rem;margin-bottom:0.75rem;flex-wrap:wrap;">
          ${statusBadge}
          <span style="font-size:0.78rem;color:var(--text-dim);">${completedLessons} / ${enrollment.courseTotalLessons || 0} lessons</span>
        </div>
        ${buildProgressBar(percent)}
      </div>
      <div class="enrolled-actions">
        <a href="course-detail.html?id=${enrollment.courseId}" class="btn-resume">
          ${percent === 100 ? '🔁 Review' : '▶️ Resume'}
        </a>
      </div>
    </div>
  `;
}

function updateStats(total, completed, avgProgress, inProgress) {
  animateNumber('statEnrolled', total);
  animateNumber('statCompleted', completed);
  document.getElementById('statAvgProgress').textContent = `${avgProgress}%`;
  animateNumber('statInProgress', inProgress);
}

function animateNumber(elementId, target) {
  const el = document.getElementById(elementId);
  if (!el) return;
  let current = 0;
  const step = Math.max(1, Math.floor(target / 20));
  const interval = setInterval(() => {
    current = Math.min(current + step, target);
    el.textContent = current;
    if (current >= target) clearInterval(interval);
  }, 40);
}
