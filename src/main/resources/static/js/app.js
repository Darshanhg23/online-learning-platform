// =====================================================
// app.js — Shared Utilities for Online Learning Platform
// =====================================================

const API_BASE = '/api';

// ---- Category Emoji Map ----
const CATEGORY_EMOJI = {
  'Programming':     { emoji: '☕', gradient: 'linear-gradient(135deg,#6366f1,#8b5cf6)' },
  'Web Development': { emoji: '🌐', gradient: 'linear-gradient(135deg,#2563eb,#06b6d4)' },
  'Backend':         { emoji: '⚙️', gradient: 'linear-gradient(135deg,#10b981,#059669)' },
  'Database':        { emoji: '🗄️', gradient: 'linear-gradient(135deg,#f59e0b,#d97706)' },
};

function getCategoryInfo(category) {
  return CATEGORY_EMOJI[category] || { emoji: '📘', gradient: 'linear-gradient(135deg,#6c63ff,#a29bff)' };
}

// ---- Toast Notification ----
function showToast(message, type = 'info') {
  const container = document.getElementById('toastContainer');
  if (!container) return;
  const icons = { success: '✅', error: '❌', info: 'ℹ️' };
  const toast = document.createElement('div');
  toast.className = `toast ${type}`;
  toast.innerHTML = `<span class="toast-icon">${icons[type] || 'ℹ️'}</span> ${message}`;
  container.appendChild(toast);
  setTimeout(() => {
    toast.style.transition = 'opacity 0.4s, transform 0.4s';
    toast.style.opacity = '0';
    toast.style.transform = 'translateX(110%)';
    setTimeout(() => toast.remove(), 400);
  }, 3200);
}

// ---- API Helpers (credentials:include sends session cookie) ----
async function apiGet(endpoint) {
  const res = await fetch(API_BASE + endpoint, { credentials: 'include' });
  if (!res.ok) throw new Error(`GET ${endpoint} failed: ${res.status}`);
  return res.json();
}

async function apiPost(endpoint, data) {
  const res = await fetch(API_BASE + endpoint, {
    method: 'POST',
    credentials: 'include',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(data)
  });
  return { status: res.status, data: await res.json() };
}

async function apiPut(endpoint, data) {
  const res = await fetch(API_BASE + endpoint, {
    method: 'PUT',
    credentials: 'include',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(data)
  });
  if (!res.ok) throw new Error(`PUT ${endpoint} failed: ${res.status}`);
  return res.json();
}

// ---- Build Course Card HTML ----
function buildCourseCard(course, enrolled = false) {
  const { emoji, gradient } = getCategoryInfo(course.category);
  const initials = course.instructor.split(' ').map(w => w[0]).join('').slice(0,2);
  const enrolledClass = enrolled ? 'enrolled' : '';
  const enrolledIcon = enrolled ? '✅ Enrolled' : '🚀 Enroll Now';

  return `
    <div class="course-card" id="card-${course.id}" data-category="${course.category}">
      <div class="card-thumbnail">
        <div class="card-thumbnail-placeholder" style="background:${gradient}">
          ${emoji}
        </div>
        <span class="card-category-badge">${course.category}</span>
      </div>
      <div class="card-body">
        <h3 class="card-title">${course.title}</h3>
        <p class="card-desc">${course.description}</p>
        <div class="card-meta">
          <span>Duration: ${course.duration}</span>
          <span>${course.totalLessons} lessons</span>
        </div>
        <div class="card-instructor">
          <div class="avatar-sm">${initials}</div>
          ${course.instructor}
        </div>
        <button class="btn-enroll ${enrolledClass}"
          id="enroll-btn-${course.id}"
          onclick="quickEnroll(${course.id}, this)"
          ${enrolled ? 'disabled' : ''}>
          ${enrolledIcon}
        </button>
        <a href="course-detail.html?id=${course.id}" class="btn-view-course">
          View Course →
        </a>
      </div>
    </div>
  `;
}

// ---- Quick Enroll from Course Cards ----
async function quickEnroll(courseId, btn) {
  if (btn.disabled) return;
  btn.disabled = true;
  btn.innerHTML = '⏳ Enrolling...';
  try {
    const result = await apiPost('/enrollments', { courseId });
    if (result.status === 201) {
      btn.innerHTML = '✅ Enrolled';
      btn.classList.add('enrolled');
      showToast('Successfully enrolled! 🎉', 'success');
    } else if (result.status === 409) {
      btn.innerHTML = '✅ Enrolled';
      btn.classList.add('enrolled');
      showToast('You are already enrolled in this course.', 'info');
    } else {
      btn.disabled = false;
      btn.innerHTML = '🚀 Enroll Now';
      showToast('Enrollment failed. Try again.', 'error');
    }
  } catch (err) {
    btn.disabled = false;
    btn.innerHTML = '🚀 Enroll Now';
    showToast('Server error. Is the backend running?', 'error');
    console.error(err);
  }
}

// ---- Build Progress Bar HTML ----
function buildProgressBar(percent) {
  return `
    <div class="progress-container">
      <div class="progress-label">
        <span>Progress</span>
        <strong>${percent}%</strong>
      </div>
      <div class="progress-bar-wrap">
        <div class="progress-bar-fill" style="width:${percent}%"></div>
      </div>
    </div>
  `;
}

// ---- Navbar scroll effect + hamburger ----
document.addEventListener('DOMContentLoaded', () => {
  const navbar = document.getElementById('navbar');
  const hamburger = document.getElementById('hamburger');
  const navLinks = document.getElementById('navLinks');

  window.addEventListener('scroll', () => {
    if (window.scrollY > 20) navbar.classList.add('scrolled');
    else navbar.classList.remove('scrolled');
  });

  if (hamburger) {
    hamburger.addEventListener('click', () => {
      navLinks.classList.toggle('open');
    });
  }
});

// Expose quickEnroll globally
window.quickEnroll = quickEnroll;
