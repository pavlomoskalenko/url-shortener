import { authFetch } from './auth.js';

const API_BASE = '/api';

export async function createShortUrl({ originalUrl, customAlias, expireInDays }) {
  const body = { originalUrl };

  if (customAlias && customAlias.trim()) {
    body.customAlias = customAlias.trim();
  }

  if (expireInDays) {
    body.expireInDays = Number(expireInDays);
  }

  const response = await authFetch(`${API_BASE}/short-url`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(body),
  });

  const data = await response.json();

  if (!response.ok) {
    const error = new Error(data.message || 'Failed to create short URL');
    error.status = response.status;
    error.fieldErrors = data.fieldErrors || null;
    throw error;
  }

  return data;
}
