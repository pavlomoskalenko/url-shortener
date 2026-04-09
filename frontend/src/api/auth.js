const API_BASE = '/api';

const TOKEN_KEY = 'access_token';
const REFRESH_KEY = 'refresh_token';

export function getAccessToken() {
  return localStorage.getItem(TOKEN_KEY);
}

export function getRefreshToken() {
  return localStorage.getItem(REFRESH_KEY);
}

export function saveTokens(accessToken, refreshToken) {
  localStorage.setItem(TOKEN_KEY, accessToken);
  if (refreshToken) {
    localStorage.setItem(REFRESH_KEY, refreshToken);
  }
}

export function clearTokens() {
  localStorage.removeItem(TOKEN_KEY);
  localStorage.removeItem(REFRESH_KEY);
}

export function isLoggedIn() {
  return !!getAccessToken();
}

export async function register(username, password) {
  const response = await fetch(`${API_BASE}/users`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ username, password }),
  });

  const data = await response.json();

  if (!response.ok) {
    const error = new Error(data.message || 'Registration failed');
    error.status = response.status;
    error.fieldErrors = data.fieldErrors || null;
    throw error;
  }

  return data;
}

export async function login(username, password) {
  const response = await fetch(`${API_BASE}/tokens`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ username, password }),
  });

  const data = await response.json();

  if (!response.ok) {
    const error = new Error(data.message || 'Login failed');
    error.status = response.status;
    error.fieldErrors = data.fieldErrors || null;
    throw error;
  }

  saveTokens(data.accessToken, data.refreshToken);
  return data;
}

export async function refreshAccessToken() {
  const refreshToken = getRefreshToken();
  if (!refreshToken) {
    throw new Error('No refresh token');
  }

  const response = await fetch(`${API_BASE}/tokens/refresh`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ refreshToken }),
  });

  const data = await response.json();

  if (!response.ok) {
    clearTokens();
    throw new Error(data.message || 'Session expired');
  }

  saveTokens(data.accessToken, null);
  return data;
}

export async function authFetch(url, options = {}) {
  const token = getAccessToken();
  const headers = { ...options.headers };
  if (token) {
    headers['Authorization'] = `Bearer ${token}`;
  }

  let response = await fetch(url, { ...options, headers });

  if (response.status === 401) {
    try {
      await refreshAccessToken();
      headers['Authorization'] = `Bearer ${getAccessToken()}`;
      response = await fetch(url, { ...options, headers });
    } catch {
      clearTokens();
      throw new Error('Session expired. Please log in again.');
    }
  }

  return response;
}
