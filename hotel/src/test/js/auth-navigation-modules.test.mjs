import assert from "node:assert/strict";
import { existsSync, readFileSync } from "node:fs";
import { dirname, resolve } from "node:path";
import { fileURLToPath } from "node:url";

const testDir = dirname(fileURLToPath(import.meta.url));
const assetsRoot = resolve(testDir, "../../main/resources/static/omnistay/assets");
const sourceRoot = resolve(testDir, "../..");
const moduleNames = [
  "core.js",
  "auth.js",
  "hotels.js",
  "bookings-payments.js",
  "reviews.js",
  "admin.js",
  "profile.js",
  "session.js"
];

for (const moduleName of moduleNames) {
  assert.equal(existsSync(resolve(assetsRoot, moduleName)), true, `Missing feature module: ${moduleName}`);
}

const entrySource = readFileSync(resolve(assetsRoot, "app.js"), "utf8");
const authSource = readFileSync(resolve(assetsRoot, "auth.js"), "utf8");
const securitySource = readFileSync(
  resolve(sourceRoot, "main/java/com/mjc/hotel/config/SecurityConfig.java"),
  "utf8"
);

assert.ok(entrySource.split(/\r?\n/).length <= 80, "app.js must remain a small route entry point.");
assert.equal(entrySource.includes("function loginPage"), false, "Login implementation must live in auth.js.");
assert.equal(entrySource.includes("function signupPage"), false, "Signup implementation must live in auth.js.");
assert.equal(entrySource.includes("function redirectToLogin"), false, "Session security guards must live in session.js.");
assert.equal(authSource.includes("function completeAuthentication"), true, "Auth completion helper is required.");
assert.equal(
  (authSource.match(/completeAuthentication\(user/g) || []).length,
  3,
  "Email login, Google login, and email signup must share the same post-auth navigation."
);
assert.equal(authSource.includes("location.assign(target)"), true, "Successful authentication must navigate explicitly.");
assert.equal(authSource.includes("const data = qs(form)"), true, "Signup must keep a stable form reference across awaits.");
assert.equal(
  /await authConfigPromise;\s*const data = qs\(event\.currentTarget\)/.test(authSource),
  false,
  "Signup must not read event.currentTarget after an await."
);
assert.equal(authSource.includes("회원가입이 완료되고 로그인되었습니다."), false, "Signup must not stop on the signup screen.");
assert.equal(
  securitySource.includes('.requestMatchers("/api/users/signup").permitAll()'),
  true,
  "Standard email signup must be publicly reachable when email verification is disabled."
);

console.log("AUTH_NAVIGATION_MODULE_AUDIT_OK");
