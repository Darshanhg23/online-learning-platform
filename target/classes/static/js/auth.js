// =====================================================
// auth.js — Authentication utilities for LearnHub
// =====================================================

const AUTH_KEY = 'learnhub_user';

/** Get current user from sessionStorage */
function getCurrentUser() {
  try {
    const raw = sessionStorage.getItem(AUTH_KEY);
    return raw ? JSON.parse(raw) : null;
  } catch { return null; }
}

/** Save user to sessionStorage after login/register */
function setCurrentUser(user) {
  sessionStorage.setItem(AUTH_KEY, JSON.stringify(user));
}

/** Clear user from sessionStorage */
function clearCurrentUser() {
  sessionStorage.removeItem(AUTH_KEY);
}

/**
 * Redirect to login if not authenticated.
 * Pages that require login call this at the top.
 */
async function requireAuth() {
  try {
    const res = await fetch('/api/auth/me', { credentials: 'include' });
    if (!res.ok) {
      clearCurrentUser();
      window.location.href = 'login.html';
      return null;
    }
    const user = await res.json();
    setCurrentUser(user);
    return user;
  } catch {
    window.location.href = 'login.html';
    return null;
  }
}

/**
 * Update the navbar to show authenticated state.
 * Shows user name + Logout when logged in, Login button when logged out.
 */
async function initNavAuth() {
  try {
    const res = await fetch('/api/auth/me', { credentials: 'include' });
    const navLinks = document.getElementById('navLinks');
    if (!navLinks) return;

    // Remove old login/user items if any
    document.querySelectorAll('.nav-auth-item').forEach(el => el.remove());

    if (res.ok) {
      const user = await res.json();
      setCurrentUser(user);

      // Show Admin link ONLY for ADMIN role
      if (user.role === 'ADMIN') {
        const liAdmin = document.createElement('li');
        liAdmin.className = 'nav-auth-item';
        liAdmin.innerHTML = `<a href="admin.html"> Admin</a>`;
        navLinks.appendChild(liAdmin);
      }

      // Show user avatar + logout
      const li1 = document.createElement('li');
      li1.className = 'nav-auth-item';
      li1.innerHTML = `<span class="nav-user-badge" id="navUserBadge">👤 ${user.name}</span>`;

      const li2 = document.createElement('li');
      li2.className = 'nav-auth-item';
      li2.innerHTML = `<button class="nav-logout-btn" id="logoutBtn" onclick="handleLogout()">Logout</button>`;

      navLinks.appendChild(li1);
      navLinks.appendChild(li2);
    } else {
      clearCurrentUser();
      // Show Login button
      const li = document.createElement('li');
      li.className = 'nav-auth-item';
      li.innerHTML = `<a href="login.html" class="nav-cta" id="navLoginBtn">Login</a>`;
      navLinks.appendChild(li);
    }
  } catch {
    // Silent fail — backend might not be running
  }
}

/** Logout handler */
async function handleLogout() {
  try {
    await fetch('/api/auth/logout', { method: 'POST', credentials: 'include' });
  } catch { /* ignore */ }
  clearCurrentUser();
  window.location.href = 'login.html';
}

/**
 * Redirect to home if not admin.
 */
async function requireAdmin() {
  const user = await requireAuth();
  if (user && user.role !== 'ADMIN') {
    window.location.href = 'index.html';
    return null;
  }
  return user;
}

// Expose globally
window.handleLogout = handleLogout;
window.requireAuth = requireAuth;
window.requireAdmin = requireAdmin;
window.initNavAuth = initNavAuth;
window.getCurrentUser = getCurrentUser;
