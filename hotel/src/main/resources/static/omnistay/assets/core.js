import { request, pageItems, qs, money, escapeHtml, todayMonth } from "./api.js";

const app = document.querySelector("#app");
const page = document.body.dataset.page;
const rel = document.body.dataset.area === "admin" ? "../" : "";
const TOSS_SDK_URL = "https://js.tosspayments.com/v2/standard";
const TOSS_SAMPLE_CLIENT_KEY = "test_ck_D5GePWvyJnrK0W0k6q8gLzN97Eoq";
let hotelsCache = [];
let tossSdkPromise = null;
const ADMIN_ROLES = new Set(["HOTEL_MANAGER", "ADMIN", "SUPER_ADMIN"]);

const userNav = [
  ["home", "홈", "index.html"],
  ["search", "숙소 검색", "search.html"],
  ["bookings", "예약내역", "bookings.html"],
  ["coupons", "쿠폰함", "coupons.html"],
  ["profile", "내 정보", "profile.html"]
];

function getCurrentUser() {
  try {
    return JSON.parse(localStorage.getItem("omnistayCurrentUser") || "null");
  } catch {
    return null;
  }
}

function setCurrentUser(user) {
  localStorage.setItem("omnistayCurrentUser", JSON.stringify(user));
}

function setAuthTokens(authResult) {
  const accessToken = authResult?.accessToken || authResult?.tokens?.accessToken;
  const refreshToken = authResult?.refreshToken || authResult?.tokens?.refreshToken;
  if (!accessToken) {
    throw new Error("로그인 응답에 access token이 없습니다.");
  }
  localStorage.setItem("omnistayAccessToken", accessToken);
  if (refreshToken) {
    localStorage.setItem("omnistayRefreshToken", refreshToken);
  } else {
    localStorage.removeItem("omnistayRefreshToken");
  }
}

function clearCurrentUser() {
  localStorage.removeItem("omnistayCurrentUser");
  localStorage.removeItem("omnistayAccessToken");
  localStorage.removeItem("omnistayRefreshToken");
}

function hasAdminRole(user = getCurrentUser()) {
  return ADMIN_ROLES.has(user?.role);
}

function hasAccessToken() {
  return Boolean(localStorage.getItem("omnistayAccessToken"));
}

function safeRedirect(defaultPage = "index.html") {
  const redirect = new URLSearchParams(location.search).get("redirect");
  if (!redirect || redirect.startsWith("//") || /^[a-z][a-z0-9+.-]*:/i.test(redirect)) {
    return defaultPage;
  }
  return redirect;
}

function loadTossPaymentsSdk() {
  if (window.TossPayments) return Promise.resolve(window.TossPayments);
  if (tossSdkPromise) return tossSdkPromise;
  tossSdkPromise = new Promise((resolve, reject) => {
    const script = document.createElement("script");
    script.src = TOSS_SDK_URL;
    script.async = true;
    script.onload = () => resolve(window.TossPayments);
    script.onerror = () => reject(new Error("Toss Payments SDK load failed."));
    document.head.appendChild(script);
  });
  return tossSdkPromise;
}

function tossMethodToPaymentMethod(method) {
  if (method === "CARD") return "CreditCard";
  if (method === "TRANSFER" || method === "VIRTUAL_ACCOUNT") return "BankTransfer";
  return "Online";
}

function paymentBookingId(payment) {
  return payment?.bookingId || payment?.booking?.bookingId || "";
}

const adminNav = [
  ["dashboard", "대시보드", "dashboard.html", "운영"],
  ["hotels", "호텔 관리", "hotels.html", "운영"],
  ["rooms", "객실 현황", "rooms.html", "객실"],
  ["room-add", "객실 추가", "room-add.html", "객실"],
  ["rates", "요금 정책", "rates.html", "객실"],
  ["reservations", "예약 조회", "reservations.html", "예약/결제"],
  ["checkins", "체크인 현황", "checkins.html", "예약/결제"],
  ["payments", "결제 관리", "payments.html", "예약/결제"],
  ["customers", "고객 조회", "customers.html", "고객"],
  ["promotions", "프로모션", "promotions.html", "마케팅"],
  ["reviews-admin", "리뷰 관리", "reviews.html", "마케팅"],
  ["sales", "매출 분석", "sales.html", "리포트"],
  ["settlement", "정산 리포트", "settlement.html", "리포트"]
];

function userShell(active, body) {
  const currentUser = getCurrentUser();
  app.innerHTML = `
    <header class="topbar">
      <a class="brand" href="${rel}index.html"><span class="brand-mark">H</span><span>OmniStay</span></a>
      <nav class="nav">${userNav.filter(([key]) => key !== "profile" || currentUser).map(([key, label, href]) => `<a class="${active === key ? "active" : ""}" href="${rel}${href}">${label}</a>`).join("")}</nav>
      <div class="form-row">
        ${currentUser
          ? `<span class="pill">${escapeHtml(currentUser.name || currentUser.email || `User ${currentUser.userId}`)}</span><button class="btn" id="logoutBtn" type="button">로그아웃</button>`
          : `<a class="btn ${active === "login" ? "primary" : ""}" href="${rel}login.html">로그인</a><a class="btn ${active === "signup" ? "primary" : ""}" href="${rel}signup.html">회원가입</a>`}
      </div>
    </header>
    <main class="page">${body}</main>
  `;
  document.querySelector("#logoutBtn")?.addEventListener("click", () => {
    clearCurrentUser();
    window.google?.accounts?.id?.disableAutoSelect();
    location.href = `${rel}index.html`;
  });
}

async function adminShell(active, body) {
  const hotels = await safeLoadHotels();
  const selected = getHotelScope();
  const groups = [];
  let currentGroup = "";
  for (const [key, label, href, group] of adminNav) {
    if (group !== currentGroup) {
      currentGroup = group;
      groups.push(`<div class="side-group">${group}</div>`);
    }
    groups.push(`<a class="side-link ${active === key ? "active" : ""}" href="${href}"><span>${label}</span></a>`);
  }
  app.innerHTML = `
    <div class="admin-shell">
      <aside class="sidebar">
        <a class="brand" href="dashboard.html"><span class="brand-mark">A</span><span>관리자</span></a>
        <select class="hotel-switcher" id="hotelScope">
          <option value="">전체 호텔</option>
          ${hotels.map((hotel) => `<option value="${hotel.hotelId}" ${String(selected) === String(hotel.hotelId) ? "selected" : ""}>${escapeHtml(hotel.name)}</option>`).join("")}
        </select>
        ${groups.join("")}
      </aside>
      <main class="admin-main">${body}</main>
    </div>
  `;
  document.querySelector("#hotelScope")?.addEventListener("change", (event) => {
    localStorage.setItem("omnistayHotelScope", event.target.value);
    location.reload();
  });
}

function title(text, sub = "") {
  return `<div class="page-title"><div><h1>${text}</h1>${sub ? `<p class="muted">${sub}</p>` : ""}</div></div>`;
}

function empty(text, detail = "") {
  return `<div class="message">${text}${detail ? `<div class="small">${detail}</div>` : ""}</div>`;
}

function errorMessage(error) {
  return `<div class="message error">API 연결 실패: ${escapeHtml(error.message)}<div class="small">백엔드 서버가 켜져 있고 DB가 연결되어 있는지 확인하세요.</div></div>`;
}

function toast(message) {
  const node = document.createElement("div");
  node.className = "toast";
  node.textContent = message;
  document.body.appendChild(node);
  setTimeout(() => node.remove(), 2600);
}

function getHotelScope() {
  return localStorage.getItem("omnistayHotelScope") || "";
}

async function safeLoadHotels() {
  if (hotelsCache.length) return hotelsCache;
  try {
    hotelsCache = pageItems(await request("/api/hotels?size=100"));
  } catch {
    hotelsCache = [];
  }
  return hotelsCache;
}

async function eligibleReviewBookings(userId) {
  const [bookingResponses, payments, reviews] = await Promise.all([
    request(`/api/bookings/${userId}`).then(pageItems),
    request("/api/payment").then(pageItems),
    request("/api/review?size=200").then(pageItems)
  ]);
  const bookings = bookingResponses.map((response) => response.booking || response);
  const paidBookingIds = new Set(payments
    .filter((payment) => {
      const status = String(payment.paymentStatus ?? "").toLowerCase();
      return status === "paid" || status === "1";
    })
    .map((payment) => Number(paymentBookingId(payment))));
  const reviewedBookingIds = new Set(reviews
    .filter((review) => Number(review.userId) === Number(userId))
    .map((review) => Number(review.reservationId)));
  const candidates = bookings.filter((booking) => (
    !booking.cancelledAt
    && paidBookingIds.has(Number(booking.bookingId))
    && !reviewedBookingIds.has(Number(booking.bookingId))
  ));

  return Promise.all(candidates.map(async (booking) => {
    const roomId = Number(booking.roomId || booking.room?.roomId);
    let room = booking.room || null;
    if ((!room?.hotelId && !room?.hotel?.hotelId) || !room?.name) {
      room = await request(`/api/room/${roomId}`).catch(() => room || {});
    }
    const hotelId = Number(room?.hotelId || room?.hotel?.hotelId);
    let hotel = room?.hotel || null;
    if (!hotel?.name && hotelId) {
      hotel = await request(`/api/hotels/${hotelId}`).catch(() => hotel || {});
    }
    return {
      reservationId: Number(booking.bookingId),
      userId: Number(userId),
      hotelId,
      roomId,
      hotelName: hotel?.name || "호텔",
      roomName: room?.name || (room?.number ? `${room.number}호` : "객실"),
      checkinDate: booking.checkinDate,
      checkoutDate: booking.checkoutDate
    };
  }));
}

function hotelImageUrl(image) {
  if (!image?.hotelImageId) return "";
  return `/api/hotelimage/image/${encodeURIComponent(image.hotelImageId)}`;
}

async function loadHotelCover(hotel) {
  try {
    const images = pageItems(await request(`/api/hotelimage/hotel/${hotel.hotelId}?size=1`));
    return { ...hotel, coverImage: images[0] || null };
  } catch {
    return { ...hotel, coverImage: null };
  }
}

function loadHotelCovers(hotels) {
  return Promise.all(hotels.map(loadHotelCover));
}

function hotelCard(hotel) {
  const coverUrl = hotelImageUrl(hotel.coverImage);
  return `
    <article class="card">
      <div class="cover">${coverUrl
        ? `<img class="cover-image" src="${coverUrl}" alt="${escapeHtml(hotel.coverImage.fileName || hotel.name)}">`
        : escapeHtml(hotel.city || hotel.type || "HOTEL")}</div>
      <div class="card-body">
        <div class="toolbar" style="margin:0 0 8px">
          <h3>${escapeHtml(hotel.name)}</h3>
          <span class="pill">${hotel.starRate ?? "-"}성</span>
        </div>
        <p class="muted">${escapeHtml(hotel.address || hotel.description || "등록된 설명이 없습니다.")}</p>
        <div class="toolbar">
          <span class="small">체크인 ${escapeHtml(hotel.checkIn || "-")} / 체크아웃 ${escapeHtml(hotel.checkOut || "-")}</span>
          <a class="btn primary" href="hotel-detail.html?hotelId=${hotel.hotelId}">상세보기</a>
        </div>
      </div>
    </article>`;
}

function roomRow(room) {
  const amount = Number(room.basePrice || 0);
  const orderName = `${room.number || ""}호 ${room.name || "객실"} 예약`.trim();
  const bookingHref = `booking.html?hotelId=${encodeURIComponent(room.hotelId || "")}&roomId=${encodeURIComponent(room.roomId || "")}&amount=${encodeURIComponent(amount)}&orderName=${encodeURIComponent(orderName)}`;
  const amenityLabels = { wifi: "Wi-Fi", tv: "TV", bathtub: "욕조", cityView: "시티뷰", oceanView: "오션뷰", breakfastIncluded: "조식 포함", nonSmoking: "금연" };
  const amenities = Object.entries(amenityLabels).filter(([key]) => room.amenities?.[key]).map(([, label]) => label).join(" · ");
  return `<tr>
    <td>${room.thumbnailUrl ? `<img class="room-list-thumb" src="${escapeHtml(room.thumbnailUrl)}" alt="${escapeHtml(room.name || "객실")}">` : ""}<strong>${escapeHtml(room.number)}</strong><div class="small muted">${escapeHtml(room.name)}</div>${amenities ? `<div class="small muted">${escapeHtml(amenities)}</div>` : ""}</td>
    <td>${escapeHtml(room.floor)}층</td>
    <td>${escapeHtml(room.roomType)} / ${escapeHtml(room.roomBedOption || "-")}</td>
    <td>${room.maxAdult ?? 0}명 + 아동 ${room.maxChild ?? 0}명</td>
    <td>${money(room.basePrice)}</td>
    <td>${statusBadge(room.roomStatus)}</td>
    <td><a class="btn" href="${bookingHref}">예약</a></td>
  </tr>`;
}

function statusBadge(status) {
  const cls = status === "EnableReservation" ? "ok" : status === "Construct" ? "warn" : "bad";
  const label = status === "EnableReservation" ? "예약가능" : status === "Construct" ? "공사중" : "예약중지";
  return `<span class="status ${cls}">${label}</span>`;
}

function screenOnlyBadge() {
  return `<span class="status warn">화면 전용</span>`;
}

function todayDate() {
  return new Date().toISOString().slice(0, 10);
}


export {
  app,
  page,
  rel,
  TOSS_SAMPLE_CLIENT_KEY,
  getCurrentUser,
  setCurrentUser,
  setAuthTokens,
  clearCurrentUser,
  hasAdminRole,
  hasAccessToken,
  safeRedirect,
  loadTossPaymentsSdk,
  tossMethodToPaymentMethod,
  paymentBookingId,
  userShell,
  adminShell,
  title,
  empty,
  errorMessage,
  toast,
  getHotelScope,
  safeLoadHotels,
  eligibleReviewBookings,
  hotelImageUrl,
  loadHotelCover,
  loadHotelCovers,
  hotelCard,
  roomRow,
  statusBadge,
  screenOnlyBadge,
  todayDate
};
