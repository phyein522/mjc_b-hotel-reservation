import { request, pageItems, qs, money, escapeHtml, todayMonth } from "./api.js";

const app = document.querySelector("#app");
const page = document.body.dataset.page;
const rel = document.body.dataset.area === "admin" ? "../" : "";
const TOSS_SDK_URL = "https://js.tosspayments.com/v2/standard";
let hotelsCache = [];
let tossSdkPromise = null;

const userNav = [
  ["home", "홈", "index.html"],
  ["search", "숙소 검색", "search.html"],
  ["bookings", "예약내역", "bookings.html"],
  ["coupons", "쿠폰함", "coupons.html"],
  ["reviews", "리뷰", "reviews.html"],
  ["login", "로그인", "login.html"],
  ["signup", "회원가입", "signup.html"]
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
        ${currentUser ? `<span class="pill">${escapeHtml(currentUser.name || currentUser.email || `User ${currentUser.userId}`)}</span><button class="btn" id="logoutBtn" type="button">로그아웃</button>` : `<a class="btn primary" href="${rel}login.html">로그인</a>`}
      </div>
    </header>
    <main class="page">${body}</main>
  `;
  document.querySelector("#logoutBtn")?.addEventListener("click", () => {
    clearCurrentUser();
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
    const [hotel, roomsRes, amenRes, transRes, reviewsRes] = await Promise.all([
      request(`/api/hotels/${id}`),
      request(`/api/room/hotel/${id}?size=100`),
      request(`/api/hotelamenities/hotel/${id}?size=10`).catch(() => null),
      request(`/api/hoteltrans/hotel/${id}?size=20`).catch(() => null),
      request(`/api/review/hotel/${id}?size=20`).catch(() => null)
    ]);
    const rooms = pageItems(roomsRes);
    const amens = pageItems(amenRes);
    const trans = pageItems(transRes);
    const reviews = pageItems(reviewsRes);
    document.querySelector("#detail").innerHTML = `
      <div class="hero"><h1>${escapeHtml(hotel.name)}</h1><p>${escapeHtml(hotel.description || hotel.address || "")}</p></div>
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
        <button class="btn primary">예약 저장</button>
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
  const currentUser = getCurrentUser();
  if (!currentUser) {
    const redirect = encodeURIComponent(`booking.html${location.search}`);
    location.href = `login.html?reason=booking&redirect=${redirect}`;
    return;
  }
  const data = qs(event.currentTarget);
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
    const booking = await request("/api/bookings/insert", { method: "POST", body: JSON.stringify(data) });
    const nights = Math.max(1, Math.ceil((new Date(data.checkoutDate) - new Date(data.checkinDate)) / 86400000));
    const paymentAmount = paymentBaseAmount * nights;
    const roomText = event.currentTarget.elements.roomId?.selectedOptions?.[0]?.textContent.trim() || "";
    const orderName = paymentOrderName || `${roomText} 예약`.trim() || "OmniStay 호텔 예약";
    const paymentParams = new URLSearchParams({
      bookingId: String(booking.bookingId),
      amount: String(paymentAmount),
      orderName
    });
    document.querySelector("#bookingResult").innerHTML = `<div class="message">예약이 저장되었습니다. 예약번호: ${escapeHtml(booking.bookingNo || booking.bookingId)} <a class="btn" href="payment.html?${paymentParams.toString()}">결제 화면</a></div>`;
  } catch (error) {
    document.querySelector("#bookingResult").innerHTML = errorMessage(error);
  }
}

async function paymentPage() {
  const currentUser = getCurrentUser();
  const params = new URLSearchParams(location.search);
  if (!currentUser) {
    const redirect = encodeURIComponent(`payment.html${location.search}`);
    location.href = `login.html?reason=payment&redirect=${redirect}`;
    return;
  }
  const bookingId = params.get("bookingId") || "";
  const totalAmount = Number(params.get("amount") || params.get("totalAmount") || 0);
  const orderName = params.get("orderName") || "OmniStay 호텔 예약";
  const amountView = totalAmount ? money(totalAmount) : "예약 화면에서 결제로 이동하면 자동 입력";
  userShell("bookings", `${title("토스 결제", "예약에서 넘어온 금액과 로그인 회원 정보로 결제를 준비합니다.")}<section class="grid cols-2"><form class="card card-body grid" id="paymentForm"><div class="message">실제 결제 승인/검증은 백엔드 구현 후 처리합니다.<div class="small">예약 화면에서 자동으로 넘어온 정보로 결제합니다.</div></div><input name="bookingId" type="hidden" value="${escapeHtml(bookingId)}"><input name="totalAmount" type="hidden" value="${escapeHtml(totalAmount)}"><input name="orderName" type="hidden" value="${escapeHtml(orderName)}"><section class="message"><div class="toolbar" style="margin:0"><span>결제 예정 금액</span><strong>${escapeHtml(amountView)}</strong></div><div class="small">회원: ${escapeHtml(currentUser.name || currentUser.email || `User ${currentUser.userId}`)}</div></section><label><span>쿠폰 선택 ${screenOnlyBadge()}</span><select name="couponId"><option value="">사용 안 함</option><option value="101">신규회원 10,000원</option><option value="202">장기투숙 15%</option><option value="303">VIP 등급 할인</option></select></label><label><span>할인금액</span><input name="discountAmount" type="number" value="0"></label><label><span>구매자명</span><input name="customerName" value="${escapeHtml(currentUser.name || "")}" placeholder="로그인 정보 자동 입력"></label><label><span>구매자 이메일</span><input name="customerEmail" type="email" value="${escapeHtml(currentUser.email || "")}" placeholder="로그인 정보 자동 입력"></label><label><span>구매자 연락처</span><input name="customerMobilePhone" value="${escapeHtml(currentUser.phone || "")}" placeholder="01012345678"></label><label><span>Toss Client Key</span><input name="tossClientKey" placeholder="test_ck_..."></label><button class="btn primary">토스 요청값 생성</button><button class="btn" id="openTossPayment" type="button" disabled>Toss checkout</button></form><section class="card"><div class="card-body"><div class="toolbar" style="margin:0 0 10px"><h2>백엔드로 넘길 값</h2><span class="status warn">백엔드 미호출</span></div><div id="tossPayloadBox">${empty("토스 요청값을 생성하세요.")}</div><div class="section"><button class="btn" id="copyTossPayload" type="button" disabled>JSON 복사</button></div></div></section></section>`);
  let latestPayload = null;
  let latestClientKey = "";
  document.querySelector("#paymentForm").addEventListener("submit", (event) => {
    event.preventDefault();
    const data = qs(event.currentTarget);
    const totalAmount = Number(data.totalAmount || 0);
    const discountAmount = Number(data.discountAmount || 0);
    if (!data.bookingId || totalAmount <= 0) {
      document.querySelector("#tossPayloadBox").innerHTML = `<div class="message error">예약에서 결제 화면으로 이동해야 결제 정보가 자동 입력됩니다.</div>`;
      return;
    }
    const amount = Math.max(totalAmount - discountAmount, 0);
    const orderId = `OMNISTAY-${data.bookingId}-${Date.now()}`;
    latestClientKey = data.tossClientKey || window.OMNISTAY_TOSS_CLIENT_KEY || "";
    latestPayload = {
      endpoint: "POST /api/payments/toss/ready",
      bookingId: Number(data.bookingId),
      provider: "TOSS",
      paymentMethod: "TOSS_PAYMENTS",
      paymentStatus: "READY",
      orderId,
      orderName: data.orderName,
      amount,
      totalAmount,
      discountAmount,
      currency: "KRW",
      couponId: data.couponId ? Number(data.couponId) : null,
      customerUserId: currentUser?.userId ?? null,
      customerName: data.customerName || currentUser?.name || "",
      customerEmail: data.customerEmail || currentUser?.email || "",
      customerMobilePhone: (data.customerMobilePhone || currentUser?.phone || "").replaceAll("-", ""),
      successUrl: `${location.origin}/omnistay/payment-success.html`,
      failUrl: `${location.origin}/omnistay/payment-fail.html`,
      tossPaymentRequest: {
        method: "CARD",
        amount: {
          value: amount,
          currency: "KRW"
        },
        orderId,
        orderName: data.orderName,
        customerName: data.customerName || currentUser?.name || "",
        customerEmail: data.customerEmail || currentUser?.email || "",
        customerMobilePhone: (data.customerMobilePhone || currentUser?.phone || "").replaceAll("-", ""),
        successUrl: `${location.origin}/omnistay/payment-success.html`,
        failUrl: `${location.origin}/omnistay/payment-fail.html`,
        metadata: {
          bookingId: String(data.bookingId),
          couponId: data.couponId || "",
          discountAmount: String(discountAmount)
        }
      }
    };
    const previewPayload = JSON.parse(JSON.stringify(latestPayload));
    delete previewPayload.bookingId;
    delete previewPayload.orderName;
    delete previewPayload.tossPaymentRequest.orderName;
    delete previewPayload.tossPaymentRequest.metadata.bookingId;
    document.querySelector("#tossPayloadBox").innerHTML = `<pre class="code-block">${escapeHtml(JSON.stringify(previewPayload, null, 2))}</pre>`;
    document.querySelector("#copyTossPayload").disabled = false;
    document.querySelector("#openTossPayment").disabled = !latestClientKey;
    toast("토스 결제 요청값을 생성");
  });
  document.querySelector("#copyTossPayload").addEventListener("click", async () => {
    if (!latestPayload) return;
    await navigator.clipboard.writeText(JSON.stringify(latestPayload, null, 2));
    toast("JSON을 복사");
  });
  document.querySelector("#openTossPayment").addEventListener("click", async () => {
    if (!latestPayload) return;
    if (!latestClientKey) {
      toast("Toss Client Key를 입력");
      return;
    }
    try {
      const TossPayments = await loadTossPaymentsSdk();
      const customerKey = currentUser?.userId ? `USER_${currentUser.userId}` : `BOOKING_${latestPayload.bookingId}`;
      const payment = TossPayments(latestClientKey).payment({ customerKey });
      await payment.requestPayment(latestPayload.tossPaymentRequest);
    } catch (error) {
      document.querySelector("#tossPayloadBox").insertAdjacentHTML("beforeend", `<div class="message error section">${escapeHtml(error.message || String(error))}</div>`);
    }
  });
}

function paymentResultPage(status) {
  const params = new URLSearchParams(location.search);
  const orderId = params.get("orderId") || "-";
  const paymentKey = params.get("paymentKey") || "-";
  const amount = params.get("amount") || "-";
  const isSuccess = status === "success";
  userShell("bookings", `${title(isSuccess ? "결제 성공" : "결제 실패", "토스 결제 redirect 후 표시되는 화면입니다.")}<section class="card"><div class="card-body grid"><span class="status ${isSuccess ? "ok" : "bad"}">${isSuccess ? "SUCCESS" : "FAIL"}</span><div class="table-wrap"><table><tbody><tr><th>orderId</th><td>${escapeHtml(orderId)}</td></tr><tr><th>paymentKey</th><td>${escapeHtml(paymentKey)}</td></tr><tr><th>amount</th><td>${escapeHtml(amount)}</td></tr></tbody></table></div><div class="message">${isSuccess ? "백엔드 구현 후 이 값들을 confirm API로 전달해 최종 승인 검증을 진행하면 됩니다." : "백엔드 구현 후 실패 사유를 저장하거나 예약 결제 상태를 갱신하면 됩니다."}</div><a class="btn primary" href="bookings.html">예약내역</a></div></section>`);
}

async function loadPayments(selector, admin = false) {
  try {
    const payments = pageItems(await request("/api/payments"));
    document.querySelector(selector).innerHTML = payments.length ? `<div class="table-wrap"><table><thead><tr><th>ID</th><th>예약</th><th>상태</th><th>금액</th><th>수단</th>${admin ? "<th></th>" : ""}</tr></thead><tbody>${payments.map((p) => `<tr><td>${p.paymentId}</td><td>${p.bookingId || p.booking?.bookingId || "-"}</td><td>${escapeHtml(p.paymentStatus)}</td><td>${money(p.totalAmount)}</td><td>${escapeHtml(p.paymentMethod)}</td>${admin ? `<td><button class="btn danger" data-delete-payment="${p.paymentId}">삭제</button></td>` : ""}</tr>`).join("")}</tbody></table></div>` : empty("등록된 결제가 없습니다.");
    document.querySelectorAll("[data-delete-payment]").forEach((btn) => btn.addEventListener("click", async () => {
      await request(`/api/payments/${btn.dataset.deletePayment}`, { method: "DELETE" });
      toast("삭제되었습니다.");
      loadPayments(selector, admin);
    }));
  } catch (error) {
    document.querySelector(selector).innerHTML = errorMessage(error);
  }
}

async function bookingsPage() {
  userShell("bookings", `${title("예약내역", "사용자 ID별 예약 조회는 백엔드 API에 연결")}<div class="filters"><input id="bookingUserId" type="number" placeholder="사용자 ID"><button class="btn primary" id="bookingLookup">조회</button></div><section class="section" id="bookingList"></section><section class="section card"><div class="card-body"><div class="toolbar" style="margin:0 0 10px"><h2>예약 상세 흐름</h2>${screenOnlyBadge()}</div><div class="grid cols-3"><div class="metric">예약 접수<strong>일정/인원</strong></div><div class="metric">결제 대기<strong>쿠폰 선택</strong></div><div class="metric">투숙 완료<strong>리뷰 작성</strong></div></div></div></section>`);
  document.querySelector("#bookingLookup").addEventListener("click", () => loadBookings("#bookingList", document.querySelector("#bookingUserId").value, false));
}

function couponsPage() {
  userShell("coupons", `${title("쿠폰함", "쿠폰 전용 백엔드 API가 없어 화면 전용으로 표시")}<section class="grid cols-3"><article class="card"><div class="card-body"><div class="toolbar" style="margin:0 0 8px"><h3>신규회원 웰컴 쿠폰</h3>${screenOnlyBadge()}</div><p class="price">10,000원 할인</p><p class="muted">7만원 이상 결제 시 사용</p><a class="btn primary" href="payment.html">결제에서 사용</a></div></article><article class="card"><div class="card-body"><div class="toolbar" style="margin:0 0 8px"><h3>장기 투숙 쿠폰</h3>${screenOnlyBadge()}</div><p class="price">15% 할인</p><p class="muted">7박 이상 예약 시 사용</p><a class="btn primary" href="payment.html">결제에서 사용</a></div></article><article class="card"><div class="card-body"><div class="toolbar" style="margin:0 0 8px"><h3>VIP 등급 할인</h3>${screenOnlyBadge()}</div><p class="price">10% 할인</p><p class="muted">VIP 회원 등급 대상</p><a class="btn primary" href="payment.html">결제에서 사용</a></div></article></section>`);
}

function loginPage() {
  const params = new URLSearchParams(location.search);
  const redirect = params.get("redirect") || "index.html";
  const reason = params.get("reason");
  userShell("login", `${title("로그인", "예약은 로그인 상태에서만 진행 가능")}<section class="grid cols-2"><form class="card card-body grid" id="loginForm"><div class="toolbar" style="margin:0"><h2>사용자 ID 로그인</h2><span class="status ok">회원조회 API</span></div>${reason === "booking" ? `<div class="message error">로그인되어 있지 않아 예약을 진행할 수 없습니다. 로그인 후 예약 화면으로 돌아갑니다.</div>` : ""}<label><span>사용자 ID</span><input name="userId" type="number" placeholder="예: 10" required></label><button class="btn primary">로그인</button><a class="small muted" href="signup.html">아직 회원이 아니면 회원가입</a></form><article class="card"><div class="card-body"><h2>예약에 사용되는 정보</h2><p class="muted">로그인하면 회원 API에서 가져온 이름, 전화번호, 이메일이 예약자 정보로 자동 입력됩니다.</p><div class="grid"><span class="pill">사용자 ID</span><span class="pill">이름</span><span class="pill">연락처</span><span class="pill">이메일</span></div></div></article></section><div class="section" id="loginResult"></div>`);
  document.querySelector("#loginForm").addEventListener("submit", async (event) => {
    event.preventDefault();
    const { userId } = qs(event.currentTarget);
    try {
      const user = await request(`/api/users/${userId}`);
      setCurrentUser(user);
      toast("로그인되었습니다.");
      location.href = redirect;
    } catch (error) {
      document.querySelector("#loginResult").innerHTML = errorMessage(error);
    }
  });
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

async function reviewsPage(adminMode = false) {
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
  userShell("signup", `${title("회원가입", "구현된 /api/users/signup API에 연결")}<form class="card card-body grid" id="signupForm"><label><span>이메일</span><input name="email" type="email" required></label><label><span>비밀번호</span><input name="password" type="password" required></label><label><span>이름</span><input name="name" required></label><label><span>전화번호</span><input name="phone"></label><button class="btn primary">가입</button></form><div class="section" id="signupResult"></div>`);
  document.querySelector("#signupForm").addEventListener("submit", async (event) => {
    event.preventDefault();
    const data = { ...qs(event.currentTarget), role: "CUSTOMER", status: "ACTIVE", membership: "NEW_MEMBER", marketingAgreed: false, point: 0 };
    try {
      const user = await request("/api/users/signup", { method: "POST", body: JSON.stringify(data) });
      setCurrentUser(user);
      document.querySelector("#signupResult").innerHTML = `<div class="message">회원이 저장되고 로그인되었습니다. 사용자 ID: ${user.userId} <a class="btn primary" href="booking.html">예약하기</a></div>`;
    } catch (error) {
      document.querySelector("#signupResult").innerHTML = errorMessage(error);
    }
  });
}

async function adminDashboard() {
  await adminShell("dashboard", `${title("관리자 대시보드", "선택한 호텔 또는 전체 호텔의 API 데이터를 요약합니다.")}<section id="dashboard">${empty("불러오는 중입니다.")}</section>`);
  try {
    const hotels = await safeLoadHotels();
    const scope = getHotelScope();
    const visibleHotels = scope ? hotels.filter((h) => String(h.hotelId) === String(scope)) : hotels;
    const [payments, promotions, reviews] = await Promise.all([
      request("/api/payments").then(pageItems).catch(() => []),
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

async function adminHotels() {
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
    document.querySelector("#hotelList").innerHTML = hotels.length ? `<div class="grid">${hotels.map((h) => `<article class="card"><div class="card-body"><div class="toolbar" style="margin:0"><h3>${escapeHtml(h.name)}</h3><button class="btn danger" data-delete-hotel="${h.hotelId}">삭제</button></div><p class="muted">${escapeHtml(h.address || "")}</p><div class="form-row"><a class="btn" href="rooms.html?hotelId=${h.hotelId}">객실</a><button class="btn" data-amen-hotel="${h.hotelId}">편의시설 태그</button><button class="btn" data-trans-hotel="${h.hotelId}">교통 추가</button></div></div></article>`).join("")}</div>` : empty("호텔 데이터가 없습니다.");
    document.querySelectorAll("[data-delete-hotel]").forEach((btn) => btn.addEventListener("click", async () => {
      await request(`/api/hotels/${btn.dataset.deleteHotel}`, { method: "DELETE" });
      toast("호텔이 삭제되었습니다.");
      loadAdminHotels();
    }));
    document.querySelectorAll("[data-amen-hotel]").forEach((btn) => btn.addEventListener("click", () => addAmen(btn.dataset.amenHotel)));
    document.querySelectorAll("[data-trans-hotel]").forEach((btn) => btn.addEventListener("click", () => addTrans(btn.dataset.transHotel)));
  } catch (error) {
    document.querySelector("#hotelList").innerHTML = errorMessage(error);
  }
}

async function addAmen(hotelId) {
  const selected = prompt("편의시설 태그를 쉼표로 입력: wifi,pool,breakfast,freeParking");
  if (!selected) return;
  const body = { hotelId: Number(hotelId) };
  selected.split(",").map((v) => v.trim()).filter(Boolean).forEach((key) => body[key] = true);
  await request("/api/hotelamenities", { method: "POST", body: JSON.stringify(body) });
  toast("편의시설이 저장되었습니다.");
}

async function addTrans(hotelId) {
  const name = prompt("교통 이름");
  if (!name) return;
  const time = prompt("소요 시간") || "";
  const depart = prompt("출발/위치") || "";
  await request("/api/hoteltrans", { method: "POST", body: JSON.stringify({ hotelId: Number(hotelId), name, time, depart }) });
  toast("교통 정보가 저장되었습니다.");
}

async function adminRooms() {
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
    document.querySelector("#roomsArea").innerHTML = `<div class="room-board">${rooms.map((r) => `<article class="room-cell ${String(r.roomStatus).toLowerCase().includes("enable") ? "enable" : String(r.roomStatus).toLowerCase().includes("construct") ? "construct" : "disable"}"><div><strong>${escapeHtml(r.number)}호</strong><div class="small muted">${escapeHtml(r.floor)}층 · ${escapeHtml(r.roomType)}</div></div><div><div class="price">${money(r.basePrice)}</div>${statusBadge(r.roomStatus)}<div class="form-row" style="margin-top:8px"><button class="btn" data-edit-room="${r.roomId}">수정</button><button class="btn danger" data-delete-room="${r.roomId}">삭제</button></div></div></article>`).join("") || empty("등록된 객실이 없습니다.")}</div>`;
    document.querySelectorAll("[data-delete-room]").forEach((btn) => btn.addEventListener("click", async () => {
      await request(`/api/room/${btn.dataset.deleteRoom}`, { method: "DELETE" });
      toast("객실이 삭제되었습니다.");
      loadRooms(hotelId);
    }));
    document.querySelectorAll("[data-edit-room]").forEach((btn) => btn.addEventListener("click", () => editRoom(btn.dataset.editRoom, hotelId)));
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

async function adminRoomAdd() {
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

async function adminCustomers() {
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
  await adminShell("promotions", `${title("프로모션 관리", "회원 등급 할인과 장기 투숙 할인은 구현된 promotion API의 이름/설명/할인값으로 저장합니다.")}<div class="grid cols-2"><form class="card card-body grid" id="promoForm"><label><span>이름</span><input name="name" required></label><label><span>설명</span><textarea name="description" placeholder="VIP 등급 할인 또는 장기 투숙 할인"></textarea></label><label><span>할인 타입</span><select name="disType"><option>RATE</option><option>AMOUNT</option><option>PACKAGE</option></select></label><label><span>할인값</span><input name="disValue" required></label><label><span>시작</span><input name="startDate" type="datetime-local"></label><label><span>종료</span><input name="endDate" type="datetime-local"></label><label><span>예약횟수</span><input name="resCount" type="number" value="0"></label><label><span>상태</span><input name="status" value="ACTIVE"></label><label><span>객실 ID</span><input name="roomId" type="number"></label><button class="btn primary">프로모션 추가</button></form><section id="promoList">${empty("불러오는 중입니다.")}</section></div>`);
  document.querySelector("#promoForm").addEventListener("submit", async (event) => {
    event.preventDefault();
    const data = qs(event.currentTarget);
    data.resCount = Number(data.resCount || 0);
    if (data.roomId) data.roomId = Number(data.roomId); else delete data.roomId;
    if (data.startDate) data.startDate = `${data.startDate}:00`;
    if (data.endDate) data.endDate = `${data.endDate}:00`;
    await request("/api/promotion", { method: "POST", body: JSON.stringify(data) });
    toast("프로모션이 추가되었습니다.");
    loadPromotions();
  });
  loadPromotions();
}

async function loadPromotions() {
  try {
    const promotions = pageItems(await request("/api/promotion"));
    document.querySelector("#promoList").innerHTML = promotions.length ? `<div class="table-wrap"><table><thead><tr><th>ID</th><th>이름</th><th>할인</th><th>상태</th><th></th></tr></thead><tbody>${promotions.map((p) => `<tr><td>${p.proId}</td><td>${escapeHtml(p.name)}</td><td>${escapeHtml(p.disType)} ${escapeHtml(p.disValue)}</td><td>${escapeHtml(p.status)}</td><td><button class="btn danger" data-delete-promo="${p.proId}">삭제</button></td></tr>`).join("")}</tbody></table></div>` : empty("프로모션 데이터가 없습니다.");
    document.querySelectorAll("[data-delete-promo]").forEach((btn) => btn.addEventListener("click", async () => {
      await request(`/api/promotion/${btn.dataset.deletePromo}`, { method: "DELETE" });
      loadPromotions();
    }));
  } catch (error) {
    document.querySelector("#promoList").innerHTML = errorMessage(error);
  }
}

async function adminPayments() {
  await adminShell("payments", `${title("결제 관리", "구현된 결제 목록/추가/삭제 API에 연결합니다.")}<div id="adminPayments">${empty("불러오는 중입니다.")}</div><section class="section"><a class="btn primary" href="../payment.html">결제 추가</a></section>`);
  loadPayments("#adminPayments", true);
}

async function adminRates() {
  await adminShell("rates", `${title("요금 정책", "구현된 rates API만 연결합니다.")}<div class="filters"><select id="rateHotel"></select><select id="rateRoomType"><option>Standard</option><option>Deluxe</option><option>Suite</option><option>Premium</option></select><button class="btn primary" id="loadRates">조회</button></div><section class="section" id="ratesArea"></section>`);
  const hotels = await safeLoadHotels();
  document.querySelector("#rateHotel").innerHTML = hotels.map((h) => `<option value="${h.hotelId}">${escapeHtml(h.name)}</option>`).join("");
  document.querySelector("#loadRates").addEventListener("click", loadRates);
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
    document.querySelector("#ratesArea").innerHTML = `<div class="grid cols-3"><div class="metric">요금 요약<strong>${escapeHtml(summary.error || "조회됨")}</strong></div><div class="metric">정책<strong>${escapeHtml(policy.error || `${policy.minStayNights ?? "-"}박`)}</strong></div><div class="metric">시즌 요금<strong>${pageItems(seasons).length}</strong></div></div>`;
  } catch (error) {
    document.querySelector("#ratesArea").innerHTML = errorMessage(error);
  }
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
      const user = await request("/api/users/signup", { method: "POST", body: JSON.stringify({ email: `guest${Date.now()}@omnistay.test`, password: "1234", name: "테스트 고객", phone: "010-0000-0000", role: "CUSTOMER", status: "ACTIVE", membership: "NEW_MEMBER", marketingAgreed: false, point: 0 }) });
      const hotel = await request("/api/hotels", { method: "POST", body: JSON.stringify({ name: "그랜드 서울", description: "서울 중심의 비즈니스 호텔", address: "서울 중구 세종대로 1", city: "서울", zipCode: "04524", phone: "02-1000-1000", email: "grand@omnistay.test", checkIn: "15:00", checkOut: "11:00", starRate: 5, isActive: true, latitude: 37.5665, longitude: 126.978, type: "HOTEL", userId: user.userId }) });
      await request("/api/hotelamenities", { method: "POST", body: JSON.stringify({ hotelId: hotel.hotelId, wifi: true, breakfast: true, fitnessCenter: true, freeParking: true, concierge: true }) });
      await request("/api/hoteltrans", { method: "POST", body: JSON.stringify({ hotelId: hotel.hotelId, name: "시청역", time: "도보 5분", depart: "1번 출구" }) });
      const room = await request("/api/room", { method: "POST", body: JSON.stringify({ hotelId: hotel.hotelId, name: "디럭스 더블", number: "1201", floor: 12, size: 32, basePrice: 180000, maxAdult: 2, maxChild: 1, isActive: true, roomType: "Deluxe", roomStatus: "EnableReservation", roomViewOption: "CityView", roomBedOption: "DoubleBed" }) });
      const booking = await request("/api/bookings/insert", { method: "POST", body: JSON.stringify({ userId: user.userId, roomId: room.roomId, guestName: user.name, nationality: "KOREA", guestPhone: user.phone, guestEmail: user.email, specialRequest: "고층 선호", adultCount: 2, childCount: 0, checkinDate: "2026-08-10", checkoutDate: "2026-08-12" }) });
      const payment = await request("/api/payments/add", { method: "POST", body: JSON.stringify({ bookingId: booking.bookingId, paymentMethod: "CreditCard", paymentStatus: "Paid", totalAmount: 360000, currency: "KRW", discountAmount: 0 }) }).catch(() => null);
      await request("/api/promotion", { method: "POST", body: JSON.stringify({ name: "VIP 회원 등급 할인", description: "회원 등급에 따른 할인", disType: "RATE", disValue: "10", startDate: "2026-08-01T00:00:00", endDate: "2026-12-31T23:59:00", resCount: 0, status: "ACTIVE", roomId: room.roomId }) });
      log.innerHTML = `<div class="message">DB 데이터가 추가되었습니다. 호텔 ID ${hotel.hotelId}, 객실 ID ${room.roomId}, 사용자 ID ${user.userId}, 예약 ID ${booking.bookingId}${payment ? "" : "<div class=\"small\">결제 저장은 현재 백엔드 DTO 오류로 건너뛰었습니다.</div>"}</div>`;
    } catch (error) {
      log.innerHTML = errorMessage(error);
    }
  });
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
