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
  return new Response(JSON.stringify({ responseData: { ok: true } }), {
    status: 200,
    headers: { "Content-Type": "application/json" }
  });
};

const { request } = await import("../../main/resources/static/omnistay/assets/api.js");

storage.set("omnistayAccessToken", "jwt-access-token");
await request("/api/users/7");
assert.equal(requests.at(-1).options.headers.Authorization, "Bearer jwt-access-token");

await request("/api/auth/config");
assert.equal(requests.at(-1).options.headers.Authorization, undefined);

globalThis.fetch = async () => new Response("Authentication required", { status: 401 });
await assert.rejects(() => request("/api/users/7"), /401 Authentication required/);
assert.equal(events.at(-1).type, "omnistay:authentication-required");
assert.equal(events.at(-1).detail.path, "/api/users/7");

console.log("SECURITY_API_AUDIT_OK");
