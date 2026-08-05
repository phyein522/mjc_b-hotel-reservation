import assert from "node:assert/strict";
import { existsSync, readFileSync } from "node:fs";
import { dirname, resolve } from "node:path";
import { fileURLToPath } from "node:url";

const testDir = dirname(fileURLToPath(import.meta.url));
const staticRoot = resolve(testDir, "../../main/resources/static");
const assetsRoot = resolve(staticRoot, "omnistay/assets");
const readAsset = (name) => readFileSync(resolve(assetsRoot, name), "utf8");

const appSource = readAsset("app.js");
const apiSource = readAsset("api.js");
const authSource = readAsset("auth.js");
const coreSource = readAsset("core.js");
const hotelsSource = readAsset("hotels.js");
const bookingsSource = readAsset("bookings-payments.js");
const reviewsSource = readAsset("reviews.js");
const adminSource = readAsset("admin.js");
const authControllerSource = readFileSync(
  resolve(staticRoot, "../../java/com/mjc/hotel/auth/controller/AuthRestController.java"),
  "utf8"
);
const securitySource = readFileSync(
  resolve(staticRoot, "../../java/com/mjc/hotel/config/SecurityConfig.java"),
  "utf8"
);

assert.equal(existsSync(resolve(staticRoot, "profile.html")), true, "A logged-in user profile screen is required.");
assert.equal(existsSync(resolve(assetsRoot, "profile.js")), true, "User profile API integration must be isolated in its own module.");
assert.match(appSource, /"profile": profilePage/, "The profile screen must be routable.");
assert.match(coreSource, /\["profile",/, "The logged-in user navigation must expose profile management.");
assert.match(apiSource, /Authorization/, "Protected backend calls must send a stored JWT access token.");
assert.match(apiSource, /Bearer \$\{accessToken\}/, "JWT access tokens must use the backend Bearer header contract.");
assert.match(apiSource, /omnistay:authentication-required/, "Authentication failures must be surfaced consistently.");
assert.match(authSource, /setAuthTokens/, "Authentication responses must persist backend JWT tokens when present.");
assert.match(authSource, /\/api\/users\/signup[\s\S]*\/api\/auth\/login/, "Standard signup must exchange credentials for a JWT session.");
assert.match(authControllerSource, /AuthenticatedUserDto/, "Authentication endpoints must return both the user and JWT tokens.");
assert.match(securitySource, /HttpMethod\.GET[\s\S]*\/api\/hotelimage\/image/, "Public hotel detail assets must be readable without weakening write security.");
assert.match(coreSource, /HOTEL_MANAGER/, "Admin screens must enforce the backend user role contract.");
assert.match(hotelsSource, /\/api\/rates\/hotels\/\$\{id\}\/rooms/, "Hotel details must use the backend room-detail list API.");
assert.match(bookingsSource, /\/api\/rates\/hotels\/\$\{selectedHotel\}\/rooms/, "Booking room selection must use the backend room-detail list API.");
assert.match(adminSource, /data-delete-room-image/, "Room image deletion must be available in room management.");
assert.match(adminSource, /amenities:/, "Room create and update must send backend room amenities.");
assert.match(bookingsSource, /\/api\/payment\/\$\{paymentId\}/, "Payment detail lookup must use the backend single-payment endpoint.");
assert.match(adminSource, /method: "PATCH"/, "Backend update operations must remain connected.");
assert.match(adminSource, /data-edit-promo=/, "Promotion update must be exposed in the admin screen.");
assert.match(adminSource, /data-edit-promo-sale=/, "Promotion-sale update must be exposed in the admin screen.");
assert.match(reviewsSource, /query\.set\("keyword"/, "Review search must send the backend keyword parameter.");
assert.match(reviewsSource, /\/api\/review\/\$\{reviewId\}/, "Review detail must use the backend single-review endpoint.");
assert.match(reviewsSource, /method: "PATCH"/, "Review editing must use the backend update endpoint.");

console.log("BACKEND_FEATURE_COVERAGE_AUDIT_OK");
