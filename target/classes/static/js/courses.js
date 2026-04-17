// =====================================================
// courses.js — All Courses Page Logic
// =====================================================

let allCourses = [];
let enrolledIds = new Set();
let activeCategory = '';
let searchDebounceTimer = null;

document.addEventListener('DOMContentLoaded', async () => {
  await loadCategoriesAndCourses();
  setupSearch();
});

async function loadCategoriesAndCourses() {
  try {
    const [courses, categories, enrollments] = await Promise.all([
      apiGet('/courses'),
      apiGet('/courses/categories'),
      apiGet('/enrollments')
    ]);

    allCourses = courses;
    enrolledIds = new Set(enrollments.map(e => e.courseId));

    buildCategoryFilters(categories);
    renderCourses(allCourses);
  } catch (err) {
    document.getElementById('coursesGrid').innerHTML = `
      <div style="grid-column:1/-1; text-align:center; padding:3rem;">
        <div style="font-size:2.5rem; margin-bottom:1rem;">⚠️</div>
        <p style="color:var(--text-muted);">Cannot connect to backend. Make sure the Spring Boot server is running at <strong style="color:var(--primary-light);">localhost:8080</strong></p>
      </div>`;
    console.error(err);
  }
}

function buildCategoryFilters(categories) {
  const bar = document.getElementById('filterBar');
  categories.forEach(cat => {
    const btn = document.createElement('button');
    btn.className = 'filter-btn';
    btn.dataset.category = cat;
    btn.textContent = `${getCategoryInfo(cat).emoji} ${cat}`;
    btn.addEventListener('click', () => setCategory(cat, btn));
    bar.appendChild(btn);
  });

  document.getElementById('filterAll').addEventListener('click', () => {
    setCategory('', document.getElementById('filterAll'));
  });
}

function setCategory(category, clickedBtn) {
  activeCategory = category;
  document.querySelectorAll('.filter-btn').forEach(b => b.classList.remove('active'));
  clickedBtn.classList.add('active');
  applyFilters();
}

function setupSearch() {
  const input = document.getElementById('searchInput');
  input.addEventListener('input', () => {
    clearTimeout(searchDebounceTimer);
    searchDebounceTimer = setTimeout(applyFilters, 300);
  });
}

function applyFilters() {
  const keyword = document.getElementById('searchInput').value.toLowerCase().trim();

  let filtered = allCourses;

  if (activeCategory) {
    filtered = filtered.filter(c => c.category === activeCategory);
  }
  if (keyword) {
    filtered = filtered.filter(c =>
      c.title.toLowerCase().includes(keyword) ||
      c.description.toLowerCase().includes(keyword) ||
      c.instructor.toLowerCase().includes(keyword)
    );
  }

  renderCourses(filtered);
}

function renderCourses(courses) {
  const grid = document.getElementById('coursesGrid');
  const empty = document.getElementById('emptyState');
  const count = document.getElementById('courseCount');

  count.textContent = courses.length;

  if (courses.length === 0) {
    grid.innerHTML = '';
    empty.classList.remove('hidden');
  } else {
    empty.classList.add('hidden');
    grid.innerHTML = courses.map(c => buildCourseCard(c, enrolledIds.has(c.id))).join('');
  }
}

function clearSearch() {
  document.getElementById('searchInput').value = '';
  activeCategory = '';
  document.querySelectorAll('.filter-btn').forEach(b => b.classList.remove('active'));
  document.getElementById('filterAll').classList.add('active');
  applyFilters();
}

window.clearSearch = clearSearch;
