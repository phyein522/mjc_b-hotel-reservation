import { page } from "./core.js";
import { startRoute } from "./session.js";
import { homePage, searchPage, detailPage } from "./hotels.js";
import {
  bookingPage,
  paymentPage,
  paymentResultPage,
  bookingsPage,
  couponsPage
} from "./bookings-payments.js";
import { loginPage, signupPage } from "./auth.js";
import { profilePage } from "./profile.js";
import { reviewsPage } from "./reviews.js";
import {
  adminDashboard,
  adminHotels,
  adminRooms,
  adminRoomAdd,
  adminRates,
  adminReservations,
  adminCheckins,
  adminPayments,
  adminCustomers,
  adminPromotions,
  adminSales,
  adminSettlement
} from "./admin.js";

const routes = {
  "home": homePage,
  "search": searchPage,
  "detail": detailPage,
  "booking": bookingPage,
  "payment": paymentPage,
  "payment-success": () => paymentResultPage("success"),
  "payment-fail": () => paymentResultPage("fail"),
  "bookings": bookingsPage,
  "coupons": couponsPage,
  "reviews": () => reviewsPage(false),
  "login": loginPage,
  "signup": signupPage,
  "profile": profilePage,
  "admin-dashboard": adminDashboard,
  "admin-hotels": adminHotels,
  "admin-rooms": adminRooms,
  "admin-room-add": adminRoomAdd,
  "admin-rates": adminRates,
  "admin-reservations": adminReservations,
  "admin-checkins": adminCheckins,
  "admin-payments": adminPayments,
  "admin-customers": adminCustomers,
  "admin-promotions": adminPromotions,
  "admin-reviews": () => reviewsPage(true),
  "admin-sales": adminSales,
  "admin-settlement": adminSettlement
};

startRoute(routes[page], page);
