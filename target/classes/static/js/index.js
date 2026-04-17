// =====================================================
// index.js — Homepage Logic
// =====================================================

document.addEventListener('DOMContentLoaded', async () => {
  await loadFeaturedCourses();
  await loadEnrollmentCount();
});

async function loadFeaturedCourses() {
  const grid = document.getElementById('featuredGrid');
  try {
    const [courses, enrollments] = await Promise.all([
      apiGet('/courses/featured'),
      apiGet('/enrollments')
    ]);

    const enrolledIds = new Set(enrollments.map(e => e.courseId));
    document.getElementById('totalCoursesStat').textContent = courses.length;

    if (courses.length === 0) {
      grid.innerHTML = '<p style="color:var(--text-muted); grid-column:1/-1; text-align:center;">No featured courses found.</p>';
      return;
    }

    grid.innerHTML = courses.map(c => buildCourseCard(c, enrolledIds.has(c.id))).join('');
  } catch (err) {
    grid.innerHTML = `
      <div style="grid-column:1/-1; text-align:center; padding:3rem;">
        <div style="font-size:2.5rem; margin-bottom:1rem;">⚠️</div>
        <p style="color:var(--text-muted);">Could not load courses. Make sure the backend server is running on <strong style="color:var(--primary-light);">localhost:8080</strong></p>
      </div>`;
    console.error('Failed to load featured courses:', err);
  }
}

async function loadEnrollmentCount() {
  try {
    const enrollments = await apiGet('/enrollments');
    document.getElementById('enrolledStat').textContent = enrollments.length;
  } catch (err) {
    document.getElementById('enrolledStat').textContent = '0';
  }
}
