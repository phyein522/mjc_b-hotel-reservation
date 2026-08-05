import assert from "node:assert/strict";

const storage = new Map();
globalThis.localStorage = {
  getItem: (key) => storage.get(key) ?? null,
  setItem: (key, value) => storage.set(key, String(value)),
  removeItem: (key) => storage.delete(key)
};

const events = [];
globalThis.window = {
  location: { search: "" },
  dispatchEvent: (event) => events.push(event)
};

const requests = [];
globalThis.fetch = async (url, options) => {
  requests.push({ url, options });
  return jsonResponse({ responseData: { ok: true } });
};

const { request } = await import("../../main/resources/static/omnistay/assets/api.js");

storage.set("omnistayAccessToken", "jwt-access-token");
await request("/api/users/7");
assert.equal(requests.at(-1).options.headers.Authorization, "Bearer jwt-access-token");

await request("/api/auth/config");
assert.equal(requests.at(-1).options.headers.Authorization, undefined);

requests.length = 0;
storage.set("omnistayAccessToken", "expired-access-token");
storage.set("omnistayRefreshToken", "old-refresh-token");
globalThis.fetch = async (url, options) => {
  requests.push({ url, options });
  if (url.endsWith("/api/auth/refresh")) {
    assert.equal(options.headers.Authorization, undefined);
    assert.deepEqual(JSON.parse(options.body), { refreshToken: "old-refresh-token" });
    return jsonResponse({ responseData: {
      accessToken: "new-access-token",
      refreshToken: "new-refresh-token"
    } });
  }
  if (options.headers.Authorization === "Bearer expired-access-token") {
    return new Response("Authentication required", { status: 401 });
  }
  assert.equal(options.headers.Authorization, "Bearer new-access-token");
  return jsonResponse({ responseData: { ok: true } });
};

assert.deepEqual(await request("/api/users/7"), { ok: true });
assert.equal(requests.length, 3);
assert.equal(storage.get("omnistayAccessToken"), "new-access-token");
assert.equal(storage.get("omnistayRefreshToken"), "new-refresh-token");

requests.length = 0;
storage.set("omnistayAccessToken", "expired-again");
storage.set("omnistayRefreshToken", "shared-refresh-token");
let refreshCalls = 0;
globalThis.fetch = async (url, options) => {
  requests.push({ url, options });
  if (url.endsWith("/api/auth/refresh")) {
    refreshCalls += 1;
    await new Promise((resolve) => setTimeout(resolve, 5));
    return jsonResponse({ responseData: {
      accessToken: "shared-access-token",
      refreshToken: "rotated-refresh-token"
    } });
  }
  if (options.headers.Authorization === "Bearer expired-again") {
    return new Response("Authentication required", { status: 401 });
  }
  return jsonResponse({ responseData: { ok: true } });
};

await Promise.all([request("/api/users/7"), request("/api/users/8")]);
assert.equal(refreshCalls, 1);

events.length = 0;
storage.set("omnistayAccessToken", "expired-access-token");
storage.set("omnistayRefreshToken", "invalid-refresh-token");
globalThis.fetch = async (url) => url.endsWith("/api/auth/refresh")
  ? new Response("Invalid refresh token", { status: 401 })
  : new Response("Authentication required", { status: 401 });

await assert.rejects(() => request("/api/users/7"), /401 Invalid refresh token/);
assert.equal(storage.has("omnistayAccessToken"), false);
assert.equal(storage.has("omnistayRefreshToken"), false);
assert.equal(events.length, 1);
assert.equal(events[0].type, "omnistay:authentication-required");
assert.equal(events[0].detail.path, "/api/users/7");

console.log("SECURITY_API_AUDIT_OK");

function jsonResponse(payload) {
  return new Response(JSON.stringify(payload), {
    status: 200,
    headers: { "Content-Type": "application/json" }
  });
}
