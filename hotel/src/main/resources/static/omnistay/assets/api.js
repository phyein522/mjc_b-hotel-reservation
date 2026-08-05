const params = new URLSearchParams(window.location.search);
export const API_BASE = params.get("api") || window.OMNISTAY_API_BASE || "";

export async function request(path, options = {}) {
  const accessToken = localStorage.getItem("omnistayAccessToken");
  const isAuthEndpoint = path.startsWith("/api/auth/");
  const contentHeaders = options.body instanceof FormData
    ? (options.headers || {})
    : { "Content-Type": "application/json", ...(options.headers || {}) };
  const headers = accessToken && !isAuthEndpoint
    ? { Authorization: `Bearer ${accessToken}`, ...contentHeaders }
    : contentHeaders;
  const response = await fetch(`${API_BASE}${path}`, { ...options, headers });
  const text = await response.text();
  let payload = null;
  if (text) {
    try {
      payload = JSON.parse(text);
    } catch {
      payload = { message: text };
    }
  }
  if (!response.ok) {
    if (response.status === 401 && accessToken && !isAuthEndpoint) {
      window.dispatchEvent(new CustomEvent("omnistay:authentication-required", {
        detail: { status: response.status, path }
      }));
    }
    const responseDetail = typeof payload?.responseData === "string"
      ? payload.responseData
      : typeof payload?.data === "string"
        ? payload.data
        : "";
    const message = responseDetail || payload?.message || payload?.error || response.statusText || "API request failed";
    const error = new Error(`${response.status} ${message}`);
    error.status = response.status;
    error.path = path;
    throw error;
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
