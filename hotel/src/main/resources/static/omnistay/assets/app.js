import { request, pageItems, qs, money, escapeHtml, todayMonth } from "./api.js";

const app = document.querySelector("#app");
const page = document.body.dataset.page;
const rel = document.body.dataset.area === "admin" ? "../" : "";
const TOSS_SDK_URL = "https://js.tosspayments.com/v2/standard";
const TOSS_SAMPLE_CLIENT_KEY = "test_ck_D5GePWvyJnrK0W0k6q8gLzN97Eoq";
let hotelsCache = [];
let tossSdkPromise = null;
let googleIdentitySdkPromise = null;

const userNav = [
  ["home", "홈", "index.html"],
  ["search", "숙소 검색", "search.html"],
  ["bookings", "예약내역", "bookings.html"],
  ["coupons", "쿠폰함", "coupons.html"],
  ["reviews", "리뷰", "reviews.html"]
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

function clearCurrentUser() {
  localStorage.removeItem("omnistayCurrentUser");
}

function safeRedirect(defaultPage = "index.html") {
  const redirect = new URLSearchParams(location.search).get("redirect");
  if (!redirect || redirect.startsWith("//") || /^[a-z][a-z0-9+.-]*:/i.test(redirect)) {
    return defaultPage;
  }
  return redirect;
}

function setAuthStatus(selector, message, type = "") {
  const node = document.querySelector(selector);
  if (!node) return;
  node.className = `auth-status ${type}`.trim();
  node.textContent = message;
}

function loadGoogleIdentitySdk() {
  if (window.google?.accounts?.id) return Promise.resolve(window.google);
  if (googleIdentitySdkPromise) return googleIdentitySdkPromise;
  googleIdentitySdkPromise = new Promise((resolve, reject) => {
    const script = document.createElement("script");
    script.src = "https://accounts.google.com/gsi/client?hl=ko";
    script.async = true;
    script.onload = () => resolve(window.google);
    script.onerror = () => reject(new Error("Google 로그인 SDK를 불러오지 못했습니다."));
    document.head.appendChild(script);
  });
  return googleIdentitySdkPromise;
}

function renderGoogleSetupButton(buttonSelector, statusSelector, message) {
  const container = document.querySelector(buttonSelector);
  if (!container) return;
  container.hidden = false;
  container.innerHTML = `<button class="google-login-fallback" type="button"><span class="google-login-mark">G</span><span>Google로 로그인</span></button>`;
  container.querySelector("button").addEventListener("click", () => {
    setAuthStatus(statusSelector, message, "warn");
  });
  setAuthStatus(statusSelector, message, "warn");
}

async function initGoogleLogin(buttonSelector, statusSelector, buttonText = "signin_with") {
  try {
    const config = await request("/api/auth/config");
    if (!config.googleLoginEnabled || !config.googleClientId) {
      renderGoogleSetupButton(
        buttonSelector,
        statusSelector,
        "Google 로그인을 사용하려면 서버의 GOOGLE_CLIENT_ID 설정이 필요합니다."
      );
      return;
    }

    await loadGoogleIdentitySdk();
    const container = document.querySelector(buttonSelector);
    container.hidden = false;
    container.innerHTML = "";
    window.google.accounts.id.initialize({
      client_id: config.googleClientId,
      callback: async ({ credential }) => {
        setAuthStatus(statusSelector, "Google 계정을 확인하는 중입니다.");
        try {
          const user = await request("/api/auth/google", {
            method: "POST",
            body: JSON.stringify({ credential })
          });
          setCurrentUser(user);
          toast("Google 계정으로 로그인되었습니다.");
          location.href = safeRedirect();
        } catch (error) {
          setAuthStatus(statusSelector, error.message, "error");
        }
      }
    });
    window.google.accounts.id.renderButton(
      document.querySelector(buttonSelector),
      { theme: "outline", size: "large", shape: "rectangular", text: buttonText, width: 320 }
    );
    setAuthStatus(statusSelector, "");
  } catch (error) {
    renderGoogleSetupButton(buttonSelector, statusSelector, error.message);
  }
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
      <nav class="nav">${userNav.map(([key, label, href]) => `<a class="${active === key ? "active" : ""}" href="${rel}${href}">${label}</a>`).join("")}</nav>
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

function hotelCard(hotel) {
  return `
    <article class="card">
      <div class="cover">${escapeHtml(hotel.city || hotel.type || "HOTEL")}</div>
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
  return `<tr>
    <td><strong>${escapeHtml(room.number)}</strong><div class="small muted">${escapeHtml(room.name)}</div></td>
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

async function homePage() {
  userShell("home", `
    <section class="hero">
      <h1>여러 호텔 예약을 한 곳에서</h1>
      <p>등록된 호텔 데이터를 불러와 검색, 상세, 예약 화면으로 연결합니다.</p>
      <form class="filters" id="homeSearch">
        <input name="keyword" placeholder="호텔명 또는 지역">
        <input name="checkin" type="date">
        <input name="checkout" type="date">
        <button class="btn primary">검색</button>
      </form>
    </section>
    <section class="section" id="hotels">${empty("호텔 데이터를 불러오는 중입니다.")}</section>
  `);
  document.querySelector("#homeSearch").addEventListener("submit", (event) => {
    event.preventDefault();
    const data = qs(event.currentTarget);
    location.href = `search.html?keyword=${encodeURIComponent(data.keyword || "")}&checkin=${data.checkin || ""}&checkout=${data.checkout || ""}`;
  });
  try {
    const hotels = pageItems(await request("/api/hotels?size=6"));
    document.querySelector("#hotels").innerHTML = `<div class="toolbar"><h2>등록 호텔</h2><a class="btn" href="search.html">전체 보기</a></div>${hotels.length ? `<div class="grid cols-3">${hotels.map(hotelCard).join("")}</div>` : empty("DB에 등록된 호텔이 없습니다.", "관리자 호텔 관리 또는 seed.html에서 데이터를 추가하세요.")}`;
  } catch (error) {
    document.querySelector("#hotels").innerHTML = errorMessage(error);
  }
}

async function searchPage() {
  userShell("search", `${title("숙소 검색", "DB에 등록된 호텔 목록을 조회합니다.")}<div class="filters"><input id="keyword" placeholder="호텔명/지역 검색"><button class="btn primary" id="filterBtn">검색</button></div><section class="section" id="results">${empty("불러오는 중입니다.")}</section>`);
  const keyword = new URLSearchParams(location.search).get("keyword") || "";
  document.querySelector("#keyword").value = keyword;
  async function load() {
    try {
      const q = document.querySelector("#keyword").value.trim().toLowerCase();
      const hotels = pageItems(await request("/api/hotels?size=100")).filter((hotel) => !q || `${hotel.name} ${hotel.city} ${hotel.address}`.toLowerCase().includes(q));
      document.querySelector("#results").innerHTML = hotels.length ? `<div class="grid cols-3">${hotels.map(hotelCard).join("")}</div>` : empty("검색 결과가 없습니다.");
    } catch (error) {
      document.querySelector("#results").innerHTML = errorMessage(error);
    }
  }
  document.querySelector("#filterBtn").addEventListener("click", load);
  load();
}

async function detailPage() {
  userShell("search", `<section id="detail">${empty("호텔 상세를 불러오는 중입니다.")}</section>`);
  try {
    const hotels = await safeLoadHotels();
    const id = new URLSearchParams(location.search).get("hotelId") || hotels[0]?.hotelId;
    if (!id) {
      document.querySelector("#detail").innerHTML = empty("표시할 호텔이 없습니다.");
      return;
    }
    const [hotel, roomsRes, amenRes, transRes, reviewsRes, imageRes] = await Promise.all([
      request(`/api/hotels/${id}`),
      request(`/api/room/hotel/${id}?size=100`),
      request(`/api/hotelamenities/hotel/${id}?size=10`).catch(() => null),
      request(`/api/hoteltrans/hotel/${id}?size=20`).catch(() => null),
      request(`/api/review/hotel/${id}?size=20`).catch(() => null),
      request(`/api/hotelimage/hotel/${id}?size=20`).catch(() => null)
    ]);
    const rooms = pageItems(roomsRes);
    const amens = pageItems(amenRes);
    const trans = pageItems(transRes);
    const reviews = pageItems(reviewsRes);
    const images = pageItems(imageRes);
    document.querySelector("#detail").innerHTML = `
      <div class="hero"><h1>${escapeHtml(hotel.name)}</h1><p>${escapeHtml(hotel.description || hotel.address || "")}</p></div>
      ${images.length ? `<section class="section grid cols-3">${images.map((image) => `<article class="card"><img class="cover-img" src="/api/hotelimage/image/${image.hotelImageId}" alt="${escapeHtml(image.fileName || hotel.name)}"></article>`).join("")}</section>` : ""}
      <section class="section grid cols-4">
        <div class="metric">지역<strong>${escapeHtml(hotel.city || "-")}</strong></div>
        <div class="metric">등급<strong>${hotel.starRate ?? "-"}성</strong></div>
        <div class="metric">체크인<strong>${escapeHtml(hotel.checkIn || "-")}</strong></div>
        <div class="metric">체크아웃<strong>${escapeHtml(hotel.checkOut || "-")}</strong></div>
      </section>
      <section class="section card"><div class="card-body"><h2>위치 / 교통</h2><p class="muted">${escapeHtml(hotel.address || "-")} ${hotel.latitude && hotel.longitude ? `(${hotel.latitude}, ${hotel.longitude})` : ""}</p>${trans.length ? trans.map((item) => `<span class="pill">${escapeHtml(item.name)} ${escapeHtml(item.time || "")} ${escapeHtml(item.depart || "")}</span>`).join(" ") : empty("등록된 교통 정보가 없습니다.")}</div></section>
      <section class="section card"><div class="card-body"><h2>편의시설</h2>${amens.length ? renderAmenities(amens[0]) : empty("등록된 편의시설이 없습니다.")}</div></section>
      <section class="section"><div class="toolbar"><h2>객실</h2></div><div class="table-wrap"><table><thead><tr><th>호실</th><th>층</th><th>타입</th><th>인원</th><th>기본가</th><th>상태</th><th></th></tr></thead><tbody>${rooms.map(roomRow).join("") || `<tr><td colspan="7">등록된 객실이 없습니다.</td></tr>`}</tbody></table></div></section>
      <section class="section"><div class="toolbar"><h2>객실 리뷰</h2><a class="btn" href="reviews.html?hotelId=${id}">리뷰 작성</a></div>${reviews.length ? `<div class="grid cols-2">${reviews.map(reviewCard).join("")}</div>` : empty("등록된 리뷰가 없습니다.")}</section>
    `;
  } catch (error) {
    document.querySelector("#detail").innerHTML = errorMessage(error);
  }
}

function renderAmenities(amen) {
  const labels = {
    wifi: "와이파이", pool: "수영장", fitnessCenter: "피트니스", spa: "스파",
    restaurant: "레스토랑", valetParking: "발렛", freeParking: "무료주차",
    concierge: "컨시어지", bar: "바", breakfast: "조식", airportShuttle: "공항셔틀",
    roomService: "룸서비스", laundry: "세탁", lounge: "라운지", sauna: "사우나",
    freeCancel: "무료취소", petFriendly: "반려동물"
  };
  const tags = Object.entries(labels).filter(([key]) => amen[key]).map(([, label]) => `<span class="pill">${label}</span>`);
  return tags.join(" ") || empty("선택된 편의시설 태그가 없습니다.");
}

function reviewCard(review, action = "") {
  const photos = Array.isArray(review.photos) ? review.photos : [];
  return `<article class="card"><div class="card-body">
    <div class="toolbar" style="margin:0 0 8px"><h3>${escapeHtml(review.title)}</h3><span class="pill">${review.overallRating ?? "-"}점</span></div>
    <p class="muted">${escapeHtml(review.content)}</p>
    <div class="small">${escapeHtml(review.userName || `user ${review.userId}`)} · ${escapeHtml(review.roomName || "")}</div>
    ${photos.length ? `<div class="form-row">${photos.map((p) => `<span class="pill">첨부 ${escapeHtml(p.photoPath)}</span>`).join("")}</div>` : ""}
    ${action}
  </div></article>`;
}

async function bookingPage() {
  userShell("bookings", `${title("예약하기", "예약 등록 API 저장")}<div id="bookingArea">${empty("객실 정보를 불러오는 중입니다.")}</div>`);
  const currentUser = getCurrentUser();
  if (!currentUser) {
    const redirect = encodeURIComponent(`booking.html${location.search}`);
    location.href = `login.html?reason=booking&redirect=${redirect}`;
    return;
  }
  try {
    const hotels = await safeLoadHotels();
    const params = new URLSearchParams(location.search);
    const selectedHotel = params.get("hotelId") || hotels[0]?.hotelId || "";
    const rooms = selectedHotel ? pageItems(await request(`/api/room/hotel/${selectedHotel}?size=100`)) : [];
    const selectedRoom = params.get("roomId") || rooms[0]?.roomId || "";
    const selectedRoomData = rooms.find((room) => String(room.roomId) === String(selectedRoom)) || rooms[0] || {};
    const baseAmount = Number(params.get("amount") || selectedRoomData.basePrice || 0);
    const orderName = params.get("orderName") || `${selectedRoomData.number || ""}호 ${selectedRoomData.name || "객실"} 예약`.trim();
    const today = todayDate();
    document.querySelector("#bookingArea").innerHTML = `
      <form class="card card-body grid" id="bookingForm">
        <div class="message">
          로그인 사용자 정보로 예약합니다.
          <div class="small">${escapeHtml(currentUser.name || "-")} · ${escapeHtml(currentUser.phone || "-")} · ${escapeHtml(currentUser.email || "-")}</div>
        </div>
        <div class="grid cols-2">
          <label><span>호텔</span><select name="hotelId" id="hotelSelect">${hotels.map((h) => `<option value="${h.hotelId}" ${String(h.hotelId) === String(selectedHotel) ? "selected" : ""}>${escapeHtml(h.name)}</option>`).join("")}</select></label>
          <label><span>객실</span><select name="roomId">${rooms.map((r) => `<option value="${r.roomId}" ${String(r.roomId) === String(selectedRoom) ? "selected" : ""}>${escapeHtml(r.number)}호 ${escapeHtml(r.name)}</option>`).join("")}</select></label>
          <label><span>체크인</span><input name="checkinDate" type="date" min="${today}" required></label>
          <label><span>체크아웃</span><input name="checkoutDate" type="date" min="${today}" required></label>
          <label><span>성인</span><input name="adultCount" type="number" value="2" min="1"></label>
          <label><span>아동</span><input name="childCount" type="number" value="0" min="0"></label>
        </div>
        <input name="baseAmount" type="hidden" value="${escapeHtml(baseAmount)}">
        <input name="orderName" type="hidden" value="${escapeHtml(orderName)}">
        <label><span>요청사항</span><textarea name="specialRequest"></textarea></label>
        <button class="btn primary">결제로 이동</button>
      </form>
      <div class="section" id="bookingResult"></div>
    `;
    document.querySelector("#hotelSelect").addEventListener("change", (event) => {
      location.href = `booking.html?hotelId=${event.target.value}`;
    });
    const checkinInput = document.querySelector("[name='checkinDate']");
    const checkoutInput = document.querySelector("[name='checkoutDate']");
    checkinInput.addEventListener("change", () => {
      checkoutInput.min = checkinInput.value || today;
      if (checkoutInput.value && checkoutInput.value < checkoutInput.min) {
        checkoutInput.value = "";
      }
    });
    document.querySelector("#bookingForm").addEventListener("submit", submitBooking);
  } catch (error) {
    document.querySelector("#bookingArea").innerHTML = errorMessage(error);
  }
}

async function submitBooking(event) {
  event.preventDefault();
  const form = event.currentTarget;
  const currentUser = getCurrentUser();
  if (!currentUser) {
    const redirect = encodeURIComponent(`booking.html${location.search}`);
    location.href = `login.html?reason=booking&redirect=${redirect}`;
    return;
  }
  const data = qs(form);
  const roomText = form.elements.roomId?.selectedOptions?.[0]?.textContent.trim() || "";
  const today = todayDate();
  if (data.checkinDate < today) {
    document.querySelector("#bookingResult").innerHTML = `<div class="message error">체크인 날짜는 오늘(${today}) 이전으로 선택할 수 없습니다.</div>`;
    return;
  }
  if (data.checkoutDate <= data.checkinDate) {
    document.querySelector("#bookingResult").innerHTML = `<div class="message error">체크아웃 날짜는 체크인 날짜 이후로 선택해야 합니다.</div>`;
    return;
  }
  data.userId = Number(currentUser.userId);
  data.guestName = currentUser.name || currentUser.email || `User ${currentUser.userId}`;
  data.guestPhone = currentUser.phone || "";
  data.guestEmail = currentUser.email || "";
  data.roomId = Number(data.roomId);
  data.adultCount = Number(data.adultCount || 0);
  data.childCount = Number(data.childCount || 0);
  data.nationality = "KOREA";
  const params = new URLSearchParams(location.search);
  const paymentBaseAmount = Number(data.baseAmount || params.get("amount") || 0);
  const paymentOrderName = data.orderName || params.get("orderName") || "";
  delete data.baseAmount;
  delete data.orderName;
  try {
    document.querySelector("#bookingResult").innerHTML = empty("예약 정보를 저장하는 중입니다.");
    const booking = await request("/api/bookings/insert", { method: "POST", body: JSON.stringify(data) });
    const nights = Math.max(1, Math.ceil((new Date(data.checkoutDate) - new Date(data.checkinDate)) / 86400000));
    const paymentAmount = paymentBaseAmount * nights;
    const orderName = paymentOrderName || `${roomText} 예약`.trim() || "OmniStay 호텔 예약";
    if (!booking.bookingNo) {
      throw new Error("예약번호가 생성되지 않아 결제를 진행할 수 없습니다.");
    }
    const paymentParams = new URLSearchParams({
      bookingId: String(booking.bookingId),
      bookingNo: String(booking.bookingNo),
      amount: String(paymentAmount),
      orderName
    });
    location.href = `payment.html?${paymentParams.toString()}`;
  } catch (error) {
    document.querySelector("#bookingResult").innerHTML = errorMessage(error);
  }
}

async function paymentPage() {
  const currentUser = getCurrentUser();
  const params = new URLSearchParams(location.search);
  const bookingId = params.get("bookingId") || "";
  const bookingNo = params.get("bookingNo") || "";
  const totalAmount = Number(params.get("amount") || params.get("totalAmount") || 0);
  const orderName = params.get("orderName") || "OmniStay 호텔 예약";
  const customerName = currentUser?.name || currentUser?.email || "비회원";
  const customerEmail = currentUser?.email || "";
  const customerPhone = (currentUser?.phone || "").replaceAll("-", "");
  const couponNote = currentUser ? "" : `<div class="toss-note">로그인하지 않아도 결제할 수 있습니다. 쿠폰은 로그인 회원에게만 표시됩니다.</div>`;
  userShell("bookings", `<section class="toss-page"><div class="toss-wrapper"><form class="toss-box" id="paymentForm"><h1>일반 결제</h1><input name="bookingId" type="hidden" value="${escapeHtml(bookingId)}"><input name="bookingNo" type="hidden" value="${escapeHtml(bookingNo)}"><input name="totalAmount" type="hidden" value="${escapeHtml(totalAmount)}"><input name="orderName" type="hidden" value="${escapeHtml(orderName)}"><input name="customerName" type="hidden" value="${escapeHtml(customerName)}"><input name="customerEmail" type="hidden" value="${escapeHtml(customerEmail)}"><input name="customerMobilePhone" type="hidden" value="${escapeHtml(customerPhone)}"><div class="toss-summary"><div class="toss-row"><span>결제자</span><strong>${escapeHtml(customerName)}</strong></div><div class="toss-row"><span>예약 금액</span><strong>${totalAmount ? money(totalAmount) : "예약 금액 없음"}</strong></div><div class="toss-row"><span>쿠폰 할인</span><strong id="paymentDiscount">${money(0)}</strong></div><div class="toss-row total"><span>최종 결제금액</span><strong id="paymentFinalAmount">${totalAmount ? money(totalAmount) : "-"}</strong></div></div><label class="toss-field"><span>쿠폰 선택</span><select name="userCouponId" id="paymentCouponSelect"><option value="">사용 안 함</option></select></label>${couponNote}<div class="toss-methods" id="payment-method"><button class="toss-method active" type="button" data-payment-method="CARD">카드</button><button class="toss-method" type="button" data-payment-method="TRANSFER">계좌이체</button><button class="toss-method" type="button" data-payment-method="VIRTUAL_ACCOUNT">가상계좌</button><button class="toss-method" type="button" data-payment-method="MOBILE_PHONE">휴대폰</button><button class="toss-method" type="button" data-payment-method="CULTURE_GIFT_CERTIFICATE">문화상품권</button></div><button class="toss-button" id="openTossPayment" type="submit">결제하기</button><div class="toss-note" id="tossPaymentStatus">토스페이먼츠 샘플 결제창을 사용합니다. 결제 금액은 예약 정보에서 자동으로 들어갑니다.</div></form></div></section>`);
  if (currentUser?.userId) {
    await loadPaymentCoupons(currentUser.userId);
  }
  let selectedPaymentMethod = "CARD";
  const couponSelect = document.querySelector("#paymentCouponSelect");
  const discountEl = document.querySelector("#paymentDiscount");
  const finalAmountEl = document.querySelector("#paymentFinalAmount");
  const statusEl = document.querySelector("#tossPaymentStatus");
  const calculateDiscount = () => {
    const option = couponSelect.selectedOptions?.[0];
    if (!option?.value || !totalAmount) return 0;
    const minOrder = Number(option.dataset.minOrder || 0);
    if (minOrder && totalAmount < minOrder) return 0;
    const discountValue = Number(option.dataset.discountValue || 0);
    const discountType = option.dataset.discountType || "FIXED";
    const maxDiscount = Number(option.dataset.maxDiscount || 0);
    let discount = discountType === "RATE" ? Math.floor(totalAmount * discountValue / 100) : discountValue;
    if (maxDiscount > 0) discount = Math.min(discount, maxDiscount);
    return Math.max(0, Math.min(totalAmount, discount));
  };
  const updateAmount = () => {
    const discount = calculateDiscount();
    const finalAmount = Math.max(totalAmount - discount, 0);
    discountEl.textContent = money(discount);
    finalAmountEl.textContent = totalAmount ? money(finalAmount) : "-";
    return { discount, finalAmount };
  };
  couponSelect.addEventListener("change", updateAmount);
  updateAmount();
  document.querySelectorAll("[data-payment-method]").forEach((button) => {
    button.addEventListener("click", () => {
      selectedPaymentMethod = button.dataset.paymentMethod;
      document.querySelectorAll("[data-payment-method]").forEach((item) => item.classList.remove("active"));
      button.classList.add("active");
    });
  });
  document.querySelector("#paymentForm").addEventListener("submit", async (event) => {
    event.preventDefault();
    const data = qs(event.currentTarget);
    const { discount, finalAmount } = updateAmount();
    if (!data.bookingId || !data.bookingNo || !totalAmount || finalAmount <= 0) {
      statusEl.className = "message error toss-note";
      statusEl.textContent = "예약번호와 결제 금액이 없습니다. 예약에서 결제 화면으로 이동해주세요.";
      return;
    }
    try {
      const TossPayments = await loadTossPaymentsSdk();
      const customerKey = currentUser?.userId ? `USER_${currentUser.userId}` : TossPayments.ANONYMOUS;
      const payment = TossPayments(window.OMNISTAY_TOSS_CLIENT_KEY || TOSS_SAMPLE_CLIENT_KEY).payment({ customerKey });
      const orderId = data.bookingNo;
      statusEl.className = "toss-note";
      statusEl.textContent = "결제 정보를 DB에 먼저 저장하는 중입니다.";
      const existingPayments = pageItems(await request("/api/payment"));
      const existingPayment = existingPayments.find((item) => String(paymentBookingId(item)) === String(data.bookingId));
      if (existingPayment?.paymentStatus === "Paid") {
        statusEl.className = "message error toss-note";
        statusEl.textContent = "이미 결제가 완료된 예약입니다.";
        return;
      }
      const couponId = discount > 0
        ? Number(couponSelect.selectedOptions?.[0]?.dataset.couponId || 0)
        : 0;
      const draftPayload = {
        ...(existingPayment || {}),
        paymentId: existingPayment?.paymentId,
        bookingId: Number(data.bookingId),
        booking: { bookingId: Number(data.bookingId) },
        transactionNum: orderId,
        paymentMethod: tossMethodToPaymentMethod(selectedPaymentMethod),
        paymentStatus: "Ready",
        totalAmount: finalAmount,
        currency: "KRW",
        couponId,
        usedPoint: existingPayment?.usedPoint || 0,
        discountAmount: discount,
        orderId,
        paymentKey: null,
        provider: "TOSS"
      };
      const draftPayment = await request("/api/payment", {
        method: existingPayment?.paymentId ? "PATCH" : "POST",
        body: JSON.stringify(draftPayload)
      });
      const query = new URLSearchParams({
        bookingId: data.bookingId,
        paymentId: String(draftPayment.paymentId || ""),
        userCouponId: data.userCouponId || "",
        discountAmount: String(discount)
      });
      const paymentRequest = {
        method: selectedPaymentMethod,
        amount: {
          currency: "KRW",
          value: finalAmount
        },
        orderId,
        orderName: data.orderName,
        successUrl: `${location.origin}/payment-success.html?${query.toString()}`,
        failUrl: `${location.origin}/payment-fail.html?${query.toString()}`,
        customerEmail: data.customerEmail,
        customerName: data.customerName,
        customerMobilePhone: data.customerMobilePhone
      };
      if (selectedPaymentMethod === "CARD") {
        paymentRequest.card = { useEscrow: false, flowMode: "DEFAULT", useCardPoint: false, useAppCardOnly: false };
      }
      if (selectedPaymentMethod === "TRANSFER") {
        paymentRequest.transfer = { cashReceipt: { type: "소득공제" }, useEscrow: false };
      }
      if (selectedPaymentMethod === "VIRTUAL_ACCOUNT") {
        paymentRequest.virtualAccount = { cashReceipt: { type: "소득공제" }, useEscrow: false, validHours: 24 };
      }
      statusEl.className = "toss-note";
      statusEl.textContent = "토스 결제창을 여는 중입니다.";
      await payment.requestPayment(paymentRequest);
    } catch (error) {
      statusEl.className = "message error toss-note";
      statusEl.textContent = error.message || String(error);
    }
  });
}

async function loadPaymentCoupons(userId) {
  try {
    const userCoupons = pageItems(await request("/api/usercoupons?size=100"))
      .filter((item) => String(item.userId || item.user?.userId) === String(userId))
      .filter((item) => !item.userCouponStatus || item.userCouponStatus === "AVAILABLE");
    document.querySelector("#paymentCouponSelect").innerHTML = `<option value="">사용 안 함</option>${userCoupons.map((item) => `<option value="${item.userCouponId}" data-coupon-id="${escapeHtml(item.couponId || item.coupon?.couponId || 0)}" data-discount-type="${escapeHtml(item.coupon?.discountType || "FIXED")}" data-discount-value="${escapeHtml(item.coupon?.discountValue || 0)}" data-min-order="${escapeHtml(item.coupon?.minOrder || 0)}" data-max-discount="${escapeHtml(item.coupon?.maxDiscount || 0)}">${escapeHtml(item.coupon?.name || `쿠폰 ${item.couponId}`)}</option>`).join("")}`;
  } catch {
    document.querySelector("#paymentCouponSelect").innerHTML = `<option value="">쿠폰 불러오기 실패</option>`;
  }
}

async function paymentResultPage(status) {
  const params = new URLSearchParams(location.search);
  const orderId = params.get("orderId") || "-";
  const paymentKey = params.get("paymentKey") || "-";
  const amount = params.get("amount") || "-";
  const bookingId = params.get("bookingId") || "";
  const paymentId = params.get("paymentId") || "";
  const code = params.get("code") || "";
  const message = params.get("message") || "";
  const isSuccess = status === "success";
  userShell("bookings", `<section class="toss-page"><div class="toss-wrapper"><div class="toss-box"><img class="toss-result-image" src="${isSuccess ? "https://static.toss.im/illusts/check-blue-spot-ending-frame.png" : "https://static.toss.im/lotties/error-spot-no-loop-space-apng.png"}" alt=""><h2>${isSuccess ? "결제를 완료했어요" : "결제를 실패했어요"}</h2><div class="toss-result-grid">${isSuccess ? `<div class="toss-row"><span><b>결제금액</b></span><strong>${escapeHtml(amount)}원</strong></div><div class="toss-row"><span><b>주문번호</b></span><strong class="toss-break">${escapeHtml(orderId)}</strong></div><div class="toss-row"><span><b>paymentKey</b></span><strong class="toss-break">${escapeHtml(paymentKey)}</strong></div>` : `<div class="toss-row"><span><b>에러메시지</b></span><strong class="toss-break">${escapeHtml(message || "-")}</strong></div><div class="toss-row"><span><b>에러코드</b></span><strong class="toss-break">${escapeHtml(code || "-")}</strong></div>`}</div><div class="toss-note" id="paymentConfirmResult">${isSuccess ? "결제 승인 확인 중입니다." : "결제 실패 정보를 저장하는 중입니다."}</div><a class="toss-button" style="display:inline-flex;align-items:center;justify-content:center;text-decoration:none" href="bookings.html">예약내역</a></div></div></section>`);
  try {
    if (isSuccess) {
      if (!bookingId || !params.get("paymentKey") || !params.get("orderId") || !params.get("amount")) {
        document.querySelector("#paymentConfirmResult").className = "message error toss-note";
        document.querySelector("#paymentConfirmResult").textContent = "토스 승인에 필요한 값이 부족합니다.";
        return;
      }
      const result = await request("/api/payment/toss/confirm", { method: "POST", body: JSON.stringify({ bookingId: Number(bookingId), paymentKey: params.get("paymentKey"), orderId: params.get("orderId"), amount: Number(params.get("amount")) }) });
      const saved = await request("/api/payment", {
        method: "PATCH",
        body: JSON.stringify({
          ...result,
          paymentId: result.paymentId || Number(paymentId),
          bookingId: Number(bookingId),
          booking: { bookingId: Number(bookingId) },
          paymentStatus: "Paid",
          totalAmount: Number(params.get("amount")),
          currency: result.currency || "KRW",
          orderId: params.get("orderId"),
          paymentKey: params.get("paymentKey"),
          provider: "TOSS"
        })
      });
      document.querySelector("#paymentConfirmResult").textContent = `결제 승인이 저장되었습니다. 결제 ID: ${saved.paymentId || result.paymentId || "-"}`;
    } else {
      if (!params.get("orderId")) {
        document.querySelector("#paymentConfirmResult").className = "message error toss-note";
        document.querySelector("#paymentConfirmResult").textContent = "토스 실패 저장에 필요한 orderId가 없습니다.";
        return;
      }
      const result = await request("/api/payment/toss/fail", { method: "POST", body: JSON.stringify({ orderId: params.get("orderId"), code, message }) });
      await request("/api/payment", {
        method: "PATCH",
        body: JSON.stringify({
          ...result,
          paymentId: result.paymentId || Number(paymentId),
          bookingId: result.bookingId || (bookingId ? Number(bookingId) : null),
          booking: result.bookingId || bookingId ? { bookingId: result.bookingId || Number(bookingId) } : undefined,
          paymentStatus: "Failed",
          orderId: params.get("orderId"),
          failCode: code,
          failMessage: message
        })
      });
      document.querySelector("#paymentConfirmResult").textContent = "결제 실패 정보가 저장되었습니다.";
    }
  } catch (error) {
    document.querySelector("#paymentConfirmResult").className = "message error toss-note";
    document.querySelector("#paymentConfirmResult").textContent = error.message || String(error);
  }
}

async function loadPayments(selector, admin = false) {
  try {
    const payments = pageItems(await request("/api/payment"));
    document.querySelector(selector).innerHTML = payments.length ? `<div class="table-wrap"><table><thead><tr><th>ID</th><th>예약</th><th>상태</th><th>금액</th><th>수단</th>${admin ? "<th></th>" : ""}</tr></thead><tbody>${payments.map((p) => `<tr><td>${p.paymentId}</td><td>${p.bookingId || p.booking?.bookingId || "-"}</td><td>${escapeHtml(p.paymentStatus)}</td><td>${money(p.totalAmount)}</td><td>${escapeHtml(p.paymentMethod)}</td>${admin ? `<td><button class="btn danger" data-delete-payment="${p.paymentId}">삭제</button></td>` : ""}</tr>`).join("")}</tbody></table></div>` : empty("등록된 결제가 없습니다.");
    document.querySelectorAll("[data-delete-payment]").forEach((btn) => btn.addEventListener("click", async () => {
      await request(`/api/payment/${btn.dataset.deletePayment}`, { method: "DELETE" });
      toast("삭제되었습니다.");
      loadPayments(selector, admin);
    }));
  } catch (error) {
    document.querySelector(selector).innerHTML = errorMessage(error);
  }
}

async function bookingsPage() {
  const currentUser = getCurrentUser();
  if (!currentUser?.userId) {
    const redirect = encodeURIComponent("bookings.html");
    location.href = `login.html?reason=bookings&redirect=${redirect}`;
    return;
  }
  userShell("bookings", `${title("내 예약내역", `${currentUser.name || currentUser.email || "로그인 회원"}님의 예약을 조회합니다.`)}<section class="section" id="bookingList">${empty("예약 내역을 불러오는 중입니다.")}</section><section class="section card"><div class="card-body"><div class="toolbar" style="margin:0 0 10px"><h2>예약 상세 흐름</h2>${screenOnlyBadge()}</div><div class="grid cols-3"><div class="metric">예약 접수<strong>일정/인원</strong></div><div class="metric">결제 대기<strong>쿠폰 선택</strong></div><div class="metric">투숙 완료<strong>리뷰 작성</strong></div></div></div></section>`);
  await loadBookings("#bookingList", currentUser.userId, false);
}

async function couponsPage() {
  const currentUser = getCurrentUser();
  if (!currentUser) {
    const redirect = encodeURIComponent("coupons.html");
    location.href = `login.html?reason=coupons&redirect=${redirect}`;
    return;
  }
  userShell("coupons", `${title("쿠폰함", "관리자가 발급한 내 쿠폰과 전체 쿠폰 정보를 조회합니다.")}<section class="grid cols-2"><section class="card card-body"><div class="toolbar" style="margin:0 0 10px"><h2>내 쿠폰</h2><span class="status ok">API 연결</span></div><div id="myCoupons">${empty("쿠폰을 불러오는 중입니다.")}</div></section><section class="card card-body"><div class="toolbar" style="margin:0 0 10px"><h2>쿠폰 안내</h2><span class="status ok">조회 전용</span></div><div id="couponCatalog">${empty("쿠폰을 불러오는 중입니다.")}</div></section></section>`);
  await loadCoupons(currentUser.userId);
}

async function loadCoupons(userId) {
  try {
    const [coupons, userCoupons] = await Promise.all([
      request("/api/coupons?size=100").then(pageItems),
      request("/api/usercoupons?size=100").then(pageItems)
    ]);
    const mine = userCoupons.filter((item) => String(item.userId || item.user?.userId) === String(userId));
    document.querySelector("#myCoupons").innerHTML = mine.length ? `<div class="table-wrap"><table><thead><tr><th>쿠폰</th><th>상태</th><th>발급일</th><th>사용일</th><th>결제 ID</th></tr></thead><tbody>${mine.map((item) => `<tr><td>${escapeHtml(item.coupon?.name || item.couponId || "-")}</td><td>${escapeHtml(item.userCouponStatus || "-")}</td><td>${escapeHtml(item.issuedAt || "-")}</td><td>${escapeHtml(item.usedAt || "-")}</td><td>${escapeHtml(item.usedPaymentId || "-")}</td></tr>`).join("")}</tbody></table></div>` : empty("보유 쿠폰이 없습니다.");
    document.querySelector("#couponCatalog").innerHTML = coupons.length ? `<div class="grid">${coupons.map((coupon) => `<article class="message"><div class="toolbar" style="margin:0 0 8px"><strong>${escapeHtml(coupon.name)}</strong><span class="status ${coupon.status === "ACTIVE" ? "ok" : "warn"}">${escapeHtml(coupon.status || "-")}</span></div><p class="muted">${escapeHtml(coupon.description || coupon.code || "")}</p><div class="small">${escapeHtml(coupon.discountType || "-")} ${escapeHtml(coupon.discountValue ?? "-")} · 최소 ${money(coupon.minOrder)} · 만료 ${escapeHtml(coupon.expirationDate || "-")}</div><div class="small muted section">쿠폰 발급과 상태 변경은 관리자만 처리할 수 있습니다.</div></article>`).join("")}</div>` : empty("등록된 쿠폰이 없습니다.");
  } catch (error) {
    document.querySelector("#myCoupons").innerHTML = errorMessage(error);
    document.querySelector("#couponCatalog").innerHTML = errorMessage(error);
  }
}

function loginPage() {
  const params = new URLSearchParams(location.search);
  const redirect = safeRedirect();
  const reason = params.get("reason");
  const reasonMessage = reason === "booking"
    ? "로그인되어 있지 않아 예약을 진행할 수 없습니다. 로그인 후 예약 화면으로 돌아갑니다."
    : reason === "bookings"
      ? "예약 내역은 로그인한 회원만 확인할 수 있습니다. 로그인 후 내 예약내역으로 돌아갑니다."
      : reason === "review"
        ? "리뷰 작성은 로그인한 회원만 이용할 수 있습니다."
        : "";
  userShell("login", `${title("로그인", "이메일 또는 Google 계정으로 로그인하세요.")}<section class="auth-layout"><form class="card card-body grid auth-card" id="loginForm"><div class="toolbar" style="margin:0"><h2>이메일 로그인</h2><span class="status ok">이메일 로그인 API</span></div>${reasonMessage ? `<div class="message error">${reasonMessage}</div>` : ""}<label><span>이메일</span><input name="email" type="email" autocomplete="email" required></label><label><span>비밀번호</span><input name="password" type="password" autocomplete="current-password" required></label><button class="btn primary">이메일로 로그인</button><div class="auth-divider"><span>또는</span></div><div class="google-login-button" id="googleLoginButton"></div><p class="auth-status" id="googleLoginStatus"></p><a class="small muted auth-link" href="signup.html">아직 회원이 아니면 회원가입</a></form><article class="card auth-info"><div class="card-body"><h2>Google 간편 로그인</h2><p class="muted">Google이 인증한 이메일과 계정 고유 식별자를 백엔드에서 검증합니다. 처음 로그인하면 일반 회원 계정이 자동으로 생성됩니다.</p><div class="grid"><span class="pill">Google ID 토큰 검증</span><span class="pill">이메일 인증 확인</span><span class="pill">신규 회원 자동 생성</span></div></div></article></section><div class="section" id="loginResult"></div>`);
  document.querySelector("#loginForm").addEventListener("submit", async (event) => {
    event.preventDefault();
    const data = qs(event.currentTarget);
    try {
      const user = await request("/api/auth/login", {
        method: "POST",
        body: JSON.stringify(data)
      });
      setCurrentUser(user);
      toast("로그인되었습니다.");
      location.href = redirect;
    } catch (error) {
      document.querySelector("#loginResult").innerHTML = errorMessage(error);
    }
  });
  initGoogleLogin("#googleLoginButton", "#googleLoginStatus");
}

async function loadBookings(selector, userId, adminMode) {
  if (!userId) {
    document.querySelector(selector).innerHTML = empty("사용자 ID를 입력하세요.");
    return;
  }
  try {
    const bookings = pageItems(await request(`/api/bookings/${userId}`));
    document.querySelector(selector).innerHTML = bookings.length ? `<div class="table-wrap"><table><thead><tr><th>예약번호</th><th>투숙객</th><th>일정</th><th>인원</th><th>객실</th>${adminMode ? "<th></th>" : ""}</tr></thead><tbody>${bookings.map((b) => `<tr><td>${escapeHtml(b.bookingNo || b.bookingId)}</td><td>${escapeHtml(b.guestName)}</td><td>${escapeHtml(b.checkinDate)} ~ ${escapeHtml(b.checkoutDate)}</td><td>성인 ${b.adultCount ?? 0}, 아동 ${b.childCount ?? 0}</td><td>${b.roomId ?? b.room?.roomId ?? "-"}</td>${adminMode ? `<td><button class="btn danger" data-cancel-booking="${b.bookingId}">취소</button></td>` : ""}</tr>`).join("")}</tbody></table></div>` : empty("예약 내역이 없습니다.");
    document.querySelectorAll("[data-cancel-booking]").forEach((btn) => btn.addEventListener("click", async () => {
      await request(`/api/bookings/cancel/${btn.dataset.cancelBooking}`, { method: "PATCH" });
      toast("예약이 취소되었습니다.");
      loadBookings(selector, userId, adminMode);
    }));
  } catch (error) {
    document.querySelector(selector).innerHTML = errorMessage(error);
  }
}

async function legacyReviewsPage(adminMode = false) {
  const shell = adminMode ? adminShell : (active, body) => userShell(active, body);
  await shell(adminMode ? "reviews-admin" : "reviews", `${title(adminMode ? "리뷰 관리" : "리뷰", "리뷰는 예약 ID, 사용자 ID, 객실 ID, 결제완료 데이터가 있어야 저장됩니다.")}<div class="grid cols-2"><form class="card card-body grid" id="reviewForm"><label><span>예약 ID</span><input name="reservationId" type="number" required></label><label><span>사용자 ID</span><input name="userId" type="number" required></label><label><span>호텔 ID</span><input name="hotelId" type="number" value="${escapeHtml(new URLSearchParams(location.search).get("hotelId") || "")}" required></label><label><span>객실 ID</span><input name="roomId" type="number" required></label><label><span>평점</span><input name="overallRating" type="number" min="1" max="5" value="5"></label><label><span>여행유형</span><input name="tripType" placeholder="COUPLE, FAMILY 등"></label><label><span>제목</span><input name="title" required></label><label><span>사진 경로</span><input name="photos" placeholder="/uploads/review/a.jpg, /uploads/review/b.jpg"></label><label style="grid-column:1/-1"><span>내용</span><textarea name="content" required></textarea></label><button class="btn primary">리뷰 저장</button></form><section id="reviewList">${empty("리뷰를 불러오는 중입니다.")}</section></div>`);
  document.querySelector("#reviewForm").addEventListener("submit", async (event) => {
    event.preventDefault();
    const data = qs(event.currentTarget);
    ["reservationId", "userId", "hotelId", "roomId", "overallRating"].forEach((key) => data[key] = Number(data[key]));
    data.viewCount = 0;
    data.likeCount = 0;
    data.dislikeCount = 0;
    data.photos = data.photos ? data.photos.split(",").map((photoPath, index) => ({ photoPath: photoPath.trim(), photoOrder: index + 1 })) : [];
    data.tags = [];
    data.ratings = [];
    try {
      await request("/api/review", { method: "POST", body: JSON.stringify(data) });
      toast("리뷰가 저장되었습니다.");
      loadReviews(adminMode);
    } catch (error) {
      document.querySelector("#reviewList").innerHTML = errorMessage(error);
    }
  });
  loadReviews(adminMode);
}

async function loadReviews(adminMode = false) {
  try {
    const reviews = pageItems(await request("/api/review?size=100"));
    document.querySelector("#reviewList").innerHTML = reviews.length ? `<div class="grid">${reviews.map((review) => reviewCard(review, adminMode ? `<button class="btn danger" data-delete-review="${review.reviewId}">삭제</button>` : "")).join("")}</div>` : empty("등록된 리뷰가 없습니다.");
    document.querySelectorAll("[data-delete-review]").forEach((btn) => btn.addEventListener("click", async () => {
      await request(`/api/review/${btn.dataset.deleteReview}`, { method: "DELETE" });
      toast("삭제되었습니다.");
      loadReviews(adminMode);
    }));
  } catch (error) {
    document.querySelector("#reviewList").innerHTML = errorMessage(error);
  }
}

function signupPage() {
  userShell("signup", `${title("회원가입", "이메일과 비밀번호로 가입할 수 있습니다.")}<section class="auth-layout"><form class="card card-body grid auth-card" id="signupForm"><h2>이메일로 회원가입</h2><input name="verificationToken" type="hidden"><div class="field-group"><label for="signupEmail">이메일</label><div class="verification-row"><input id="signupEmail" name="email" type="email" autocomplete="email" required><button class="btn" id="sendEmailCode" type="button" hidden disabled>인증번호 받기</button></div></div><p class="auth-status" id="emailSendStatus"></p><div class="field-group" id="emailVerificationCodeGroup" hidden><label for="signupVerificationCode">인증번호</label><div class="verification-row"><input id="signupVerificationCode" name="verificationCode" inputmode="numeric" pattern="[0-9]{6}" maxlength="6" placeholder="숫자 6자리" required disabled><button class="btn" id="verifyEmailCode" type="button" disabled>인증 확인</button></div></div><p class="auth-status" id="emailVerifyStatus"></p><label><span>비밀번호</span><input name="password" type="password" autocomplete="new-password" minlength="4" required disabled></label><label><span>이름</span><input name="name" autocomplete="name" required disabled></label><label><span>전화번호</span><input name="phone" type="tel" autocomplete="tel" required disabled></label><button class="btn primary" id="signupSubmit" disabled>가입</button><div class="auth-divider"><span>또는</span></div><div class="google-login-button" id="googleSignupButton"></div><p class="auth-status" id="googleSignupStatus"></p><a class="small muted auth-link" href="login.html">이미 회원이면 로그인</a></form><article class="card auth-info"><div class="card-body"><h2>회원가입 안내</h2><p class="muted">메일 발송 설정이 있으면 인증번호를 확인하고, 설정이 없어도 일반 이메일 회원가입은 계속 이용할 수 있습니다.</p><div class="grid"><span class="pill">이메일 중복 확인</span><span class="pill">비밀번호 암호화</span><span class="pill">가입 후 자동 로그인</span></div></div></article></section><div class="section" id="signupResult"></div>`);
  const form = document.querySelector("#signupForm");
  const emailInput = form.elements.email;
  const verificationTokenInput = form.elements.verificationToken;
  const verificationCodeInput = form.elements.verificationCode;
  const signupFields = ["password", "name", "phone"].map((name) => form.elements[name]);
  const submitButton = document.querySelector("#signupSubmit");
  const sendCodeButton = document.querySelector("#sendEmailCode");
  const verifyCodeButton = document.querySelector("#verifyEmailCode");
  const verificationCodeGroup = document.querySelector("#emailVerificationCodeGroup");
  let emailVerificationEnabled = false;

  const setSignupEnabled = (enabled) => {
    signupFields.forEach((field) => field.disabled = !enabled);
    submitButton.disabled = !enabled;
  };

  const useStandardSignup = (message) => {
    emailVerificationEnabled = false;
    sendCodeButton.hidden = true;
    verifyCodeButton.disabled = true;
    verificationCodeGroup.hidden = true;
    verificationCodeInput.disabled = true;
    verificationCodeInput.required = false;
    setSignupEnabled(true);
    setAuthStatus("#emailSendStatus", message, "warn");
  };

  const resetVerification = () => {
    if (!emailVerificationEnabled) return;
    verificationTokenInput.value = "";
    setSignupEnabled(false);
    setAuthStatus("#emailVerifyStatus", "");
  };

  const authConfigPromise = request("/api/auth/config").then((config) => {
    emailVerificationEnabled = Boolean(config.emailVerificationEnabled);
    if (!emailVerificationEnabled) {
      useStandardSignup("메일 인증 설정이 없어 일반 이메일 회원가입으로 진행합니다.");
      return;
    }
    sendCodeButton.hidden = false;
    sendCodeButton.disabled = false;
    verifyCodeButton.disabled = false;
    verificationCodeGroup.hidden = false;
    verificationCodeInput.disabled = false;
    verificationCodeInput.required = true;
    resetVerification();
    setAuthStatus("#emailSendStatus", "이메일 인증 후 회원가입할 수 있습니다.");
  }).catch(() => {
    useStandardSignup("인증 설정을 확인할 수 없어 일반 이메일 회원가입으로 진행합니다.");
  });

  emailInput.addEventListener("input", resetVerification);
  sendCodeButton.addEventListener("click", async () => {
    await authConfigPromise;
    if (!emailVerificationEnabled) return;
    if (!emailInput.reportValidity()) return;
    sendCodeButton.disabled = true;
    setAuthStatus("#emailSendStatus", "인증 메일을 전송하는 중입니다.");
    try {
      await request("/api/auth/email/send-code", {
        method: "POST",
        body: JSON.stringify({ email: emailInput.value })
      });
      setAuthStatus("#emailSendStatus", "인증번호를 전송했습니다. 메일함을 확인해주세요.", "success");
      form.elements.verificationCode.focus();
    } catch (error) {
      setAuthStatus("#emailSendStatus", error.message, "error");
    } finally {
      sendCodeButton.disabled = false;
    }
  });

  verifyCodeButton.addEventListener("click", async () => {
    if (!emailInput.reportValidity() || !verificationCodeInput.reportValidity()) return;
    try {
      const result = await request("/api/auth/email/verify", {
        method: "POST",
        body: JSON.stringify({ email: emailInput.value, code: verificationCodeInput.value })
      });
      verificationTokenInput.value = result.verificationToken;
      emailInput.readOnly = true;
      verificationCodeInput.readOnly = true;
      setSignupEnabled(true);
      setAuthStatus("#emailVerifyStatus", "이메일 인증이 완료되었습니다.", "success");
      form.elements.password.focus();
    } catch (error) {
      setAuthStatus("#emailVerifyStatus", error.message, "error");
    }
  });

  document.querySelector("#signupForm").addEventListener("submit", async (event) => {
    event.preventDefault();
    await authConfigPromise;
    const data = qs(event.currentTarget);
    delete data.verificationCode;
    try {
      let endpoint = "/api/auth/signup";
      if (!emailVerificationEnabled) {
        endpoint = "/api/users/signup";
        delete data.verificationToken;
      }
      const user = await request(endpoint, { method: "POST", body: JSON.stringify(data) });
      const safeUser = { ...user };
      delete safeUser.password;
      setCurrentUser(safeUser);
      document.querySelector("#signupResult").innerHTML = `<div class="message">회원가입이 완료되고 로그인되었습니다. <a class="btn primary" href="index.html">호텔 둘러보기</a></div>`;
    } catch (error) {
      document.querySelector("#signupResult").innerHTML = errorMessage(error);
    }
  });
  initGoogleLogin("#googleSignupButton", "#googleSignupStatus", "continue_with");
}

async function adminDashboard() {
  await adminShell("dashboard", `${title("관리자 대시보드", "선택한 호텔 또는 전체 호텔의 API 데이터를 요약합니다.")}<section id="dashboard">${empty("불러오는 중입니다.")}</section>`);
  try {
    const hotels = await safeLoadHotels();
    const scope = getHotelScope();
    const visibleHotels = scope ? hotels.filter((h) => String(h.hotelId) === String(scope)) : hotels;
    const [payments, promotions, reviews] = await Promise.all([
      request("/api/payment").then(pageItems).catch(() => []),
      request("/api/promotion").then(pageItems).catch(() => []),
      request("/api/review?size=100").then(pageItems).catch(() => [])
    ]);
    document.querySelector("#dashboard").innerHTML = `<div class="grid cols-4">
      <div class="metric">관리 호텔<strong>${visibleHotels.length}</strong></div>
      <div class="metric">결제 건수<strong>${payments.length}</strong></div>
      <div class="metric">프로모션<strong>${promotions.length}</strong></div>
      <div class="metric">리뷰<strong>${reviews.length}</strong></div>
    </div><section class="section"><h2>호텔</h2><div class="grid cols-3">${visibleHotels.map((hotel) => `<article class="card"><div class="card-body"><h3>${escapeHtml(hotel.name)}</h3><p class="muted">${escapeHtml(hotel.address || hotel.description || "")}</p><div class="form-row"><a class="btn" href="../hotel-detail.html?hotelId=${hotel.hotelId}">사용자 상세</a><a class="btn primary" href="rooms.html?hotelId=${hotel.hotelId}">객실 관리</a></div></div></article>`).join("") || empty("호텔 데이터가 없습니다.")}</div></section>`;
  } catch (error) {
    document.querySelector("#dashboard").innerHTML = errorMessage(error);
  }
}

async function legacyAdminHotels() {
  await adminShell("hotels", `${title("호텔 관리", "호텔 생성/조회/삭제와 편의시설 태그, 교통 정보를 관리합니다.")}<div class="grid cols-2"><form class="card card-body grid" id="hotelForm">${hotelFormFields()}<button class="btn primary">호텔 추가</button></form><section id="hotelList">${empty("호텔을 불러오는 중입니다.")}</section></div>`);
  document.querySelector("#hotelForm").addEventListener("submit", async (event) => {
    event.preventDefault();
    const data = qs(event.currentTarget);
    data.starRate = Number(data.starRate || 0);
    data.latitude = Number(data.latitude);
    data.longitude = Number(data.longitude);
    data.isActive = true;
    data.userId = data.userId ? Number(data.userId) : null;
    try {
      await request("/api/hotels", { method: "POST", body: JSON.stringify(data) });
      toast("호텔이 추가되었습니다.");
      loadAdminHotels();
    } catch (error) {
      document.querySelector("#hotelList").innerHTML = errorMessage(error);
    }
  });
  loadAdminHotels();
}

function hotelFormFields() {
  return `<label><span>호텔명</span><input name="name" required></label><label><span>설명</span><textarea name="description"></textarea></label><div class="grid cols-2"><label><span>도시</span><input name="city"></label><label><span>주소</span><input name="address"></label><label><span>우편번호</span><input name="zipCode"></label><label><span>전화</span><input name="phone"></label><label><span>이메일</span><input name="email" type="email"></label><label><span>등급</span><input name="starRate" type="number" min="1" max="5"></label><label><span>위도</span><input name="latitude" type="number" step="0.000001" required></label><label><span>경도</span><input name="longitude" type="number" step="0.000001" required></label><label><span>체크인</span><input name="checkIn" type="time"></label><label><span>체크아웃</span><input name="checkOut" type="time"></label><label><span>유형</span><select name="type"><option>HOTEL</option><option>RESORT</option><option>PENSION_GUESTHOUSE</option></select></label><label><span>관리자 사용자 ID</span><input name="userId" type="number"></label></div>`;
}

async function loadAdminHotels() {
  try {
    const hotels = pageItems(await request("/api/hotels?size=100"));
    document.querySelector("#hotelList").innerHTML = hotels.length ? `<div class="grid">${hotels.map((h) => `<article class="card"><div class="card-body"><div class="toolbar" style="margin:0"><h3>${escapeHtml(h.name)}</h3><button class="btn danger" data-delete-hotel="${h.hotelId}">삭제</button></div><p class="muted">${escapeHtml(h.address || "")}</p><div class="form-row"><a class="btn" href="rooms.html?hotelId=${h.hotelId}">객실</a><button class="btn" data-amen-hotel="${h.hotelId}">편의시설 태그</button><button class="btn" data-trans-hotel="${h.hotelId}">교통 추가</button></div><form class="form-row section" data-hotel-image-form="${h.hotelId}"><input type="file" name="file" accept="image/*" required><button class="btn" type="submit">호텔 이미지 업로드</button></form></div></article>`).join("")}</div>` : empty("호텔 데이터가 없습니다.");
    document.querySelectorAll("[data-delete-hotel]").forEach((btn) => btn.addEventListener("click", async () => {
      await request(`/api/hotels/${btn.dataset.deleteHotel}`, { method: "DELETE" });
      toast("호텔이 삭제되었습니다.");
      loadAdminHotels();
    }));
    document.querySelectorAll("[data-amen-hotel]").forEach((btn) => btn.addEventListener("click", () => addAmen(btn.dataset.amenHotel)));
    document.querySelectorAll("[data-trans-hotel]").forEach((btn) => btn.addEventListener("click", () => addTrans(btn.dataset.transHotel)));
    document.querySelectorAll("[data-hotel-image-form]").forEach((form) => form.addEventListener("submit", async (event) => {
      event.preventDefault();
      const userId = requireHotelManagerUserId();
      if (!userId) return;
      await uploadImage(`/api/hotelimage/hotel/${form.dataset.hotelImageForm}?userId=${encodeURIComponent(userId)}`, form.elements.file.files[0]);
      toast("호텔 이미지가 업로드되었습니다.");
    }));
  } catch (error) {
    document.querySelector("#hotelList").innerHTML = errorMessage(error);
  }
}

async function uploadImage(path, file, method = "POST") {
  const body = new FormData();
  body.append("file", file);
  return request(path, { method, body });
}

async function legacyAddAmen(hotelId) {
  const selected = prompt("편의시설 태그를 쉼표로 입력: wifi,pool,breakfast,freeParking");
  if (!selected) return;
  const userId = requireHotelManagerUserId();
  if (!userId) return;
  const body = { hotelId: Number(hotelId) };
  selected.split(",").map((v) => v.trim()).filter(Boolean).forEach((key) => body[key] = true);
  await request(`/api/hotelamenities?userId=${encodeURIComponent(userId)}`, { method: "POST", body: JSON.stringify(body) });
  toast("편의시설이 저장되었습니다.");
}

function resolveHotelManagerUserId(candidate) {
  const currentUser = getCurrentUser();
  const managementRoles = ["ADMIN", "SUPER_ADMIN", "HOTEL_MANAGER"];
  if (currentUser?.userId && managementRoles.includes(currentUser.role)) {
    return currentUser.userId;
  }
  return candidate || "";
}

function requireHotelManagerUserId(candidate) {
  const userId = resolveHotelManagerUserId(candidate);
  if (!userId) {
    toast("호텔 관리 작업에는 관리자 사용자 ID가 필요합니다.");
    return null;
  }
  return userId;
}

async function addTrans(hotelId, managerUserId) {
  const userId = requireHotelManagerUserId(managerUserId);
  if (!userId) return;
  const name = prompt("교통 이름");
  if (!name) return;
  const time = prompt("소요 시간") || "";
  const depart = prompt("출발/위치") || "";
  await request(`/api/hoteltrans?userId=${encodeURIComponent(userId)}`, {
    method: "POST",
    body: JSON.stringify({ hotelId: Number(hotelId), name, time, depart })
  });
  toast("교통 정보가 저장되었습니다.");
  loadAdminHotelsV2();
}

async function legacyAdminRooms() {
  await adminShell("rooms", `${title("객실 현황", "방 종류가 아니라 호실 단위로 객실을 표/보드 형태로 관리합니다.")}<div class="filters"><select id="roomHotel"></select><a class="btn primary" href="room-add.html">객실 추가</a></div><section class="section" id="roomsArea">${empty("객실을 불러오는 중입니다.")}</section>`);
  const hotels = await safeLoadHotels();
  const selected = new URLSearchParams(location.search).get("hotelId") || getHotelScope() || hotels[0]?.hotelId || "";
  document.querySelector("#roomHotel").innerHTML = hotels.map((h) => `<option value="${h.hotelId}" ${String(h.hotelId) === String(selected) ? "selected" : ""}>${escapeHtml(h.name)}</option>`).join("");
  document.querySelector("#roomHotel").addEventListener("change", (e) => loadRooms(e.target.value));
  loadRooms(selected);
}

async function loadRooms(hotelId) {
  if (!hotelId) {
    document.querySelector("#roomsArea").innerHTML = empty("호텔을 먼저 등록하세요.");
    return;
  }
  try {
    const rooms = pageItems(await request(`/api/room/hotel/${hotelId}?size=200`));
    document.querySelector("#roomsArea").innerHTML = `<div class="room-board">${rooms.map((r) => `<article class="room-cell ${String(r.roomStatus).toLowerCase().includes("enable") ? "enable" : String(r.roomStatus).toLowerCase().includes("construct") ? "construct" : "disable"}"><div><strong>${escapeHtml(r.number)}호</strong><div class="small muted">${escapeHtml(r.floor)}층 · ${escapeHtml(r.roomType)}</div><form class="form-row section" data-room-image-form="${r.roomId}"><input type="file" name="file" accept="image/*" required><button class="btn" type="submit">이미지</button></form></div><div><div class="price">${money(r.basePrice)}</div>${statusBadge(r.roomStatus)}<div class="form-row" style="margin-top:8px"><button class="btn" data-edit-room="${r.roomId}">수정</button><button class="btn danger" data-delete-room="${r.roomId}">삭제</button></div></div></article>`).join("") || empty("등록된 객실이 없습니다.")}</div>`;
    document.querySelectorAll("[data-delete-room]").forEach((btn) => btn.addEventListener("click", async () => {
      await request(`/api/room/${btn.dataset.deleteRoom}`, { method: "DELETE" });
      toast("객실이 삭제되었습니다.");
      loadRooms(hotelId);
    }));
    document.querySelectorAll("[data-edit-room]").forEach((btn) => btn.addEventListener("click", () => editRoom(btn.dataset.editRoom, hotelId)));
    document.querySelectorAll("[data-room-image-form]").forEach((form) => form.addEventListener("submit", async (event) => {
      event.preventDefault();
      await uploadImage(`/api/roomimage/room/${form.dataset.roomImageForm}`, form.elements.file.files[0]);
      toast("객실 이미지가 업로드되었습니다.");
    }));
  } catch (error) {
    document.querySelector("#roomsArea").innerHTML = errorMessage(error);
  }
}

async function editRoom(roomId, hotelId) {
  const room = await request(`/api/room/${roomId}`);
  const basePrice = prompt("기본요금", room.basePrice);
  if (basePrice == null) return;
  const status = prompt("상태: EnableReservation, DisableReservation, Construct", room.roomStatus) || room.roomStatus;
  await request("/api/room", { method: "PATCH", body: JSON.stringify({ ...room, basePrice: Number(basePrice), roomStatus: status }) });
  toast("객실이 수정되었습니다.");
  loadRooms(hotelId);
}

async function legacyAdminRoomAdd() {
  await adminShell("room-add", `${title("객실 추가", "구현된 객실 등록 API에 연결합니다.")}<form class="card card-body grid" id="roomForm"><div class="grid cols-2"><label><span>호텔</span><select name="hotelId" id="roomHotelSelect"></select></label><label><span>호실</span><input name="number" required></label><label><span>층</span><input name="floor" type="number" required></label><label><span>객실명</span><input name="name" required></label><label><span>크기</span><input name="size" type="number"></label><label><span>기본요금</span><input name="basePrice" type="number" required></label><label><span>성인 최대</span><input name="maxAdult" type="number" value="2"></label><label><span>아동 최대</span><input name="maxChild" type="number" value="0"></label><label><span>타입</span><select name="roomType"><option>Standard</option><option>Deluxe</option><option>Suite</option><option>Premium</option></select></label><label><span>상태</span><select name="roomStatus"><option>EnableReservation</option><option>DisableReservation</option><option>Construct</option></select></label><label><span>전망</span><select name="roomViewOption"><option>CityView</option><option>RiverView</option><option>MountainView</option><option>OceanView</option></select></label><label><span>침대</span><select name="roomBedOption"><option>DoubleBed</option><option>QueenBed</option><option>Floor</option></select></label></div><button class="btn primary">객실 저장</button></form><div class="section" id="roomResult"></div>`);
  const hotels = await safeLoadHotels();
  document.querySelector("#roomHotelSelect").innerHTML = hotels.map((h) => `<option value="${h.hotelId}">${escapeHtml(h.name)}</option>`).join("");
  document.querySelector("#roomForm").addEventListener("submit", async (event) => {
    event.preventDefault();
    const data = qs(event.currentTarget);
    ["hotelId", "floor", "size", "basePrice", "maxAdult", "maxChild"].forEach((key) => data[key] = Number(data[key] || 0));
    data.isActive = true;
    try {
      const room = await request("/api/room", { method: "POST", body: JSON.stringify(data) });
      document.querySelector("#roomResult").innerHTML = `<div class="message">${room.number}호 객실이 저장되었습니다. <a class="btn" href="rooms.html?hotelId=${room.hotelId}">객실 현황</a></div>`;
    } catch (error) {
      document.querySelector("#roomResult").innerHTML = errorMessage(error);
    }
  });
}

async function adminReservations() {
  await adminShell("reservations", `${title("예약 조회", "사용자 ID별 예약 조회/취소는 API에 연결하고, 전체 현황은 화면 전용으로 표시합니다.")}<section class="card"><div class="card-body"><div class="toolbar" style="margin:0 0 10px"><h2>전체 예약 현황</h2>${screenOnlyBadge()}</div><div class="table-wrap"><table><thead><tr><th>예약번호</th><th>호텔</th><th>객실</th><th>투숙객</th><th>일정</th><th>상태</th></tr></thead><tbody><tr><td>SN-2026-0721-7285</td><td>그랜드 서울</td><td>805호</td><td>예약 조회 고객</td><td>2026-08-15 ~ 2026-08-17</td><td>${statusBadge("EnableReservation")}</td></tr><tr><td>SN-2026-0801-1042</td><td>부산 오션 리조트</td><td>1502호</td><td>장기 투숙 고객</td><td>2026-08-20 ~ 2026-08-29</td><td><span class="status warn">결제대기</span></td></tr></tbody></table></div></div></section><section class="section"><div class="filters"><input id="adminBookingUserId" type="number" placeholder="사용자 ID"><button class="btn primary" id="adminBookingLookup">API 조회</button></div><div class="section" id="adminBookings"></div></section>`);
  document.querySelector("#adminBookingLookup").addEventListener("click", () => loadBookings("#adminBookings", document.querySelector("#adminBookingUserId").value, true));
}

async function adminCheckins() {
  await adminShell("checkins", `${title("체크인 현황", "체크인 처리 API가 없어 화면 전용 보드로 구성했습니다.")}<section class="grid cols-4"><div class="metric">오늘 체크인<strong>12</strong></div><div class="metric">체크아웃 예정<strong>8</strong></div><div class="metric">투숙 중<strong>34</strong></div><div class="metric">지연 도착<strong>3</strong></div></section><section class="section card"><div class="card-body"><div class="toolbar" style="margin:0 0 10px"><h2>체크인 목록</h2>${screenOnlyBadge()}</div><div class="table-wrap"><table><thead><tr><th>호텔</th><th>객실</th><th>투숙객</th><th>체크인</th><th>체크아웃</th><th>상태</th></tr></thead><tbody><tr><td>그랜드 서울</td><td>1201호</td><td>김하나</td><td>15:00</td><td>11:00</td><td><span class="status ok">체크인 완료</span></td></tr><tr><td>그랜드 서울</td><td>805호</td><td>이도윤</td><td>15:00</td><td>11:00</td><td><span class="status warn">도착 예정</span></td></tr><tr><td>부산 오션 리조트</td><td>1502호</td><td>박서연</td><td>16:00</td><td>11:00</td><td><span class="status bad">지연</span></td></tr></tbody></table></div></div></section>`);
}

async function legacyAdminCustomers() {
  await adminShell("customers", `${title("고객 조회", "회원 단건 조회/생성은 API에 연결하고, 고객 목록은 화면 전용으로 표시합니다.")}<section class="card"><div class="card-body"><div class="toolbar" style="margin:0 0 10px"><h2>고객 목록</h2>${screenOnlyBadge()}</div><div class="table-wrap"><table><thead><tr><th>고객</th><th>등급</th><th>예약</th><th>최근 투숙</th><th>상태</th></tr></thead><tbody><tr><td>예약 조회 고객<br><span class="small muted">lookup@omnistay.test</span></td><td>NEW_MEMBER</td><td>1건</td><td>그랜드 서울</td><td><span class="status ok">활성</span></td></tr><tr><td>VIP 고객<br><span class="small muted">vip@omnistay.test</span></td><td>VIP</td><td>7건</td><td>부산 오션 리조트</td><td><span class="status ok">활성</span></td></tr></tbody></table></div></div></section><section class="section grid cols-2"><section class="card card-body grid"><div class="toolbar" style="margin:0"><h2>회원 단건 조회</h2><span class="status ok">API 연결</span></div><div class="filters"><input id="lookupUserId" type="number" placeholder="사용자 ID"><button class="btn primary" id="lookupUser">조회</button></div><div id="userResult"></div></section><form class="card card-body grid" id="adminUserForm"><div class="toolbar" style="margin:0"><h2>회원 생성</h2><span class="status ok">API 연결</span></div><label><span>이메일</span><input name="email" type="email" required></label><label><span>비밀번호</span><input name="password" type="password" required></label><label><span>이름</span><input name="name" required></label><label><span>전화</span><input name="phone"></label><label><span>권한</span><select name="role"><option>CUSTOMER</option><option>HOTEL_MANAGER</option><option>ADMIN</option><option>SUPER_ADMIN</option></select></label><button class="btn primary">회원 생성</button></form></section>`);
  document.querySelector("#lookupUser").addEventListener("click", async () => {
    const id = document.querySelector("#lookupUserId").value;
    try {
      const user = await request(`/api/users/${id}`);
      document.querySelector("#userResult").innerHTML = `<div class="message"><strong>${escapeHtml(user.name)}</strong><div>${escapeHtml(user.email)}</div><div>${escapeHtml(user.role)} · ${escapeHtml(user.membership)}</div><button class="btn danger" data-delete-user="${user.userId}">삭제</button></div>`;
      document.querySelector("[data-delete-user]").addEventListener("click", async () => {
        await request(`/api/users/delete/${user.userId}`, { method: "DELETE" });
        document.querySelector("#userResult").innerHTML = empty("삭제되었습니다.");
      });
    } catch (error) {
      document.querySelector("#userResult").innerHTML = errorMessage(error);
    }
  });
  document.querySelector("#adminUserForm").addEventListener("submit", async (event) => {
    event.preventDefault();
    const data = { ...qs(event.currentTarget), status: "ACTIVE", membership: "NEW_MEMBER", marketingAgreed: false, point: 0 };
    await request("/api/users/signup", { method: "POST", body: JSON.stringify(data) });
    toast("회원이 생성되었습니다.");
  });
}

async function adminSettlement() {
  await adminShell("settlement", `${title("정산 리포트", "정산 API가 아직 없어 화면 전용 리포트로 표시합니다.")}<section class="grid cols-4"><div class="metric">정산 예정<strong>${money(4820000)}</strong></div><div class="metric">수수료<strong>${money(386000)}</strong></div><div class="metric">환불 차감<strong>${money(120000)}</strong></div><div class="metric">지급 예정일<strong>2026-08-05</strong></div></section><section class="section card"><div class="card-body"><div class="toolbar" style="margin:0 0 10px"><h2>호텔별 정산</h2>${screenOnlyBadge()}</div><div class="table-wrap"><table><thead><tr><th>호텔</th><th>예약 매출</th><th>수수료</th><th>환불</th><th>정산액</th><th>상태</th></tr></thead><tbody><tr><td>그랜드 서울</td><td>${money(3260000)}</td><td>${money(260800)}</td><td>${money(0)}</td><td>${money(2999200)}</td><td><span class="status warn">정산 예정</span></td></tr><tr><td>부산 오션 리조트</td><td>${money(1680000)}</td><td>${money(125200)}</td><td>${money(120000)}</td><td>${money(1434800)}</td><td><span class="status warn">검토 중</span></td></tr></tbody></table></div></div></section>`);
}

async function adminPromotions() {
  const managerUserId = getCurrentUser()?.userId || 1;
  await adminShell("promotions", `${title("프로모션 관리", "프로모션, 쿠폰, 회원별 쿠폰 API를 연결합니다.")}<div class="grid cols-2"><form class="card card-body grid" id="promoForm"><div class="toolbar" style="margin:0"><h2>프로모션 추가</h2><span class="status ok">API 연결</span></div><label><span>이름</span><input name="name" required></label><label><span>설명</span><textarea name="description" placeholder="VIP 등급 할인 또는 장기 투숙 할인"></textarea></label><label><span>할인 타입</span><select name="disType"><option>RATE</option><option>AMOUNT</option><option>PACKAGE</option></select></label><label><span>할인값</span><input name="disValue" required></label><label><span>시작</span><input name="startDate" type="datetime-local"></label><label><span>종료</span><input name="endDate" type="datetime-local"></label><label><span>예약횟수</span><input name="resCount" type="number" value="0"></label><label><span>상태</span><select name="status"><option>ACTIVE</option><option>INACTIVE</option><option>EXPIRED</option></select></label><label><span>객실 ID</span><input name="roomId" type="number"></label><label><span>관리자 사용자 ID</span><input name="userId" type="number" value="${managerUserId}"></label><button class="btn primary">프로모션 추가</button></form><section id="promoList">${empty("불러오는 중입니다.")}</section><form class="card card-body grid" id="promoSaleForm"><div class="toolbar" style="margin:0"><h2>프로모션 적용 대상</h2><span class="status ok">API 연결</span></div><label><span>프로모션</span><select name="proId" id="promoSaleProId"></select></label><label><span>대상 설명</span><input name="saleDes" placeholder="VIP 회원, 7박 이상 등" required></label><label><span>사용자 ID</span><input name="userId" type="number" value="${managerUserId}"></label><button class="btn primary">적용 대상 추가</button></form><section id="promoSaleList">${empty("불러오는 중입니다.")}</section><form class="card card-body grid" id="couponForm"><div class="toolbar" style="margin:0"><h2>쿠폰 등록</h2><span class="status ok">API 연결</span></div><label><span>코드</span><input name="code" required></label><label><span>이름</span><input name="name" required></label><label><span>설명</span><textarea name="description"></textarea></label><label><span>할인 타입</span><select name="discountType"><option>FIXED</option><option>RATE</option></select></label><label><span>할인값</span><input name="discountValue" type="number" min="1" required></label><label><span>최소 주문</span><input name="minOrder" type="number" value="0"></label><label><span>최대 할인</span><input name="maxDiscount" type="number" value="0"></label><label><span>만료일</span><input name="expirationDate" type="date" required></label><label><span>상태</span><select name="status"><option>ACTIVE</option><option>USED</option><option>EXPIRED</option></select></label><label><span>관리자 사용자 ID</span><input name="userId" type="number" min="1" value="${managerUserId}" required></label><button class="btn primary">쿠폰 등록</button></form><section id="adminCouponList">${empty("불러오는 중입니다.")}</section><form class="card card-body grid" id="userCouponForm"><div class="toolbar" style="margin:0"><h2>회원 쿠폰 발급</h2><span class="status ok">관리자 API</span></div><label><span>대상 사용자 ID</span><input name="targetUserId" type="number" min="1" required></label><label><span>쿠폰</span><select name="couponId" id="userCouponCouponId" required></select></label><label><span>관리자 사용자 ID</span><input name="managerUserId" id="userCouponManagerId" type="number" min="1" value="${managerUserId}" required></label><button class="btn primary">회원에게 발급</button></form><section id="adminUserCouponList">${empty("회원 쿠폰을 불러오는 중입니다.")}</section></div>`);
  document.querySelector("#promoForm").addEventListener("submit", async (event) => {
    event.preventDefault();
    const data = qs(event.currentTarget);
    data.resCount = Number(data.resCount || 0);
    if (data.roomId) data.roomId = Number(data.roomId); else delete data.roomId;
    if (data.userId) data.userId = Number(data.userId); else delete data.userId;
    if (data.startDate) data.startDate = `${data.startDate}:00`;
    if (data.endDate) data.endDate = `${data.endDate}:00`;
    await request("/api/promotion", { method: "POST", body: JSON.stringify(data) });
    toast("프로모션이 추가되었습니다.");
    loadPromotions();
  });
  document.querySelector("#promoSaleForm").addEventListener("submit", async (event) => {
    event.preventDefault();
    const data = qs(event.currentTarget);
    data.proId = Number(data.proId);
    data.userId = Number(data.userId || 0);
    await request("/api/promotionsale", { method: "POST", body: JSON.stringify(data) });
    toast("프로모션 적용 대상이 추가되었습니다.");
    loadPromotions();
  });
  document.querySelector("#couponForm").addEventListener("submit", async (event) => {
    event.preventDefault();
    const data = qs(event.currentTarget);
    ["discountValue", "minOrder", "maxDiscount", "userId"].forEach((key) => data[key] = Number(data[key] || 0));
    if (!data.expirationDate) delete data.expirationDate;
    await request("/api/coupons", { method: "POST", body: JSON.stringify(data) });
    toast("쿠폰이 등록되었습니다.");
    loadPromotions();
  });
  document.querySelector("#userCouponForm").addEventListener("submit", async (event) => {
    event.preventDefault();
    const data = qs(event.currentTarget);
    await request(`/api/usercoupons?userId=${encodeURIComponent(data.managerUserId)}`, {
      method: "POST",
      body: JSON.stringify({
        userId: Number(data.targetUserId),
        couponId: Number(data.couponId)
      })
    });
    toast("회원에게 쿠폰이 발급되었습니다.");
    event.currentTarget.elements.targetUserId.value = "";
    loadPromotions();
  });
  loadPromotions();
}

async function loadPromotions() {
  try {
    const [promotions, promotionSales, coupons, userCoupons] = await Promise.all([
      request("/api/promotion?size=100").then(pageItems),
      request("/api/promotionsale?size=100").then(pageItems),
      request("/api/coupons?size=100").then(pageItems),
      request("/api/usercoupons?size=100").then(pageItems)
    ]);
    const saleSelect = document.querySelector("#promoSaleProId");
    if (saleSelect) {
      saleSelect.innerHTML = promotions.map((p) => `<option value="${p.proId}">${escapeHtml(p.name)}</option>`).join("");
    }
    const userCouponSelect = document.querySelector("#userCouponCouponId");
    if (userCouponSelect) {
      userCouponSelect.innerHTML = coupons.map((coupon) => `<option value="${coupon.couponId}">${escapeHtml(coupon.name)} (${escapeHtml(coupon.code || "-")})</option>`).join("");
    }
    document.querySelector("#promoList").innerHTML = promotions.length ? `<div class="table-wrap"><table><thead><tr><th>ID</th><th>이름</th><th>할인</th><th>상태</th><th></th></tr></thead><tbody>${promotions.map((p) => `<tr><td>${p.proId}</td><td>${escapeHtml(p.name)}</td><td>${escapeHtml(p.disType)} ${escapeHtml(p.disValue)}</td><td>${escapeHtml(p.status)}</td><td><button class="btn danger" data-delete-promo="${p.proId}">삭제</button></td></tr>`).join("")}</tbody></table></div>` : empty("프로모션 데이터가 없습니다.");
    document.querySelector("#promoSaleList").innerHTML = promotionSales.length ? `<div class="table-wrap"><table><thead><tr><th>ID</th><th>프로모션</th><th>대상</th><th>사용자</th><th></th></tr></thead><tbody>${promotionSales.map((sale) => `<tr><td>${sale.proSaleId}</td><td>${escapeHtml(sale.promotion?.name || sale.proId || "-")}</td><td>${escapeHtml(sale.saleDes || "-")}</td><td>${escapeHtml(sale.userId || "-")}</td><td><button class="btn danger" data-delete-promo-sale="${sale.proSaleId}" data-user-id="${sale.userId || 1}">삭제</button></td></tr>`).join("")}</tbody></table></div>` : empty("프로모션 적용 대상이 없습니다.");
    document.querySelector("#adminCouponList").innerHTML = coupons.length ? `<div class="table-wrap"><table><thead><tr><th>ID</th><th>쿠폰</th><th>할인</th><th>만료</th><th>상태</th><th></th></tr></thead><tbody>${coupons.map((coupon) => `<tr><td>${coupon.couponId}</td><td>${escapeHtml(coupon.name)}<div class="small muted">${escapeHtml(coupon.code || "")}</div></td><td>${escapeHtml(coupon.discountType)} ${escapeHtml(coupon.discountValue)}</td><td>${escapeHtml(coupon.expirationDate || "-")}</td><td>${escapeHtml(coupon.status || "-")}</td><td><div class="actions"><button class="btn" data-edit-coupon="${coupon.couponId}">수정</button><button class="btn danger" data-delete-coupon="${coupon.couponId}" data-user-id="${coupon.userId || 1}">삭제</button></div></td></tr>`).join("")}</tbody></table></div>` : empty("등록된 쿠폰이 없습니다.");
    document.querySelector("#adminUserCouponList").innerHTML = userCoupons.length ? `<div class="table-wrap"><table><thead><tr><th>ID</th><th>회원</th><th>쿠폰</th><th>상태</th><th>사용 결제 ID</th><th></th></tr></thead><tbody>${userCoupons.map((item) => `<tr><td>${item.userCouponId}</td><td>${escapeHtml(item.user?.name || item.userId || "-")}<div class="small muted">ID ${escapeHtml(item.userId || item.user?.userId || "-")}</div></td><td>${escapeHtml(item.coupon?.name || item.couponId || "-")}</td><td><select data-user-coupon-status="${item.userCouponId}"><option ${item.userCouponStatus === "AVAILABLE" ? "selected" : ""}>AVAILABLE</option><option ${item.userCouponStatus === "USED" ? "selected" : ""}>USED</option><option ${item.userCouponStatus === "EXPIRED" ? "selected" : ""}>EXPIRED</option></select></td><td><input data-used-payment-id="${item.userCouponId}" type="number" min="1" value="${escapeHtml(item.usedPaymentId || "")}" placeholder="선택"></td><td><div class="actions"><button class="btn" data-update-user-coupon="${item.userCouponId}" data-target-user-id="${item.userId || item.user?.userId || ""}" data-coupon-id="${item.couponId || item.coupon?.couponId || ""}">상태 저장</button><button class="btn danger" data-delete-user-coupon="${item.userCouponId}">삭제</button></div></td></tr>`).join("")}</tbody></table></div>` : empty("발급된 회원 쿠폰이 없습니다.");
    document.querySelectorAll("[data-delete-promo]").forEach((btn) => btn.addEventListener("click", async () => {
      await request(`/api/promotion/${btn.dataset.deletePromo}?userId=1`, { method: "DELETE" });
      loadPromotions();
    }));
    document.querySelectorAll("[data-delete-promo-sale]").forEach((btn) => btn.addEventListener("click", async () => {
      await request(`/api/promotionsale/${btn.dataset.deletePromoSale}?userId=${btn.dataset.userId}`, { method: "DELETE" });
      loadPromotions();
    }));
    document.querySelectorAll("[data-delete-coupon]").forEach((btn) => btn.addEventListener("click", async () => {
      await request(`/api/coupons/${btn.dataset.deleteCoupon}?userId=${encodeURIComponent(btn.dataset.userId)}`, { method: "DELETE" });
      loadPromotions();
    }));
    document.querySelectorAll("[data-edit-coupon]").forEach((btn) => btn.addEventListener("click", async () => {
      const coupon = await request(`/api/coupons/${btn.dataset.editCoupon}`);
      const name = prompt("쿠폰 이름", coupon.name);
      if (name == null) return;
      const discountValue = prompt("할인값", coupon.discountValue);
      if (discountValue == null) return;
      const expirationDate = prompt("만료일 (YYYY-MM-DD)", coupon.expirationDate);
      if (expirationDate == null) return;
      await request("/api/coupons", {
        method: "PATCH",
        body: JSON.stringify({
          ...coupon,
          name,
          discountValue: Number(discountValue),
          expirationDate,
          userId: Number(document.querySelector("#userCouponManagerId")?.value || coupon.userId || 1)
        })
      });
      toast("쿠폰이 수정되었습니다.");
      loadPromotions();
    }));
    document.querySelectorAll("[data-update-user-coupon]").forEach((btn) => btn.addEventListener("click", async () => {
      const userCouponId = btn.dataset.updateUserCoupon;
      const status = document.querySelector(`[data-user-coupon-status="${userCouponId}"]`).value;
      const usedPaymentId = document.querySelector(`[data-used-payment-id="${userCouponId}"]`).value;
      const managerId = document.querySelector("#userCouponManagerId").value;
      const localNow = new Date(Date.now() - new Date().getTimezoneOffset() * 60000)
        .toISOString()
        .slice(0, 19);
      await request(`/api/usercoupons?userId=${encodeURIComponent(managerId)}`, {
        method: "PATCH",
        body: JSON.stringify({
          userCouponId: Number(userCouponId),
          userId: Number(btn.dataset.targetUserId),
          couponId: Number(btn.dataset.couponId),
          userCouponStatus: status,
          ...(status === "USED" ? { usedAt: localNow } : {}),
          ...(usedPaymentId ? { usedPaymentId: Number(usedPaymentId) } : {})
        })
      });
      toast("회원 쿠폰 상태가 저장되었습니다.");
      loadPromotions();
    }));
    document.querySelectorAll("[data-delete-user-coupon]").forEach((btn) => btn.addEventListener("click", async () => {
      const managerId = document.querySelector("#userCouponManagerId").value;
      await request(`/api/usercoupons/${btn.dataset.deleteUserCoupon}?userId=${encodeURIComponent(managerId)}`, { method: "DELETE" });
      toast("회원 쿠폰이 삭제되었습니다.");
      loadPromotions();
    }));
  } catch (error) {
    document.querySelector("#promoList").innerHTML = errorMessage(error);
    document.querySelector("#promoSaleList").innerHTML = errorMessage(error);
    document.querySelector("#adminCouponList").innerHTML = errorMessage(error);
    document.querySelector("#adminUserCouponList").innerHTML = errorMessage(error);
  }
}

async function adminPayments() {
  await adminShell("payments", `${title("결제 관리", "구현된 결제 목록/추가/삭제 API에 연결합니다.")}<div id="adminPayments">${empty("불러오는 중입니다.")}</div><section class="section"><a class="btn primary" href="../payment.html">결제 추가</a></section>`);
  loadPayments("#adminPayments", true);
}

async function legacyAdminRates() {
  await adminShell("rates", `${title("요금 정책", "구현된 rates API를 조회/수정/미리보기까지 연결합니다.")}<div class="filters"><select id="rateHotel"></select><select id="rateRoomType"><option>Standard</option><option>Deluxe</option><option>Suite</option><option>Premium</option></select><button class="btn primary" id="loadRates">조회</button></div><section class="section" id="ratesArea"></section><section class="section grid cols-3"><form class="card card-body grid" id="ratePolicyForm"><div class="toolbar" style="margin:0"><h2>정책 수정</h2><span class="status ok">API 연결</span></div><label><span>최소 숙박</span><input name="minStayNights" type="number" min="1"></label><label><span>체크인</span><input name="checkInTime" type="time"></label><label><span>체크아웃</span><input name="checkOutTime" type="time"></label><label><span>취소 기한(일)</span><input name="cancelDeadlineDays" type="number"></label><label><span>취소 수수료율</span><input name="cancelFeeRate" type="number" step="0.01"></label><label><span>무료 아동 나이</span><input name="freeChildAge" type="number"></label><label><span>아동 요금 유형</span><select name="childRateType"><option>FREE</option><option>DISCOUNT</option></select></label><label><span>아동 할인율</span><input name="childDiscountRate" type="number" step="0.01"></label><button class="btn primary">정책 저장</button></form><form class="card card-body grid" id="seasonRateForm"><div class="toolbar" style="margin:0"><h2>시즌 요금 추가</h2><span class="status ok">API 연결</span></div><label><span>시즌명</span><input name="seasonName" required></label><label><span>시작일</span><input name="startDate" type="date" required></label><label><span>종료일</span><input name="endDate" type="date" required></label><label><span>평일가</span><input name="weekdayPrice" type="number" required></label><label><span>주말가</span><input name="weekendPrice" type="number" required></label><label><span>상태</span><select name="status"><option>UPCOMING</option><option>ONGOING</option><option>ENDED</option></select></label><label><span>배율</span><input name="multiplier" type="number" step="0.01" value="20.0"></label><button class="btn primary">시즌 저장</button></form><form class="card card-body grid" id="pricePreviewForm"><div class="toolbar" style="margin:0"><h2>가격 미리보기</h2><span class="status ok">API 연결</span></div><label><span>평일가</span><input name="weekdayPrice" type="number" required></label><label><span>주말가</span><input name="weekendPrice" type="number" required></label><label><span>평일 정책</span><select name="weekdayPolicyEnabled"><option value="true">사용</option><option value="false">미사용</option></select></label><label><span>배율</span><input name="multiplier" type="number" step="0.01" value="20.0"></label><button class="btn primary">미리보기</button><div id="pricePreviewResult"></div></form></section>`);
  const hotels = await safeLoadHotels();
  document.querySelector("#rateHotel").innerHTML = hotels.map((h) => `<option value="${h.hotelId}">${escapeHtml(h.name)}</option>`).join("");
  document.querySelector("#loadRates").addEventListener("click", loadRates);
  document.querySelector("#ratePolicyForm").addEventListener("submit", saveRatePolicy);
  document.querySelector("#seasonRateForm").addEventListener("submit", saveSeasonRate);
  document.querySelector("#pricePreviewForm").addEventListener("submit", previewRatePrice);
}

async function loadRates() {
  const hotelId = document.querySelector("#rateHotel").value;
  const roomType = document.querySelector("#rateRoomType").value;
  try {
    const [summary, policy, seasons] = await Promise.all([
      request(`/api/rates/hotels/${hotelId}/summary`).catch((e) => ({ error: e.message })),
      request(`/api/rates/policies/hotels/${hotelId}`).catch((e) => ({ error: e.message })),
      request(`/api/rates/hotels/${hotelId}/rooms/${roomType}`).catch(() => [])
    ]);
    const seasonItems = pageItems(seasons);
    document.querySelector("#ratesArea").innerHTML = `<div class="grid cols-3"><div class="metric">요금 요약<strong>${escapeHtml(summary.error || "조회됨")}</strong></div><div class="metric">정책<strong>${escapeHtml(policy.error || `${policy.minStayNights ?? "-"}박`)}</strong></div><div class="metric">시즌 요금<strong>${seasonItems.length}</strong></div></div><section class="section grid cols-2"><pre class="card card-body">${escapeHtml(JSON.stringify(summary, null, 2))}</pre><pre class="card card-body">${escapeHtml(JSON.stringify(policy, null, 2))}</pre></section><section class="section"><div class="table-wrap"><table><thead><tr><th>ID</th><th>시즌</th><th>기간</th><th>평일/주말</th><th>상태</th><th></th></tr></thead><tbody>${seasonItems.map((season) => `<tr><td>${season.seasonRateId}</td><td>${escapeHtml(season.seasonName)}</td><td>${escapeHtml(season.startDate)} ~ ${escapeHtml(season.endDate)}</td><td>${money(season.weekdayPrice)} / ${money(season.weekendPrice)}</td><td>${escapeHtml(season.status)}</td><td><button class="btn danger" data-delete-season="${season.seasonRateId}">삭제</button></td></tr>`).join("") || `<tr><td colspan="6">시즌 요금이 없습니다.</td></tr>`}</tbody></table></div></section>`;
    document.querySelectorAll("[data-delete-season]").forEach((btn) => btn.addEventListener("click", async () => {
      await request(`/api/rates/${btn.dataset.deleteSeason}`, { method: "DELETE" });
      toast("시즌 요금이 삭제되었습니다.");
      loadRates();
    }));
  } catch (error) {
    document.querySelector("#ratesArea").innerHTML = errorMessage(error);
  }
}

async function saveRatePolicy(event) {
  event.preventDefault();
  const hotelId = document.querySelector("#rateHotel").value;
  const data = qs(event.currentTarget);
  try {
    if (data.minStayNights) await request(`/api/rates/policies/hotels/${hotelId}/min-stay`, { method: "PUT", body: JSON.stringify({ minStayNights: Number(data.minStayNights) }) });
    if (data.checkInTime && data.checkOutTime) await request(`/api/rates/policies/hotels/${hotelId}/times`, { method: "PUT", body: JSON.stringify({ checkInTime: data.checkInTime, checkOutTime: data.checkOutTime }) });
    if (data.cancelDeadlineDays && data.cancelFeeRate) await request(`/api/rates/policies/hotels/${hotelId}/cancellation`, { method: "PUT", body: JSON.stringify({ cancelDeadlineDays: Number(data.cancelDeadlineDays), cancelFeeRate: Number(data.cancelFeeRate) }) });
    if (data.freeChildAge && data.childRateType) await request(`/api/rates/policies/hotels/${hotelId}/child-rates`, { method: "PUT", body: JSON.stringify({ freeChildAge: Number(data.freeChildAge), childRateType: data.childRateType, childDiscountRate: Number(data.childDiscountRate || 0) }) });
    toast("요금 정책이 저장되었습니다.");
    loadRates();
  } catch (error) {
    document.querySelector("#ratesArea").innerHTML = errorMessage(error);
  }
}

async function saveSeasonRate(event) {
  event.preventDefault();
  const hotelId = document.querySelector("#rateHotel").value;
  const roomType = document.querySelector("#rateRoomType").value;
  const data = qs(event.currentTarget);
  const multiplier = data.multiplier || "20.0";
  delete data.multiplier;
  Object.assign(data, {
    hotelId: Number(hotelId),
    roomType,
    weekdayPrice: Number(data.weekdayPrice),
    weekendPrice: Number(data.weekendPrice),
    minStayNights: 1,
    weekdayPolicyEnabled: true
  });
  await request(`/api/rates?multiplier=${encodeURIComponent(multiplier)}`, { method: "POST", body: JSON.stringify(data) });
  toast("시즌 요금이 저장되었습니다.");
  loadRates();
}

async function previewRatePrice(event) {
  event.preventDefault();
  const data = qs(event.currentTarget);
  const result = await request("/api/rates/preview", { method: "POST", body: JSON.stringify({ weekdayPrice: Number(data.weekdayPrice), weekendPrice: Number(data.weekendPrice), weekdayPolicyEnabled: data.weekdayPolicyEnabled === "true", multiplier: Number(data.multiplier || 0) }) });
  document.querySelector("#pricePreviewResult").innerHTML = `<div class="message">평일 ${money(result.calculatedWeekdayPrice)} · 주말 ${money(result.calculatedWeekendPrice)} · 증가율 ${escapeHtml(result.increaseRate ?? "-")}</div>`;
}

async function adminSales() {
  await adminShell("sales", `${title("매출 분석", "구현된 sales API는 호텔 ID와 월을 기준으로 조회합니다.")}<div class="filters"><select id="salesHotel"></select><input id="salesMonth" type="month" value="${todayMonth()}"><button class="btn primary" id="salesLoad">조회</button></div><section class="section" id="salesArea"></section>`);
  const hotels = await safeLoadHotels();
  document.querySelector("#salesHotel").innerHTML = hotels.map((h) => `<option value="${h.hotelId}">${escapeHtml(h.name)}</option>`).join("");
  document.querySelector("#salesLoad").addEventListener("click", loadSales);
}

async function loadSales() {
  const hotelId = document.querySelector("#salesHotel").value;
  const month = document.querySelector("#salesMonth").value;
  try {
    const [dashboard, monthly, top, rooms] = await Promise.all([
      request(`/api/sales/dashboard?hotelId=${hotelId}&targetMonth=${month}`),
      request(`/api/sales/monthly?hotelId=${hotelId}&startDate=${month}-01`),
      request(`/api/sales/top-bookings?hotelId=${hotelId}&targetMonth=${month}`),
      request(`/api/sales/rooms?hotelId=${hotelId}&targetMonth=${month}`)
    ]);
    document.querySelector("#salesArea").innerHTML = `<div class="grid cols-4"><div class="metric">대시보드<strong>조회됨</strong></div><div class="metric">월별 데이터<strong>${pageItems(monthly).length}</strong></div><div class="metric">상위 예약<strong>${pageItems(top).length}</strong></div><div class="metric">객실 매출<strong>${pageItems(rooms).length}</strong></div></div><pre class="card card-body">${escapeHtml(JSON.stringify(dashboard, null, 2))}</pre>`;
  } catch (error) {
    document.querySelector("#salesArea").innerHTML = errorMessage(error);
  }
}

async function seedPage() {
  userShell("home", `${title("DB 데이터 추가", "백엔드 서버가 실행 중일 때만 기존 API로 샘플 데이터를 DB에 넣습니다.")}<button class="btn primary" id="seedBtn">시드 데이터 추가</button><section class="section" id="seedLog"></section>`);
  document.querySelector("#seedBtn").addEventListener("click", async () => {
    const log = document.querySelector("#seedLog");
    log.innerHTML = empty("시드 데이터를 추가하는 중입니다.");
    try {
      const user = await request("/api/users/signup", { method: "POST", body: JSON.stringify({ email: `manager${Date.now()}@omnistay.test`, password: "1234", name: "테스트 호텔 관리자", phone: "010-0000-0000", role: "HOTEL_MANAGER", status: "ACTIVE", membership: "NEW_MEMBER", marketingAgreed: false, point: 0 }) });
      const hotel = await request("/api/hotels", { method: "POST", body: JSON.stringify({ name: "그랜드 서울", description: "서울 중심의 비즈니스 호텔", address: "서울 중구 세종대로 1", city: "서울", zipCode: "04524", phone: "02-1000-1000", email: "grand@omnistay.test", checkIn: "15:00", checkOut: "11:00", starRate: 5, isActive: true, latitude: 37.5665, longitude: 126.978, type: "HOTEL", userId: user.userId }) });
      await request(`/api/hotelamenities?userId=${encodeURIComponent(user.userId)}`, { method: "POST", body: JSON.stringify({ hotelId: hotel.hotelId, wifi: true, breakfast: true, fitnessCenter: true, freeParking: true, concierge: true }) });
      await request(`/api/hoteltrans?userId=${encodeURIComponent(user.userId)}`, { method: "POST", body: JSON.stringify({ hotelId: hotel.hotelId, name: "시청역", time: "도보 5분", depart: "1번 출구" }) });
      const room = await request("/api/room", { method: "POST", body: JSON.stringify({ hotelId: hotel.hotelId, name: "디럭스 더블", number: "1201", floor: 12, size: 32, basePrice: 180000, maxAdult: 2, maxChild: 1, isActive: true, roomType: "Deluxe", roomStatus: "EnableReservation", roomViewOption: "CityView", roomBedOption: "DoubleBed" }) });
      const booking = await request("/api/bookings/insert", { method: "POST", body: JSON.stringify({ userId: user.userId, roomId: room.roomId, guestName: user.name, nationality: "KOREA", guestPhone: user.phone, guestEmail: user.email, specialRequest: "고층 선호", adultCount: 2, childCount: 0, checkinDate: "2026-08-10", checkoutDate: "2026-08-12" }) });
      const seedOrderId = String(booking.bookingNo);
      const payment = await request("/api/payment", { method: "POST", body: JSON.stringify({ bookingId: booking.bookingId, booking: { bookingId: booking.bookingId }, transactionNum: seedOrderId, orderId: seedOrderId, paymentMethod: "CreditCard", paymentStatus: "Paid", totalAmount: 360000, currency: "KRW", couponId: 0, usedPoint: 0, discountAmount: 0, provider: "TOSS" }) }).catch(() => null);
      await request("/api/promotion", { method: "POST", body: JSON.stringify({ name: "VIP 회원 등급 할인", description: "회원 등급에 따른 할인", disType: "RATE", disValue: "10", startDate: "2026-08-01T00:00:00", endDate: "2026-12-31T23:59:00", resCount: 0, status: "ACTIVE", roomId: room.roomId, userId: user.userId }) });
      log.innerHTML = `<div class="message">DB 데이터가 추가되었습니다. 호텔 ID ${hotel.hotelId}, 객실 ID ${room.roomId}, 사용자 ID ${user.userId}, 예약 ID ${booking.bookingId}${payment ? "" : "<div class=\"small\">결제 저장은 현재 백엔드 DTO 오류로 건너뛰었습니다.</div>"}</div>`;
    } catch (error) {
      log.innerHTML = errorMessage(error);
    }
  });
}

async function loadRateRooms(hotelId, selector = "#roomsArea") {
  if (!hotelId) {
    document.querySelector(selector).innerHTML = empty("호텔을 먼저 선택하세요.");
    return;
  }
  try {
    const rooms = pageItems(await request(`/api/rates/hotels/${hotelId}/rooms?size=200`));
    document.querySelector(selector).innerHTML = rooms.length ? `<div class="table-wrap"><table><thead><tr><th>호실</th><th>객실명</th><th>층/면적</th><th>객실 유형</th><th>정원</th><th>기본 요금</th><th>상태</th><th>작업</th></tr></thead><tbody>${rooms.map((room) => `<tr><td><strong>${escapeHtml(room.number)}</strong></td><td>${escapeHtml(room.name)}${room.description ? `<div class="small muted">${escapeHtml(room.description)}</div>` : ""}</td><td>${escapeHtml(room.floor)}층 / ${escapeHtml(room.size)}㎡</td><td>${escapeHtml(room.roomType)} / ${escapeHtml(room.roomBedOption)} / ${escapeHtml(room.roomViewOption)}</td><td>성인 ${room.maxAdult ?? 0}명 · 아동 ${room.maxChild ?? 0}명</td><td>${money(room.basePrice)}</td><td>${statusBadge(room.roomStatus)}</td><td><div class="form-row"><button class="btn" data-rate-edit="${room.roomId}">수정</button><button class="btn danger" data-rate-delete="${room.roomId}">삭제</button></div><form class="form-row section" data-rate-image="${room.roomId}"><input name="files" type="file" accept="image/*" required><button class="btn" type="submit">이미지</button></form></td></tr>`).join("")}</tbody></table></div>` : empty("등록된 객실이 없습니다.");
    document.querySelectorAll("[data-rate-delete]").forEach((button) => button.addEventListener("click", async () => {
      await request(`/api/rates/rooms/${button.dataset.rateDelete}`, { method: "DELETE" });
      toast("객실이 삭제되었습니다.");
      loadRateRooms(hotelId, selector);
    }));
    document.querySelectorAll("[data-rate-edit]").forEach((button) => button.addEventListener("click", () => editRateRoom(button.dataset.rateEdit, hotelId, selector)));
    document.querySelectorAll("[data-rate-image]").forEach((form) => form.addEventListener("submit", async (event) => {
      event.preventDefault();
      const body = new FormData();
      body.append("files", form.elements.files.files[0]);
      await request(`/api/rates/rooms/${form.dataset.rateImage}/images`, { method: "POST", body });
      toast("객실 이미지가 업로드되었습니다.");
    }));
  } catch (error) {
    document.querySelector(selector).innerHTML = errorMessage(error);
  }
}

async function editRateRoom(roomId, hotelId, selector = "#roomsArea") {
  try {
    const room = await request(`/api/rates/rooms/${roomId}`);
    const name = prompt("객실명", room.name);
    if (name == null) return;
    const number = prompt("객실 번호", room.number);
    if (number == null) return;
    const basePrice = prompt("기본 요금", room.basePrice);
    if (basePrice == null) return;
    const roomStatus = prompt("상태: EnableReservation, DisableReservation, Construct", room.roomStatus) || room.roomStatus;
    await request(`/api/rates/rooms/${roomId}`, { method: "PUT", body: JSON.stringify({ name, number, basePrice: Number(basePrice), roomStatus }) });
    toast("객실이 수정되었습니다.");
    loadRateRooms(hotelId, selector);
  } catch (error) {
    document.querySelector(selector).innerHTML = errorMessage(error);
  }
}

async function adminRooms() {
  await adminShell("rooms", `${title("객실 현황", "백엔드의 객실 상세 API를 사용해 호실 단위로 관리합니다.")}<div class="filters"><select id="roomHotel"></select><a class="btn primary" href="room-add.html">객실 추가</a></div><section class="section" id="roomsArea">${empty("객실을 불러오는 중입니다.")}</section>`);
  const hotels = await safeLoadHotels();
  const selected = new URLSearchParams(location.search).get("hotelId") || getHotelScope() || hotels[0]?.hotelId || "";
  document.querySelector("#roomHotel").innerHTML = hotels.map((hotel) => `<option value="${hotel.hotelId}" ${String(hotel.hotelId) === String(selected) ? "selected" : ""}>${escapeHtml(hotel.name)}</option>`).join("");
  document.querySelector("#roomHotel").addEventListener("change", (event) => loadRateRooms(event.target.value));
  loadRateRooms(selected);
}

async function adminRoomAdd() {
  await adminShell("room-add", `${title("객실 추가", "객실 기본 정보와 설명을 백엔드의 /api/rates 생성 API에 저장합니다.")}<form class="card card-body grid" id="rateRoomForm"><div class="grid cols-2"><label><span>호텔</span><select id="rateRoomHotel"></select></label><label><span>객실 번호</span><input name="number" required></label><label><span>층</span><input name="floor" type="number" min="1" required></label><label><span>객실명</span><input name="name" required></label><label><span>면적(㎡)</span><input name="size" type="number" min="1" required></label><label><span>기본 요금</span><input name="basePrice" type="number" min="1" required></label><label><span>성인 최대</span><input name="maxAdult" type="number" min="1" value="2" required></label><label><span>아동 최대</span><input name="maxChild" type="number" min="0" value="0" required></label><label><span>유형</span><select name="roomType"><option>Standard</option><option>Suite</option><option>Deluxe</option><option>Premium</option></select></label><label><span>상태</span><select name="roomStatus"><option>EnableReservation</option><option>DisableReservation</option><option>Construct</option></select></label><label><span>전망</span><select name="roomViewOption"><option>CityView</option><option>RiverView</option><option>MountainView</option><option>OceanView</option></select></label><label><span>침대</span><select name="roomBedOption"><option>Floor</option><option>DoubleBed</option><option>QueenBed</option></select></label></div><label><span>객실 설명</span><textarea name="description"></textarea></label><button class="btn primary">객실 저장</button></form><div class="section" id="rateRoomResult"></div>`);
  const hotels = await safeLoadHotels();
  const selected = getHotelScope() || hotels[0]?.hotelId || "";
  document.querySelector("#rateRoomHotel").innerHTML = hotels.map((hotel) => `<option value="${hotel.hotelId}" ${String(hotel.hotelId) === String(selected) ? "selected" : ""}>${escapeHtml(hotel.name)}</option>`).join("");
  document.querySelector("#rateRoomForm").addEventListener("submit", async (event) => {
    event.preventDefault();
    const data = qs(event.currentTarget);
    ["floor", "size", "basePrice", "maxAdult", "maxChild"].forEach((key) => data[key] = Number(data[key] || 0));
    try {
      const hotelId = document.querySelector("#rateRoomHotel").value;
      const room = await request(`/api/rates/hotels/${hotelId}/rooms`, { method: "POST", body: JSON.stringify(data) });
      document.querySelector("#rateRoomResult").innerHTML = `<div class="message">${escapeHtml(room.number)}호 객실이 저장되었습니다. <a class="btn" href="rooms.html?hotelId=${hotelId}">객실 현황</a></div>`;
    } catch (error) {
      document.querySelector("#rateRoomResult").innerHTML = errorMessage(error);
    }
  });
}

async function adminRates() {
  await adminShell("rates", `${title("객실 요금", "객실별 기본 요금은 백엔드의 객실 관리 API에서 조회합니다.")}<div class="filters"><select id="rateHotel"></select><button class="btn primary" id="loadRates">조회</button></div><section class="section" id="ratesArea">${empty("객실 요금을 불러오는 중입니다.")}</section><section class="section card"><div class="card-body"><div class="toolbar" style="margin:0"><h2>요금 정책 / 시즌 요금 / 가격 미리보기</h2>${screenOnlyBadge()}</div><p class="muted">현재 백엔드에는 객실 기본 요금 CRUD만 있고, 정책·시즌·미리보기 API는 없습니다. 화면은 보존했습니다.</p></div></section>`);
  const hotels = await safeLoadHotels();
  const selected = getHotelScope() || hotels[0]?.hotelId || "";
  document.querySelector("#rateHotel").innerHTML = hotels.map((hotel) => `<option value="${hotel.hotelId}" ${String(hotel.hotelId) === String(selected) ? "selected" : ""}>${escapeHtml(hotel.name)}</option>`).join("");
  document.querySelector("#loadRates").addEventListener("click", () => loadRateRooms(document.querySelector("#rateHotel").value, "#ratesArea"));
  loadRateRooms(selected, "#ratesArea");
}

const hotelAmenityKeys = ["wifi", "pool", "fitnessCenter", "spa", "restaurant", "valetParking", "freeParking", "concierge", "bar", "breakfast", "airportShuttle", "roomService", "laundry", "lounge", "sauna", "freeCancel", "petFriendly"];

async function addAmen(hotelId, managerUserId) {
  const userId = requireHotelManagerUserId(managerUserId);
  if (!userId) return;
  const selected = prompt("편의시설 태그를 쉼표로 입력하세요: wifi,pool,breakfast,freeParking");
  if (!selected) return;
  const existing = pageItems(await request(`/api/hotelamenities/hotel/${hotelId}?size=1`))[0];
  const body = { ...(existing || {}), hotelId: Number(hotelId) };
  hotelAmenityKeys.forEach((key) => body[key] = false);
  selected.split(",").map((value) => value.trim()).filter((value) => hotelAmenityKeys.includes(value)).forEach((key) => body[key] = true);
  await request(`/api/hotelamenities?userId=${encodeURIComponent(userId)}`, {
    method: existing?.amenId ? "PATCH" : "POST",
    body: JSON.stringify(body)
  });
  toast("편의시설 태그가 저장되었습니다.");
  loadAdminHotelsV2();
}

async function deleteAmenity(amenId, managerUserId) {
  const userId = requireHotelManagerUserId(managerUserId);
  if (!userId) return;
  await request(`/api/hotelamenities/${amenId}?userId=${encodeURIComponent(userId)}`, { method: "DELETE" });
  toast("편의시설이 삭제되었습니다.");
  loadAdminHotelsV2();
}

async function editTransport(transId, managerUserId) {
  const userId = requireHotelManagerUserId(managerUserId);
  if (!userId) return;
  const transport = await request(`/api/hoteltrans/${transId}`);
  const name = prompt("교통 이름", transport.name || "");
  if (name == null) return;
  const time = prompt("소요 시간", transport.time || "");
  if (time == null) return;
  const depart = prompt("출발/위치", transport.depart || "");
  if (depart == null) return;
  await request(`/api/hoteltrans?userId=${encodeURIComponent(userId)}`, {
    method: "PATCH",
    body: JSON.stringify({ ...transport, name, time, depart })
  });
  toast("교통 정보가 수정되었습니다.");
  loadAdminHotelsV2();
}

async function deleteTransport(transId, managerUserId) {
  const userId = requireHotelManagerUserId(managerUserId);
  if (!userId) return;
  await request(`/api/hoteltrans/${transId}?userId=${encodeURIComponent(userId)}`, { method: "DELETE" });
  toast("교통 정보가 삭제되었습니다.");
  loadAdminHotelsV2();
}

async function replaceHotelImage(hotelImageId, file, managerUserId) {
  const userId = requireHotelManagerUserId(managerUserId);
  if (!userId || !file) return;
  await uploadImage(`/api/hotelimage/image/${hotelImageId}?userId=${encodeURIComponent(userId)}`, file, "PATCH");
  toast("호텔 이미지가 교체되었습니다.");
  loadAdminHotelsV2();
}

async function deleteHotelImage(hotelImageId, managerUserId) {
  const userId = requireHotelManagerUserId(managerUserId);
  if (!userId) return;
  await request(`/api/hotelimage/${hotelImageId}?userId=${encodeURIComponent(userId)}`, { method: "DELETE" });
  toast("호텔 이미지가 삭제되었습니다.");
  loadAdminHotelsV2();
}

async function editHotel(hotelId) {
  try {
    const hotel = await request(`/api/hotels/${hotelId}`);
    const name = prompt("호텔명", hotel.name);
    if (name == null) return;
    const address = prompt("주소", hotel.address || "");
    if (address == null) return;
    const description = prompt("설명", hotel.description || "");
    if (description == null) return;
    await request("/api/hotels", { method: "PATCH", body: JSON.stringify({ ...hotel, name, address, description }) });
    toast("호텔 정보가 수정되었습니다.");
    loadAdminHotelsV2();
  } catch (error) {
    document.querySelector("#hotelList").innerHTML = errorMessage(error);
  }
}

async function loadAdminHotelsV2() {
  try {
    const hotels = pageItems(await request("/api/hotels?size=100"));
    const scope = getHotelScope();
    const visibleHotels = scope
      ? hotels.filter((hotel) => String(hotel.hotelId) === String(scope))
      : hotels;
    const details = await Promise.all(visibleHotels.map(async (hotel) => {
      const [amenityResponse, transportResponse, imageResponse] = await Promise.all([
        request(`/api/hotelamenities/hotel/${hotel.hotelId}?size=1`).catch(() => []),
        request(`/api/hoteltrans/hotel/${hotel.hotelId}?size=100`).catch(() => []),
        request(`/api/hotelimage/hotel/${hotel.hotelId}?size=100`).catch(() => [])
      ]);
      return {
        hotel,
        amenities: pageItems(amenityResponse)[0],
        transports: pageItems(transportResponse),
        images: pageItems(imageResponse)
      };
    }));
    const labels = { wifi: "Wi-Fi", pool: "수영장", fitnessCenter: "피트니스", spa: "스파", restaurant: "레스토랑", valetParking: "발렛", freeParking: "무료 주차", concierge: "컨시어지", bar: "바", breakfast: "조식", airportShuttle: "공항 셔틀", roomService: "룸서비스", laundry: "세탁", lounge: "라운지", sauna: "사우나", freeCancel: "무료 취소", petFriendly: "반려동물" };
    document.querySelector("#hotelList").innerHTML = details.length ? `<div class="grid">${details.map(({ hotel, amenities, transports, images }) => {
      const managerUserId = resolveHotelManagerUserId(hotel.userId || hotel.user?.userId);
      const tags = Object.entries(labels).filter(([key]) => amenities?.[key]).map(([, label]) => `<span class="pill">${label}</span>`).join("");
      const transportRows = transports.length
        ? transports.map((transport) => `<div class="hotel-resource-row"><div><strong>${escapeHtml(transport.name || "-")}</strong><div class="small muted">${escapeHtml(transport.time || "-")} · ${escapeHtml(transport.depart || "-")}</div></div><div class="form-row"><button class="btn" data-edit-transport="${transport.transId}" data-manager-id="${escapeHtml(managerUserId)}">수정</button><button class="btn danger" data-delete-transport="${transport.transId}" data-manager-id="${escapeHtml(managerUserId)}">삭제</button></div></div>`).join("")
        : `<span class="small muted">등록된 교통 정보가 없습니다.</span>`;
      const imageItems = images.length
        ? images.map((image) => `<div class="hotel-image-item"><img class="hotel-image-thumb" src="/api/hotelimage/image/${image.hotelImageId}" alt="${escapeHtml(image.fileName || hotel.name)}"><div class="small muted">${escapeHtml(image.fileName || `이미지 ${image.hotelImageId}`)}</div><form class="form-row" data-replace-hotel-image="${image.hotelImageId}" data-manager-id="${escapeHtml(managerUserId)}"><input type="file" name="file" accept="image/*" required><button class="btn" type="submit">교체</button><button class="btn danger" type="button" data-delete-hotel-image="${image.hotelImageId}" data-manager-id="${escapeHtml(managerUserId)}">삭제</button></form></div>`).join("")
        : `<span class="small muted">등록된 호텔 이미지가 없습니다.</span>`;
      return `<article class="card"><div class="card-body">
        <div class="toolbar" style="margin:0"><h3>${escapeHtml(hotel.name)}</h3><div class="form-row"><button class="btn" data-edit-hotel="${hotel.hotelId}">수정</button><button class="btn danger" data-delete-hotel="${hotel.hotelId}" data-manager-id="${escapeHtml(managerUserId)}">삭제</button></div></div>
        <p class="muted">${escapeHtml(hotel.address || "")}</p>
        <div class="hotel-resource"><div class="toolbar"><h4>편의시설</h4><div class="form-row"><button class="btn" data-amen-hotel="${hotel.hotelId}" data-manager-id="${escapeHtml(managerUserId)}">태그 수정</button>${amenities?.amenId ? `<button class="btn danger" data-delete-amenity="${amenities.amenId}" data-manager-id="${escapeHtml(managerUserId)}">삭제</button>` : ""}</div></div><div class="form-row">${tags || `<span class="small muted">편의시설 태그 없음</span>`}</div></div>
        <div class="hotel-resource"><div class="toolbar"><h4>교통 정보</h4><button class="btn" data-trans-hotel="${hotel.hotelId}" data-manager-id="${escapeHtml(managerUserId)}">추가</button></div><div class="hotel-resource-list">${transportRows}</div></div>
        <div class="hotel-resource"><div class="toolbar"><h4>호텔 이미지</h4><a class="btn" href="rooms.html?hotelId=${hotel.hotelId}">객실 관리</a></div><div class="hotel-image-grid">${imageItems}</div><form class="form-row section" data-hotel-image-form="${hotel.hotelId}" data-manager-id="${escapeHtml(managerUserId)}"><input type="file" name="file" accept="image/*" required><button class="btn" type="submit">새 이미지 추가</button></form></div>
      </div></article>`;
    }).join("")}</div>` : empty(scope ? "선택한 호텔을 찾을 수 없습니다." : "등록된 호텔이 없습니다.");
    document.querySelectorAll("[data-edit-hotel]").forEach((button) => button.addEventListener("click", () => editHotel(button.dataset.editHotel)));
    document.querySelectorAll("[data-delete-hotel]").forEach((button) => button.addEventListener("click", async () => {
      const userId = requireHotelManagerUserId(button.dataset.managerId);
      if (!userId) { toast("호텔 삭제에는 관리자 사용자 ID가 필요합니다."); return; }
      await request(`/api/hotels/${button.dataset.deleteHotel}?userId=${encodeURIComponent(userId)}`, { method: "DELETE" });
      toast("호텔이 삭제되었습니다.");
      loadAdminHotelsV2();
    }));
    document.querySelectorAll("[data-amen-hotel]").forEach((button) => button.addEventListener("click", () => addAmen(button.dataset.amenHotel, button.dataset.managerId)));
    document.querySelectorAll("[data-delete-amenity]").forEach((button) => button.addEventListener("click", () => deleteAmenity(button.dataset.deleteAmenity, button.dataset.managerId)));
    document.querySelectorAll("[data-trans-hotel]").forEach((button) => button.addEventListener("click", () => addTrans(button.dataset.transHotel, button.dataset.managerId)));
    document.querySelectorAll("[data-edit-transport]").forEach((button) => button.addEventListener("click", () => editTransport(button.dataset.editTransport, button.dataset.managerId)));
    document.querySelectorAll("[data-delete-transport]").forEach((button) => button.addEventListener("click", () => deleteTransport(button.dataset.deleteTransport, button.dataset.managerId)));
    document.querySelectorAll("[data-hotel-image-form]").forEach((form) => form.addEventListener("submit", async (event) => {
      event.preventDefault();
      const userId = requireHotelManagerUserId(form.dataset.managerId);
      if (!userId) return;
      await uploadImage(`/api/hotelimage/hotel/${form.dataset.hotelImageForm}?userId=${encodeURIComponent(userId)}`, form.elements.file.files[0]);
      toast("호텔 이미지가 업로드되었습니다.");
      loadAdminHotelsV2();
    }));
    document.querySelectorAll("[data-replace-hotel-image]").forEach((form) => form.addEventListener("submit", async (event) => {
      event.preventDefault();
      await replaceHotelImage(form.dataset.replaceHotelImage, form.elements.file.files[0], form.dataset.managerId);
    }));
    document.querySelectorAll("[data-delete-hotel-image]").forEach((button) => button.addEventListener("click", () => deleteHotelImage(button.dataset.deleteHotelImage, button.dataset.managerId)));
  } catch (error) {
    document.querySelector("#hotelList").innerHTML = errorMessage(error);
  }
}

async function adminHotels() {
  await adminShell("hotels", `${title("호텔 관리", "호텔 생성·수정·삭제와 백엔드가 지원하는 편의시설 태그, 교통, 이미지를 관리합니다.")}<div class="grid cols-2"><form class="card card-body grid" id="hotelForm">${hotelFormFields()}<button class="btn primary">호텔 추가</button></form><section id="hotelList">${empty("호텔을 불러오는 중입니다.")}</section></div>`);
  document.querySelector("#hotelForm").addEventListener("submit", async (event) => {
    event.preventDefault();
    const data = qs(event.currentTarget);
    data.starRate = Number(data.starRate || 0);
    data.latitude = Number(data.latitude || 0);
    data.longitude = Number(data.longitude || 0);
    data.isActive = true;
    data.userId = data.userId ? Number(data.userId) : null;
    try {
      await request("/api/hotels", { method: "POST", body: JSON.stringify(data) });
      toast("호텔이 추가되었습니다.");
      loadAdminHotelsV2();
    } catch (error) {
      document.querySelector("#hotelList").innerHTML = errorMessage(error);
    }
  });
  loadAdminHotelsV2();
}

async function adminCustomers() {
  await adminShell("customers", `${title("고객 관리", "백엔드가 제공하는 고객 단건 조회·수정·생성·삭제 기능을 연결합니다.")}<section class="grid cols-2"><section class="card card-body grid"><h2>고객 조회</h2><div class="filters"><input id="customerId" type="number" placeholder="고객 ID"><button class="btn primary" id="customerLookup">조회</button></div><div id="customerResult">${empty("고객 ID를 입력하세요.")}</div></section><form class="card card-body grid" id="customerCreateForm"><h2>고객 생성</h2><label><span>이메일</span><input name="email" type="email" required></label><label><span>비밀번호</span><input name="password" type="password" required></label><label><span>이름</span><input name="name" required></label><label><span>전화번호</span><input name="phone"></label><button class="btn primary">고객 생성</button></form></section>`);
  document.querySelector("#customerLookup").addEventListener("click", async () => {
    const id = document.querySelector("#customerId").value;
    if (!id) return;
    try {
      const customer = await request(`/api/users/${id}`);
      document.querySelector("#customerResult").innerHTML = `<form class="grid" id="customerUpdateForm"><input name="userId" type="hidden" value="${customer.userId}"><label><span>이름</span><input name="name" value="${escapeHtml(customer.name || "")}" required></label><label><span>전화번호</span><input name="phone" value="${escapeHtml(customer.phone || "")}"></label><label><span>회원 등급</span><select name="membership"><option ${customer.membership === "NEW_MEMBER" ? "selected" : ""}>NEW_MEMBER</option><option ${customer.membership === "VIP" ? "selected" : ""}>VIP</option><option ${customer.membership === "GOLD" ? "selected" : ""}>GOLD</option></select></label><label><span>상태</span><select name="status"><option ${customer.status === "ACTIVE" ? "selected" : ""}>ACTIVE</option><option ${customer.status === "INACTIVE" ? "selected" : ""}>INACTIVE</option></select></label><div class="form-row"><button class="btn primary">고객 수정</button><button class="btn danger" type="button" id="customerDelete">삭제</button></div></form>`;
      document.querySelector("#customerUpdateForm").addEventListener("submit", async (event) => {
        event.preventDefault();
        const data = { ...customer, ...qs(event.currentTarget), userId: Number(customer.userId) };
        await request("/api/users/update", { method: "PATCH", body: JSON.stringify(data) });
        toast("고객 정보가 수정되었습니다.");
      });
      document.querySelector("#customerDelete").addEventListener("click", async () => {
        await request(`/api/users/delete/${customer.userId}`, { method: "DELETE" });
        document.querySelector("#customerResult").innerHTML = empty("고객이 삭제되었습니다.");
      });
    } catch (error) {
      document.querySelector("#customerResult").innerHTML = errorMessage(error);
    }
  });
  document.querySelector("#customerCreateForm").addEventListener("submit", async (event) => {
    event.preventDefault();
    try {
      await request("/api/users/signup", { method: "POST", body: JSON.stringify({ ...qs(event.currentTarget), role: "CUSTOMER", status: "ACTIVE", membership: "NEW_MEMBER", marketingAgreed: false, point: 0 }) });
      toast("고객이 생성되었습니다.");
      event.currentTarget.reset();
    } catch (error) {
      document.querySelector("#customerResult").innerHTML = errorMessage(error);
    }
  });
}

async function legacyReviewsPageV2(adminMode = false) {
  const currentUser = getCurrentUser();
  if (!adminMode && !currentUser) {
    location.href = `login.html?reason=review&redirect=${encodeURIComponent("reviews.html")}`;
    return;
  }
  const userIdField = adminMode ? `<label><span>사용자 ID</span><input name="userId" type="number" required></label>` : `<input name="userId" type="hidden" value="${escapeHtml(currentUser.userId)}">`;
  await (adminMode ? adminShell : (active, body) => userShell(active, body))(adminMode ? "reviews-admin" : "reviews", `${title(adminMode ? "리뷰 관리" : "리뷰 작성", "백엔드 ReviewDto가 지원하는 여행 유형·태그·카테고리별 평점·사진 경로를 전송합니다.")}<div class="grid cols-2"><form class="card card-body grid" id="reviewForm"><label><span>예약 ID</span><input name="reservationId" type="number" required></label>${userIdField}<label><span>호텔 ID</span><input name="hotelId" type="number" value="${escapeHtml(new URLSearchParams(location.search).get("hotelId") || "")}" required></label><label><span>객실 ID</span><input name="roomId" type="number" required></label><label><span>여행 유형</span><select name="tripType"><option>FAMILY</option><option>COUPLE</option><option>FRIENDS</option><option>BUSINESS</option><option>SOLO</option><option>OTHER</option></select></label><label><span>종합 평점</span><input name="overallRating" type="number" min="1" max="5" value="5" required></label><label><span>제목</span><input name="title" required></label><label><span>사진 경로</span><input name="photos" placeholder="/uploads/review/a.jpg, /uploads/review/b.jpg"><small class="muted">파일 업로드 API는 백엔드에 없어 경로 문자열만 저장됩니다.</small></label><label><span>리뷰 태그</span><select name="tags" multiple><option>CLEAN</option><option>KIND</option><option>GOOD_LOCATION</option><option>QUIET</option><option>GOOD_VALUE</option><option>GOOD_VIEW</option><option>DELICIOUS_BREAKFAST</option><option>EASY_PARKING</option><option>UNCLEAN</option><option>NOISY</option><option>BAD_LOCATION</option><option>EXPENSIVE</option><option>UNKIND</option><option>INCONVENIENT</option></select></label><div class="grid cols-2"><label><span>청결</span><input name="rating_CLEANLINESS" type="number" min="1" max="5" value="5"></label><label><span>서비스</span><input name="rating_SERVICE" type="number" min="1" max="5" value="5"></label><label><span>위치</span><input name="rating_LOCATION" type="number" min="1" max="5" value="5"></label><label><span>시설</span><input name="rating_FACILITY" type="number" min="1" max="5" value="5"></label><label><span>가성비</span><input name="rating_VALUE" type="number" min="1" max="5" value="5"></label><label><span>편안함</span><input name="rating_COMFORT" type="number" min="1" max="5" value="5"></label></div><label style="grid-column:1/-1"><span>내용</span><textarea name="content" required></textarea></label><button class="btn primary">리뷰 저장</button></form><section id="reviewList">${empty("리뷰를 불러오는 중입니다.")}</section></div>`);
  document.querySelector("#reviewForm").addEventListener("submit", async (event) => {
    event.preventDefault();
    const data = qs(event.currentTarget);
    ["reservationId", "userId", "hotelId", "roomId", "overallRating"].forEach((key) => data[key] = Number(data[key]));
    data.viewCount = 0;
    data.likeCount = 0;
    data.dislikeCount = 0;
    data.photos = data.photos ? data.photos.split(",").map((photoPath, index) => ({ photoPath: photoPath.trim(), photoOrder: index + 1 })) : [];
    data.ratings = ["CLEANLINESS", "SERVICE", "LOCATION", "FACILITY", "VALUE", "COMFORT"].map((category) => ({ category, score: Number(data[`rating_${category}`]) })).filter((rating) => rating.score > 0);
    data.tags = Array.from(event.currentTarget.elements.tags.selectedOptions).map((option) => ({ tag: option.value }));
    Object.keys(data).filter((key) => key.startsWith("rating_")).forEach((key) => delete data[key]);
    try {
      await request("/api/review", { method: "POST", body: JSON.stringify(data) });
      toast("리뷰가 저장되었습니다.");
      loadReviews(adminMode);
    } catch (error) {
      document.querySelector("#reviewList").innerHTML = errorMessage(error);
    }
  });
  loadReviews(adminMode);
}

const reviewTagOptions = [
  ["CLEAN", "깨끗해요"],
  ["KIND", "친절해요"],
  ["GOOD_LOCATION", "위치가 좋아요"],
  ["QUIET", "조용해요"],
  ["GOOD_VALUE", "가성비가 좋아요"],
  ["GOOD_VIEW", "전망이 좋아요"],
  ["DELICIOUS_BREAKFAST", "조식이 맛있어요"],
  ["EASY_PARKING", "주차가 편해요"],
  ["UNCLEAN", "청결이 아쉬워요"],
  ["NOISY", "소음이 있어요"],
  ["BAD_LOCATION", "위치가 아쉬워요"],
  ["EXPENSIVE", "가격이 비싸요"],
  ["UNKIND", "응대가 아쉬워요"],
  ["INCONVENIENT", "이용이 불편해요"]
];

const reviewRatingOptions = [
  ["CLEANLINESS", "청결"],
  ["SERVICE", "서비스"],
  ["LOCATION", "위치"],
  ["FACILITY", "시설"],
  ["VALUE", "가성비"],
  ["COMFORT", "편안함"]
];

function ratingOptions(selected = 5) {
  return [5, 4, 3, 2, 1]
    .map((score) => `<option value="${score}" ${score === selected ? "selected" : ""}>${score}점</option>`)
    .join("");
}

function ratingStars(name, label, selected = 5) {
  return `<fieldset class="rating-star-field"><legend>${label}</legend><div class="rating-stars">${[5, 4, 3, 2, 1]
    .map((score) => `<input type="radio" id="${name}_${score}" name="${name}" value="${score}" ${score === selected ? "checked" : ""} required><label for="${name}_${score}" title="${score}점"><span aria-hidden="true">&#9733;</span><span class="sr-only">${score}점</span></label>`)
    .join("")}</div></fieldset>`;
}

async function eligibleReviewBookings(userId) {
  const [bookings, payments, reviews] = await Promise.all([
    request(`/api/bookings/${userId}`).then(pageItems),
    request("/api/payment").then(pageItems),
    request("/api/review?size=200").then(pageItems)
  ]);
  const paidBookingIds = new Set(payments
    .filter((payment) => String(payment.paymentStatus || "").toLowerCase() === "paid")
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

async function uploadReviewPhotos(files) {
  if (!files?.length) return [];
  const body = new FormData();
  Array.from(files).forEach((file) => body.append("files", file));
  return pageItems(await request("/api/review/photos", { method: "POST", body }));
}

async function reviewsPage(adminMode = false) {
  if (adminMode) {
    await adminShell("reviews-admin", `${title("리뷰 관리", "등록된 리뷰를 조회하고 삭제합니다.")}<section id="reviewList">${empty("리뷰를 불러오는 중입니다.")}</section>`);
    loadReviews(true);
    return;
  }

  const currentUser = getCurrentUser();
  if (!currentUser) {
    location.href = `login.html?reason=review&redirect=${encodeURIComponent("reviews.html")}`;
    return;
  }

  userShell("reviews", `${title("리뷰 작성", "결제가 완료된 예약만 리뷰를 작성할 수 있습니다.")}<div class="grid cols-2"><form class="card card-body grid" id="reviewForm"><div id="reviewEligibility">${empty("작성 가능한 예약을 확인하고 있습니다.")}</div><label><span>리뷰를 작성할 숙박</span><select name="reviewBooking" id="reviewBooking" disabled required><option value="">불러오는 중</option></select></label><label><span>여행 유형</span><select name="tripType"><option value="FAMILY">가족 여행</option><option value="COUPLE">커플 여행</option><option value="FRIENDS">친구 여행</option><option value="BUSINESS">출장</option><option value="SOLO">나홀로 여행</option><option value="OTHER">기타</option></select></label>${ratingStars("overallRating", "종합 평점")}<label><span>제목</span><input name="title" required maxlength="200"></label><fieldset class="review-fieldset"><legend>어떤 점이 인상적이었나요?</legend><div class="tag-picker">${reviewTagOptions.map(([value, label]) => `<label class="tag-option"><input type="checkbox" name="reviewTag" value="${value}"><span>${label}</span></label>`).join("")}</div></fieldset><fieldset class="review-fieldset"><legend>항목별 점수</legend><div class="rating-grid">${reviewRatingOptions.map(([category, label]) => ratingStars(`rating_${category}`, label)).join("")}</div></fieldset><label><span>사진 첨부</span><input name="photos" type="file" accept="image/png,image/jpeg,image/webp,image/gif" multiple><small class="muted">이미지 파일을 여러 장 선택할 수 있습니다.</small></label><label style="grid-column:1/-1"><span>내용</span><textarea name="content" required></textarea></label><button class="btn primary" id="reviewSubmit" disabled>리뷰 저장</button></form><section id="reviewList">${empty("리뷰를 불러오는 중입니다.")}</section></div>`);

  const form = document.querySelector("#reviewForm");
  const bookingSelect = document.querySelector("#reviewBooking");
  const submitButton = document.querySelector("#reviewSubmit");
  let eligibleBookings = [];

  try {
    eligibleBookings = await eligibleReviewBookings(currentUser.userId);
    if (!eligibleBookings.length) {
      document.querySelector("#reviewEligibility").innerHTML = empty("작성 가능한 리뷰가 없습니다.", "결제가 완료되고 아직 리뷰를 작성하지 않은 예약이 필요합니다.");
      bookingSelect.innerHTML = `<option value="">작성 가능한 예약 없음</option>`;
    } else {
      const requestedBookingId = Number(new URLSearchParams(location.search).get("bookingId"));
      bookingSelect.innerHTML = eligibleBookings.map((booking, index) => `<option value="${index}" ${booking.reservationId === requestedBookingId ? "selected" : ""}>${escapeHtml(booking.hotelName)} · ${escapeHtml(booking.roomName)} · ${escapeHtml(booking.checkinDate)} ~ ${escapeHtml(booking.checkoutDate)}</option>`).join("");
      bookingSelect.disabled = false;
      submitButton.disabled = false;
      document.querySelector("#reviewEligibility").innerHTML = `<div class="message">결제 완료 예약 ${eligibleBookings.length}건에서 리뷰 대상을 선택할 수 있습니다.</div>`;
    }
  } catch (error) {
    document.querySelector("#reviewEligibility").innerHTML = errorMessage(error);
  }

  form.addEventListener("submit", async (event) => {
    event.preventDefault();
    const selectedBooking = eligibleBookings[Number(bookingSelect.value)];
    if (!selectedBooking) return;
    submitButton.disabled = true;
    try {
      const raw = qs(form);
      const photos = await uploadReviewPhotos(form.elements.photos.files);
      const data = {
        reservationId: selectedBooking.reservationId,
        userId: selectedBooking.userId,
        hotelId: selectedBooking.hotelId,
        roomId: selectedBooking.roomId,
        tripType: raw.tripType,
        overallRating: Number(raw.overallRating),
        title: raw.title,
        content: raw.content,
        viewCount: 0,
        likeCount: 0,
        dislikeCount: 0,
        photos: photos.map((photo, index) => ({ photoPath: photo.photoPath, photoOrder: index + 1 })),
        tags: Array.from(form.querySelectorAll('input[name="reviewTag"]:checked')).map((input) => ({ tag: input.value })),
        ratings: reviewRatingOptions.map(([category]) => ({ category, score: Number(raw[`rating_${category}`]) }))
      };
      await request("/api/review", { method: "POST", body: JSON.stringify(data) });
      toast("리뷰가 저장되었습니다.");
      await loadReviews(false);
      eligibleBookings = eligibleBookings.filter((booking) => booking.reservationId !== selectedBooking.reservationId);
      bookingSelect.innerHTML = eligibleBookings.length
        ? eligibleBookings.map((booking, index) => `<option value="${index}">${escapeHtml(booking.hotelName)} · ${escapeHtml(booking.roomName)} · ${escapeHtml(booking.checkinDate)} ~ ${escapeHtml(booking.checkoutDate)}</option>`).join("")
        : `<option value="">작성 가능한 예약 없음</option>`;
      form.reset();
      submitButton.disabled = !eligibleBookings.length;
      bookingSelect.disabled = !eligibleBookings.length;
      document.querySelector("#reviewEligibility").innerHTML = eligibleBookings.length
        ? `<div class="message">결제 완료 예약 ${eligibleBookings.length}건에서 리뷰 대상을 선택할 수 있습니다.</div>`
        : empty("작성 가능한 리뷰가 없습니다.", "모든 결제 완료 예약에 리뷰를 작성했습니다.");
    } catch (error) {
      document.querySelector("#reviewEligibility").innerHTML = errorMessage(error);
      submitButton.disabled = false;
    }
  });
  loadReviews(false);
}

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
  "seed": seedPage,
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

routes[page]?.();
