const params = new URLSearchParams(window.location.search);
export const API_BASE = params.get("api") || window.OMNISTAY_API_BASE || "";

export async function request(path, options = {}) {
  const headers = options.body instanceof FormData
    ? (options.headers || {})
    : { "Content-Type": "application/json", ...(options.headers || {}) };
  const response = await fetch(`${API_BASE}${path}`, { ...options, headers });
  const text = await response.text();
  const payload = text ? JSON.parse(text) : null;
  if (!response.ok) {
    const message = payload?.message || payload?.error || response.statusText || "API request failed";
    throw new Error(`${response.status} ${message}`);
  }
  return unwrap(payload);
}

export function unwrap(payload) {
  if (payload && Object.prototype.hasOwnProperty.call(payload, "responseData")) return payload.responseData;
  if (payload && Object.prototype.hasOwnProperty.call(payload, "data")) return payload.data;
  return payload;
}

export function pageItems(payload) {
  const data = unwrap(payload);
  if (!data) return [];
  if (Array.isArray(data)) return data;
  if (Array.isArray(data.content)) return data.content;
  return [data];
}

export function qs(form) {
  return Object.fromEntries(new FormData(form).entries());
}

export function money(value) {
  const number = Number(value || 0);
  return number.toLocaleString("ko-KR", { style: "currency", currency: "KRW", maximumFractionDigits: 0 });
}

export function todayMonth() {
  return new Date().toISOString().slice(0, 7);
}

export function escapeHtml(value) {
  return String(value ?? "").replace(/[&<>"']/g, (char) => ({
    "&": "&amp;",
    "<": "&lt;",
    ">": "&gt;",
    "\"": "&quot;",
    "'": "&#039;"
  }[char]));
}
