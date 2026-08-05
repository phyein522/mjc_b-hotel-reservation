const params = new URLSearchParams(window.location.search);
export const API_BASE = params.get("api") || window.OMNISTAY_API_BASE || "";

const ACCESS_TOKEN_KEY = "omnistayAccessToken";
const REFRESH_TOKEN_KEY = "omnistayRefreshToken";
let refreshPromise = null;

export async function request(path, options = {}) {
  const { skipAuthRefresh = false, ...fetchOptions } = options;
  let accessToken = localStorage.getItem(ACCESS_TOKEN_KEY);
  const isAuthEndpoint = path.startsWith("/api/auth/");
  let result = await executeRequest(path, fetchOptions, accessToken);

  if (result.response.status === 401 && !isAuthEndpoint && !skipAuthRefresh
      && localStorage.getItem(REFRESH_TOKEN_KEY)) {
    accessToken = await refreshAuthentication(path);
    result = await executeRequest(path, fetchOptions, accessToken);
  }

  if (result.response.status === 401 && !isAuthEndpoint) {
    requireAuthentication(path, result.response.status);
  }

  return handleResponse(path, result.response, result.payload);
}

async function executeRequest(path, options, accessToken) {
  const contentHeaders = options.body instanceof FormData
    ? (options.headers || {})
    : { "Content-Type": "application/json", ...(options.headers || {}) };
  const headers = accessToken && !path.startsWith("/api/auth/")
    ? { Authorization: `Bearer ${accessToken}`, ...contentHeaders }
    : contentHeaders;
  const response = await fetch(`${API_BASE}${path}`, { ...options, headers });
  return { response, payload: await parsePayload(response) };
}

async function parsePayload(response) {
  const text = await response.text();
  if (!text) return null;
  try {
    return JSON.parse(text);
  } catch {
    return { message: text };
  }
}

async function refreshAuthentication(path) {
  if (!refreshPromise) {
    refreshPromise = performTokenRefresh()
      .catch((error) => {
        requireAuthentication(path, error.status || 401);
        throw error;
      })
      .finally(() => {
        refreshPromise = null;
      });
  }
  return refreshPromise;
}

async function performTokenRefresh() {
  const refreshToken = localStorage.getItem(REFRESH_TOKEN_KEY);
  if (!refreshToken) {
    throw createError(401, "/api/auth/refresh", "Refresh token is missing");
  }

  const response = await fetch(`${API_BASE}/api/auth/refresh`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ refreshToken })
  });
  const payload = await parsePayload(response);
  if (!response.ok) {
    throw responseError("/api/auth/refresh", response, payload);
  }

  const tokens = unwrap(payload);
  if (!tokens?.accessToken || !tokens?.refreshToken) {
    throw createError(401, "/api/auth/refresh", "Token refresh response is invalid");
  }
  localStorage.setItem(ACCESS_TOKEN_KEY, tokens.accessToken);
  localStorage.setItem(REFRESH_TOKEN_KEY, tokens.refreshToken);
  return tokens.accessToken;
}

function requireAuthentication(path, status) {
  localStorage.removeItem(ACCESS_TOKEN_KEY);
  localStorage.removeItem(REFRESH_TOKEN_KEY);
  window.dispatchEvent(new CustomEvent("omnistay:authentication-required", {
    detail: { status, path }
  }));
}

function handleResponse(path, response, payload) {
  if (!response.ok) {
    throw responseError(path, response, payload);
  }
  return unwrap(payload);
}

function responseError(path, response, payload) {
  const responseDetail = typeof payload?.responseData === "string"
    ? payload.responseData
    : typeof payload?.data === "string"
      ? payload.data
      : "";
  const message = responseDetail || payload?.message || payload?.error
    || response.statusText || "API request failed";
  return createError(response.status, path, message);
}

function createError(status, path, message) {
  const error = new Error(`${status} ${message}`);
  error.status = status;
  error.path = path;
  return error;
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
