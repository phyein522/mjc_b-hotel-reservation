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
const profileSource = readAsset("profile.js");
const reviewEligibilitySource = `${coreSource}\n${reviewsSource}`;
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
assert.match(hotelsSource, /\/api\/review\/hotel\/\$\{id\}/, "Hotel details must load reviews for the selected hotel.");
assert.doesNotMatch(coreSource, /\["reviews",/, "User top navigation must not expose a standalone review item.");
assert.match(hotelsSource, /eligibleReviewBookings/, "Hotel details must check review eligibility.");
assert.match(hotelsSource, /bookingId=.*eligibleBooking\.reservationId/, "Eligible hotel reviews must open with the matching booking selected.");
assert.match(hotelsSource, /reviews\.map\(\(review\) => reviewCard\(review\)\)/, "Hotel review rendering must not pass Array.map index values as card actions.");
assert.match(hotelsSource, /class="review-photo"/, "Hotel details must render attached review images.");
assert.match(bookingsSource, /\/api\/rates\/hotels\/\$\{selectedHotel\}\/rooms/, "Booking room selection must use the backend room-detail list API.");
assert.match(adminSource, /data-delete-room-image/, "Room image deletion must be available in room management.");
assert.match(adminSource, /amenities:/, "Room create and update must send backend room amenities.");
assert.match(bookingsSource, /\/api\/payment\/\$\{paymentId\}/, "Payment detail lookup must use the backend single-payment endpoint.");
assert.match(adminSource, /method: "PATCH"/, "Backend update operations must remain connected.");
assert.match(adminSource, /\/api\/coupons\?userId=\$\{encodeURIComponent\(data\.userId\)\}[\s\S]*method: "POST"/, "Coupon creation must send the manager userId query parameter required by the backend.");
assert.match(adminSource, /\/api\/coupons\?userId=\$\{encodeURIComponent\(userId\)\}[\s\S]*method: "PATCH"/, "Coupon updates must send the manager userId query parameter required by the backend.");
assert.match(adminSource, /data\.startDate[\s\S]*data\.endDate[\s\S]*data\.roomId[\s\S]*data\.userId/, "Promotion creation must validate backend-required fields before submission.");
assert.match(adminSource, /name="membership"[\s\S]*NEW_MEMBER[\s\S]*VVIP/, "Promotion-sale creation must target a membership grade.");
assert.match(adminSource, /membership, saleDes, userId: Number\(managerUserId\)/, "Promotion-sale updates must preserve the membership target and manager authorization.");
assert.doesNotMatch(adminSource, /<label><span>사용자 ID<\/span><input name="userId"/, "Promotion-sale target must not be presented as an individual user.");
assert.match(adminSource, /data-edit-promo=/, "Promotion update must be exposed in the admin screen.");
assert.match(adminSource, /data-edit-promo-sale=/, "Promotion-sale update must be exposed in the admin screen.");
assert.match(reviewsSource, /query\.set\("keyword"/, "Review search must send the backend keyword parameter.");
assert.match(reviewsSource, /\/api\/review\/\$\{reviewId\}/, "Review detail must use the backend single-review endpoint.");
assert.match(reviewsSource, /method: "PATCH"/, "Review editing must use the backend update endpoint.");
assert.match(profileSource, /data-profile-tab="reviews"/, "Profile must expose a review-edit tab.");
assert.match(reviewsSource, /data-delete-review=/, "User review management must expose review deletion.");
assert.match(reviewsSource, /name="keepReviewPhoto"/, "Review editing must allow existing photos to be kept or removed individually.");
assert.match(reviewsSource, /name="newPhotos"/, "Review editing must allow new photos to be attached.");
assert.match(reviewsSource, /uploadReviewPhotos\(form\.elements\.newPhotos\.files\)/, "Review editing must upload newly selected photos before saving.");
assert.match(reviewEligibilitySource, /response\.booking \|\| response/, "Review eligibility must unwrap booking-list response items.");
assert.match(reviewEligibilitySource, /status === "paid" \|\| status === "1"/, "Review eligibility must recognize persisted paid payment status.");
assert.match(bookingsSource, /\/api\/coupons\/available/, "Coupon screens and payment must request globally generated coupons that meet the order conditions.");
assert.doesNotMatch(bookingsSource, /\/api\/usercoupons/, "Coupon screens and payment must not issue or load personal user coupons.");
assert.match(bookingsSource, /discountAmount/, "Coupon discounts must be carried through the payment flow.");
assert.match(bookingsSource, /paymentsByBookingId/, "Booking history must join payment data instead of hardcoding booking completion status.");
assert.match(bookingsSource, /reviews\.html\?bookingId=/, "Paid bookings must expose the review-writing flow.");

console.log("BACKEND_FEATURE_COVERAGE_AUDIT_OK");
