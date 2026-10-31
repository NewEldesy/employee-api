const API_BASE = 'http://localhost:8989/api';

class ApiError extends Error {
  constructor(message, status, data) {
    super(message);
    this.status = status;
    this.data = data;
  }
}

async function request(method, path, body) {
  const headers = { 'Content-Type': 'application/json' };

  const token = sessionStorage.getItem('jwt_token');
  if (token) {
    headers['Authorization'] = 'Bearer ' + token;
  }

  const options = { method, headers };
  if (body !== undefined) {
    options.body = JSON.stringify(body);
  }

  const res = await fetch(API_BASE + path, options);

  if (res.status === 204) return null;

  let data;
  try { data = await res.json(); } catch { data = null; }

  if (!res.ok) {
    const msg = Array.isArray(data)
      ? data.join(', ')
      : (data?.message || `Erreur ${res.status}`);
    throw new ApiError(msg, res.status, data);
  }

  return data;
}

const Api = {
  login(username, password) {
    return request('POST', '/auth/login', { username, password })
      .then(data => data.token);
  },

  getEmployees(page = 0, size = 10) {
    return request('GET', `/employees?page=${page}&size=${size}&sort=lastName`);
  },

  getEmployee(id) {
    return request('GET', `/employees/${encodeURIComponent(id)}`);
  },

  createEmployee(payload) {
    return request('POST', '/employees', payload);
  },

  updateEmployee(id, payload) {
    return request('PUT', `/employees/${encodeURIComponent(id)}`, payload);
  },

  deleteEmployee(id) {
    return request('DELETE', `/employees/${encodeURIComponent(id)}`);
  },
};
