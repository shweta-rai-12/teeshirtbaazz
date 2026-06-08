import axios from 'axios';

const api = axios.create({
  baseURL: '/api',
});

api.interceptors.request.use((config) => {
  const token = localStorage.getItem('tsb_token');
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

export default api;

export function saveSession(authResponse) {
  localStorage.setItem('tsb_token', authResponse.token);
  localStorage.setItem('tsb_user', JSON.stringify({
    email: authResponse.email,
    name: authResponse.name,
    role: authResponse.role,
  }));
}

export function getSessionUser() {
  try {
    return JSON.parse(localStorage.getItem('tsb_user') || 'null');
  } catch {
    return null;
  }
}

export function isAdmin() {
  return getSessionUser()?.role === 'ROLE_ADMIN';
}

export function isLoggedIn() {
  return Boolean(localStorage.getItem('tsb_token'));
}
