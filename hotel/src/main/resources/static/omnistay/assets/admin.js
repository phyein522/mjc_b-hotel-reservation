import { request, pageItems, qs, money, escapeHtml, todayMonth } from "./api.js";
import {
  adminShell,
  title,
  empty,
  errorMessage,
  toast,
  getCurrentUser,
  getHotelScope,
  safeLoadHotels,
  roomRow,
  statusBadge,
  screenOnlyBadge,
  hotelImageUrl
} from "./core.js";
import { loadBookings, loadPayments } from "./bookings-payments.js";

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

function hotelFormFields() {
  return `<label><span>호텔명</span><input name="name" required></label><label><span>설명</span><textarea name="description"></textarea></label><div class="grid cols-2"><label><span>도시</span><input name="city"></label><label><span>주소</span><input name="address"></label><label><span>우편번호</span><input name="zipCode"></label><label><span>전화</span><input name="phone"></label><label><span>이메일</span><input name="email" type="email"></label><label><span>등급</span><input name="starRate" type="number" min="1" max="5"></label><label><span>위도</span><input name="latitude" type="number" step="0.000001" required></label><label><span>경도</span><input name="longitude" type="number" step="0.000001" required></label><label><span>체크인</span><input name="checkIn" type="time"></label><label><span>체크아웃</span><input name="checkOut" type="time"></label><label><span>유형</span><select name="type"><option>HOTEL</option><option>RESORT</option><option>PENSION_GUESTHOUSE</option></select></label><label><span>관리자 사용자 ID</span><input name="userId" type="number"></label></div>`;
}

async function uploadImage(path, file, method = "POST") {
  const body = new FormData();
  body.append("file", file);
  return request(path, { method, body });
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

async function adminReservations() {
  await adminShell("reservations", `${title("예약 조회", "사용자 ID별 예약 조회와 취소 기능을 제공합니다.")}<section><div class="filters"><input id="adminBookingUserId" type="number" placeholder="사용자 ID"><button class="btn primary" id="adminBookingLookup">조회</button></div><div class="section" id="adminBookings">${empty("조회할 사용자 ID를 입력하세요.")}</div></section>`);
  document.querySelector("#adminBookingLookup").addEventListener("click", () => loadBookings("#adminBookings", document.querySelector("#adminBookingUserId").value, true));
}

async function adminCheckins() {
  await adminShell("checkins", `${title("체크인 현황", "체크인 API가 구현되면 실제 현황을 표시합니다.")}<section>${empty("연결된 체크인 데이터가 없습니다.", "현재 백엔드에 체크인 조회 API가 구현되어 있지 않습니다.")}</section>`);
}

async function adminSettlement() {
  await adminShell("settlement", `${title("정산 리포트", "정산 API가 구현되면 실제 리포트를 표시합니다.")}<section>${empty("연결된 정산 데이터가 없습니다.", "현재 백엔드에 정산 조회 API가 구현되어 있지 않습니다.")}</section>`);
}

async function adminPromotions() {
  const managerUserId = getCurrentUser()?.userId || "";
  await adminShell("promotions", `${title("프로모션 관리", "프로모션, 쿠폰, 회원별 쿠폰 API를 연결합니다.")}<div class="grid cols-2"><form class="card card-body grid" id="promoForm"><div class="toolbar" style="margin:0"><h2>프로모션 추가</h2><span class="status ok">API 연결</span></div><label><span>이름</span><input name="name" required></label><label><span>설명</span><textarea name="description" placeholder="프로모션 설명"></textarea></label><label><span>할인 타입</span><select name="disType"><option>RATE</option><option>AMOUNT</option><option>PACKAGE</option></select></label><label><span>할인값</span><input name="disValue" required></label><label><span>시작</span><input name="startDate" type="datetime-local"></label><label><span>종료</span><input name="endDate" type="datetime-local"></label><label><span>예약횟수</span><input name="resCount" type="number" value="0"></label><label><span>상태</span><select name="status"><option>ACTIVE</option><option>INACTIVE</option><option>EXPIRED</option></select></label><label><span>객실 ID</span><input name="roomId" type="number"></label><label><span>관리자 사용자 ID</span><input name="userId" type="number" value="${managerUserId}"></label><button class="btn primary">프로모션 추가</button></form><section id="promoList">${empty("불러오는 중입니다.")}</section><form class="card card-body grid" id="promoSaleForm"><div class="toolbar" style="margin:0"><h2>프로모션 적용 대상</h2><span class="status ok">API 연결</span></div><label><span>프로모션</span><select name="proId" id="promoSaleProId"></select></label><label><span>대상 설명</span><input name="saleDes" placeholder="적용 대상 설명" required></label><label><span>사용자 ID</span><input name="userId" type="number" value="${managerUserId}"></label><button class="btn primary">적용 대상 추가</button></form><section id="promoSaleList">${empty("불러오는 중입니다.")}</section><form class="card card-body grid" id="couponForm"><div class="toolbar" style="margin:0"><h2>쿠폰 등록</h2><span class="status ok">API 연결</span></div><label><span>코드</span><input name="code" required></label><label><span>이름</span><input name="name" required></label><label><span>설명</span><textarea name="description"></textarea></label><label><span>할인 타입</span><select name="discountType"><option>FIXED</option><option>RATE</option></select></label><label><span>할인값</span><input name="discountValue" type="number" min="1" required></label><label><span>최소 주문</span><input name="minOrder" type="number" value="0"></label><label><span>최대 할인</span><input name="maxDiscount" type="number" value="0"></label><label><span>만료일</span><input name="expirationDate" type="date" required></label><label><span>상태</span><select name="status"><option>ACTIVE</option><option>USED</option><option>EXPIRED</option></select></label><label><span>관리자 사용자 ID</span><input name="userId" type="number" min="1" value="${managerUserId}" required></label><button class="btn primary">쿠폰 등록</button></form><section id="adminCouponList">${empty("불러오는 중입니다.")}</section><form class="card card-body grid" id="userCouponForm"><div class="toolbar" style="margin:0"><h2>회원 쿠폰 발급</h2><span class="status ok">관리자 API</span></div><label><span>대상 사용자 ID</span><input name="targetUserId" type="number" min="1" required></label><label><span>쿠폰</span><select name="couponId" id="userCouponCouponId" required></select></label><label><span>관리자 사용자 ID</span><input name="managerUserId" id="userCouponManagerId" type="number" min="1" value="${managerUserId}" required></label><button class="btn primary">회원에게 발급</button></form><section id="adminUserCouponList">${empty("회원 쿠폰을 불러오는 중입니다.")}</section></div>`);
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
    const managerUserId = document.querySelector("#userCouponManagerId")?.value || getCurrentUser()?.userId || "";
    const saleSelect = document.querySelector("#promoSaleProId");
    if (saleSelect) {
      saleSelect.innerHTML = promotions.map((p) => `<option value="${p.proId}">${escapeHtml(p.name)}</option>`).join("");
    }
    const userCouponSelect = document.querySelector("#userCouponCouponId");
    if (userCouponSelect) {
      userCouponSelect.innerHTML = coupons.map((coupon) => `<option value="${coupon.couponId}">${escapeHtml(coupon.name)} (${escapeHtml(coupon.code || "-")})</option>`).join("");
    }
    document.querySelector("#promoList").innerHTML = promotions.length ? `<div class="table-wrap"><table><thead><tr><th>ID</th><th>이름</th><th>할인</th><th>상태</th><th></th></tr></thead><tbody>${promotions.map((p) => `<tr><td>${p.proId}</td><td>${escapeHtml(p.name)}</td><td>${escapeHtml(p.disType)} ${escapeHtml(p.disValue)}</td><td>${escapeHtml(p.status)}</td><td><div class="actions"><button class="btn" data-edit-promo="${p.proId}">수정</button><button class="btn danger" data-delete-promo="${p.proId}" data-user-id="${escapeHtml(p.userId || p.user?.userId || managerUserId)}">삭제</button></div></td></tr>`).join("")}</tbody></table></div>` : empty("프로모션 데이터가 없습니다.");
    document.querySelector("#promoSaleList").innerHTML = promotionSales.length ? `<div class="table-wrap"><table><thead><tr><th>ID</th><th>프로모션</th><th>대상</th><th>사용자</th><th></th></tr></thead><tbody>${promotionSales.map((sale) => `<tr><td>${sale.proSaleId}</td><td>${escapeHtml(sale.promotion?.name || sale.proId || "-")}</td><td>${escapeHtml(sale.saleDes || "-")}</td><td>${escapeHtml(sale.userId || "-")}</td><td><div class="actions"><button class="btn" data-edit-promo-sale="${sale.proSaleId}">수정</button><button class="btn danger" data-delete-promo-sale="${sale.proSaleId}" data-user-id="${escapeHtml(sale.userId || managerUserId)}">삭제</button></div></td></tr>`).join("")}</tbody></table></div>` : empty("프로모션 적용 대상이 없습니다.");
    document.querySelector("#adminCouponList").innerHTML = coupons.length ? `<div class="table-wrap"><table><thead><tr><th>ID</th><th>쿠폰</th><th>할인</th><th>만료</th><th>상태</th><th></th></tr></thead><tbody>${coupons.map((coupon) => `<tr><td>${coupon.couponId}</td><td>${escapeHtml(coupon.name)}<div class="small muted">${escapeHtml(coupon.code || "")}</div></td><td>${escapeHtml(coupon.discountType)} ${escapeHtml(coupon.discountValue)}</td><td>${escapeHtml(coupon.expirationDate || "-")}</td><td>${escapeHtml(coupon.status || "-")}</td><td><div class="actions"><button class="btn" data-edit-coupon="${coupon.couponId}">수정</button><button class="btn danger" data-delete-coupon="${coupon.couponId}" data-user-id="${escapeHtml(coupon.userId || coupon.user?.userId || managerUserId)}">삭제</button></div></td></tr>`).join("")}</tbody></table></div>` : empty("등록된 쿠폰이 없습니다.");
    document.querySelector("#adminUserCouponList").innerHTML = userCoupons.length ? `<div class="table-wrap"><table><thead><tr><th>ID</th><th>회원</th><th>쿠폰</th><th>상태</th><th>사용 결제 ID</th><th></th></tr></thead><tbody>${userCoupons.map((item) => `<tr><td>${item.userCouponId}</td><td>${escapeHtml(item.user?.name || item.userId || "-")}<div class="small muted">ID ${escapeHtml(item.userId || item.user?.userId || "-")}</div></td><td>${escapeHtml(item.coupon?.name || item.couponId || "-")}</td><td><select data-user-coupon-status="${item.userCouponId}"><option ${item.userCouponStatus === "AVAILABLE" ? "selected" : ""}>AVAILABLE</option><option ${item.userCouponStatus === "USED" ? "selected" : ""}>USED</option><option ${item.userCouponStatus === "EXPIRED" ? "selected" : ""}>EXPIRED</option></select></td><td><input data-used-payment-id="${item.userCouponId}" type="number" min="1" value="${escapeHtml(item.usedPaymentId || "")}" placeholder="선택"></td><td><div class="actions"><button class="btn" data-update-user-coupon="${item.userCouponId}" data-target-user-id="${item.userId || item.user?.userId || ""}" data-coupon-id="${item.couponId || item.coupon?.couponId || ""}">상태 저장</button><button class="btn danger" data-delete-user-coupon="${item.userCouponId}">삭제</button></div></td></tr>`).join("")}</tbody></table></div>` : empty("발급된 회원 쿠폰이 없습니다.");
    document.querySelectorAll("[data-delete-promo]").forEach((btn) => btn.addEventListener("click", async () => {
      const userId = requireHotelManagerUserId(btn.dataset.userId);
      if (!userId) return;
      await request(`/api/promotion/${btn.dataset.deletePromo}?userId=${encodeURIComponent(userId)}`, { method: "DELETE" });
      loadPromotions();
    }));
    document.querySelectorAll("[data-edit-promo]").forEach((btn) => btn.addEventListener("click", async () => {
      const promotion = await request(`/api/promotion/${btn.dataset.editPromo}`);
      const name = prompt("프로모션 이름", promotion.name || "");
      if (name == null) return;
      const disValue = prompt("할인값", promotion.disValue ?? "");
      if (disValue == null) return;
      const status = prompt("상태: ACTIVE, INACTIVE, EXPIRED", promotion.status || "ACTIVE");
      if (status == null) return;
      await request("/api/promotion", { method: "PATCH", body: JSON.stringify({ ...promotion, name, disValue: Number(disValue), status }) });
      toast("프로모션이 수정되었습니다.");
      loadPromotions();
    }));
    document.querySelectorAll("[data-delete-promo-sale]").forEach((btn) => btn.addEventListener("click", async () => {
      const userId = requireHotelManagerUserId(btn.dataset.userId);
      if (!userId) return;
      await request(`/api/promotionsale/${btn.dataset.deletePromoSale}?userId=${encodeURIComponent(userId)}`, { method: "DELETE" });
      loadPromotions();
    }));
    document.querySelectorAll("[data-edit-promo-sale]").forEach((btn) => btn.addEventListener("click", async () => {
      const sale = await request(`/api/promotionsale/${btn.dataset.editPromoSale}`);
      const proId = prompt("프로모션 ID", sale.proId || sale.promotion?.proId || "");
      if (proId == null) return;
      const saleDes = prompt("적용 대상 설명", sale.saleDes || "");
      if (saleDes == null) return;
      await request("/api/promotionsale", { method: "PATCH", body: JSON.stringify({ ...sale, proId: Number(proId), saleDes }) });
      toast("프로모션 적용 대상이 수정되었습니다.");
      loadPromotions();
    }));
    document.querySelectorAll("[data-delete-coupon]").forEach((btn) => btn.addEventListener("click", async () => {
      const userId = requireHotelManagerUserId(btn.dataset.userId);
      if (!userId) return;
      await request(`/api/coupons/${btn.dataset.deleteCoupon}?userId=${encodeURIComponent(userId)}`, { method: "DELETE" });
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
      const userId = requireHotelManagerUserId(document.querySelector("#userCouponManagerId")?.value || coupon.userId || coupon.user?.userId);
      if (!userId) return;
      await request("/api/coupons", {
        method: "PATCH",
        body: JSON.stringify({
          ...coupon,
          name,
          discountValue: Number(discountValue),
          expirationDate,
          userId: Number(userId)
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

async function loadRateRooms(hotelId, selector = "#roomsArea") {
  if (!hotelId) {
    document.querySelector(selector).innerHTML = empty("호텔을 먼저 선택하세요.");
    return;
  }
  try {
    const rooms = pageItems(await request(`/api/rates/hotels/${hotelId}/rooms?size=200`));
    document.querySelector(selector).innerHTML = rooms.length ? `<div class="table-wrap"><table><thead><tr><th>호실</th><th>객실명</th><th>층/면적</th><th>객실 유형</th><th>정원</th><th>기본 요금</th><th>상태</th><th>작업</th></tr></thead><tbody>${rooms.map((room) => `<tr><td><strong>${escapeHtml(room.number)}</strong></td><td>${escapeHtml(room.name)}${room.description ? `<div class="small muted">${escapeHtml(room.description)}</div>` : ""}</td><td>${escapeHtml(room.floor)}층 / ${escapeHtml(room.size)}㎡</td><td>${escapeHtml(room.roomType)} / ${escapeHtml(room.roomBedOption)} / ${escapeHtml(room.roomViewOption)}</td><td>성인 ${room.maxAdult ?? 0}명 · 아동 ${room.maxChild ?? 0}명</td><td>${money(room.basePrice)}</td><td>${statusBadge(room.roomStatus)}</td><td><div class="form-row"><button class="btn" data-rate-edit="${room.roomId}">상세/수정</button><button class="btn danger" data-rate-delete="${room.roomId}">삭제</button></div><form class="form-row section" data-rate-image="${room.roomId}"><input name="files" type="file" accept="image/*" multiple required><button class="btn" type="submit">이미지 추가</button></form></td></tr>`).join("")}</tbody></table></div>` : empty("등록된 객실이 없습니다.");
    document.querySelectorAll("[data-rate-delete]").forEach((button) => button.addEventListener("click", async () => {
      await request(`/api/rates/rooms/${button.dataset.rateDelete}`, { method: "DELETE" });
      toast("객실이 삭제되었습니다.");
      loadRateRooms(hotelId, selector);
    }));
    document.querySelectorAll("[data-rate-edit]").forEach((button) => button.addEventListener("click", () => editRateRoom(button.dataset.rateEdit, hotelId, selector)));
    document.querySelectorAll("[data-rate-image]").forEach((form) => form.addEventListener("submit", async (event) => {
      event.preventDefault();
      const body = new FormData();
      Array.from(form.elements.files.files).forEach((file) => body.append("files", file));
      await request(`/api/rates/rooms/${form.dataset.rateImage}/images`, { method: "POST", body });
      toast("객실 이미지가 업로드되었습니다.");
      loadRateRooms(hotelId, selector);
    }));
  } catch (error) {
    document.querySelector(selector).innerHTML = errorMessage(error);
  }
}

async function editRateRoom(roomId, hotelId, selector = "#roomsArea") {
  try {
    const room = await request(`/api/rates/rooms/${roomId}`);
    const dialog = ensureRoomEditDialog();
    const amenityLabels = { wifi: "Wi-Fi", tv: "TV", bathtub: "욕조", cityView: "시티뷰", oceanView: "오션뷰", breakfastIncluded: "조식 포함", nonSmoking: "금연" };
    const options = (values, selected) => values.map((value) => `<option ${value === selected ? "selected" : ""}>${value}</option>`).join("");
    dialog.innerHTML = `<form method="dialog" class="backend-dialog-head"><h2>${escapeHtml(room.number)}호 객실 수정</h2><button class="btn" value="cancel">닫기</button></form>
      <form class="grid" id="roomEditForm">
        <div class="grid cols-2">
          <label><span>객실명</span><input name="name" value="${escapeHtml(room.name || "")}" required></label><label><span>객실 번호</span><input name="number" value="${escapeHtml(room.number || "")}" required></label>
          <label><span>층</span><input name="floor" type="number" min="1" value="${escapeHtml(room.floor)}" required></label><label><span>면적(㎡)</span><input name="size" type="number" min="1" value="${escapeHtml(room.size)}" required></label>
          <label><span>기본 요금</span><input name="basePrice" type="number" min="1" value="${escapeHtml(room.basePrice)}" required></label><label><span>운영 여부</span><select name="isActive"><option value="true" ${room.isActive !== false ? "selected" : ""}>운영</option><option value="false" ${room.isActive === false ? "selected" : ""}>중지</option></select></label>
          <label><span>성인 최대</span><input name="maxAdult" type="number" min="1" value="${escapeHtml(room.maxAdult)}" required></label><label><span>아동 최대</span><input name="maxChild" type="number" min="0" value="${escapeHtml(room.maxChild)}" required></label>
          <label><span>유형</span><select name="roomType">${options(["Standard", "Suite", "Deluxe", "Premium"], room.roomType)}</select></label><label><span>상태</span><select name="roomStatus">${options(["EnableReservation", "DisableReservation", "Construct"], room.roomStatus)}</select></label>
          <label><span>전망</span><select name="roomViewOption">${options(["CityView", "RiverView", "MountainView", "OceanView"], room.roomViewOption)}</select></label><label><span>침대</span><select name="roomBedOption">${options(["Floor", "DoubleBed", "QueenBed"], room.roomBedOption)}</select></label>
        </div>
        <label><span>객실 설명</span><textarea name="description">${escapeHtml(room.description || "")}</textarea></label>
        <fieldset><legend>객실 편의시설</legend><div class="tag-picker">${Object.entries(amenityLabels).map(([key, label]) => `<label class="tag-option"><input type="checkbox" name="amenity_${key}" ${room.amenities?.[key] ? "checked" : ""}><span>${label}</span></label>`).join("")}</div></fieldset>
        <section><h3>등록 이미지</h3><div class="hotel-image-grid">${(room.images || []).map((image) => `<article class="hotel-image-item"><img class="hotel-image-thumb" src="${escapeHtml(image.imageUrl || `/api/roomimage/image/${image.roomImageId}`)}" alt="객실 이미지"><button class="btn danger" type="button" data-delete-room-image="${image.roomImageId}">삭제</button></article>`).join("") || empty("등록된 객실 이미지가 없습니다.")}</div></section>
        <div class="form-row"><button class="btn primary" type="submit">변경사항 저장</button><span id="roomEditStatus"></span></div>
      </form>`;
    dialog.querySelector("#roomEditForm").addEventListener("submit", async (event) => {
      event.preventDefault();
      const form = event.currentTarget;
      const data = qs(form);
      ["floor", "size", "basePrice", "maxAdult", "maxChild"].forEach((key) => data[key] = Number(data[key]));
      data.isActive = data.isActive === "true";
      data.amenities = Object.fromEntries(Object.keys(amenityLabels).map((key) => [key, form.elements[`amenity_${key}`].checked]));
      Object.keys(amenityLabels).forEach((key) => delete data[`amenity_${key}`]);
      try {
        await request(`/api/rates/rooms/${roomId}`, { method: "PUT", body: JSON.stringify(data) });
        toast("객실이 수정되었습니다.");
        dialog.close();
        loadRateRooms(hotelId, selector);
      } catch (error) {
        dialog.querySelector("#roomEditStatus").textContent = error.message;
      }
    });
    dialog.querySelectorAll("[data-delete-room-image]").forEach((button) => button.addEventListener("click", async () => {
      await request(`/api/rates/rooms/${roomId}/images/${button.dataset.deleteRoomImage}`, { method: "DELETE" });
      toast("객실 이미지가 삭제되었습니다.");
      editRateRoom(roomId, hotelId, selector);
    }));
    dialog.showModal();
  } catch (error) {
    document.querySelector(selector).innerHTML = errorMessage(error);
  }
}

function ensureRoomEditDialog() {
  let dialog = document.querySelector("#roomEditDialog");
  if (!dialog) {
    dialog = document.createElement("dialog");
    dialog.id = "roomEditDialog";
    dialog.className = "backend-dialog";
    document.body.appendChild(dialog);
  }
  return dialog;
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
  await adminShell("room-add", `${title("객실 추가", "객실 정보, 편의시설, 이미지를 백엔드의 통합 객실 생성 API에 저장합니다.")}<form class="card card-body grid" id="rateRoomForm"><div class="grid cols-2"><label><span>호텔</span><select id="rateRoomHotel"></select></label><label><span>객실 번호</span><input name="number" required></label><label><span>층</span><input name="floor" type="number" min="1" required></label><label><span>객실명</span><input name="name" required></label><label><span>면적(㎡)</span><input name="size" type="number" min="1" required></label><label><span>기본 요금</span><input name="basePrice" type="number" min="1" required></label><label><span>성인 최대</span><input name="maxAdult" type="number" min="1" value="2" required></label><label><span>아동 최대</span><input name="maxChild" type="number" min="0" value="0" required></label><label><span>유형</span><select name="roomType"><option>Standard</option><option>Suite</option><option>Deluxe</option><option>Premium</option></select></label><label><span>상태</span><select name="roomStatus"><option>EnableReservation</option><option>DisableReservation</option><option>Construct</option></select></label><label><span>전망</span><select name="roomViewOption"><option>CityView</option><option>RiverView</option><option>MountainView</option><option>OceanView</option></select></label><label><span>침대</span><select name="roomBedOption"><option>Floor</option><option>DoubleBed</option><option>QueenBed</option></select></label></div><label><span>객실 설명</span><textarea name="description"></textarea></label><fieldset><legend>객실 편의시설</legend><div class="tag-picker">${[["wifi","Wi-Fi"],["tv","TV"],["bathtub","욕조"],["cityView","시티뷰"],["oceanView","오션뷰"],["breakfastIncluded","조식 포함"],["nonSmoking","금연"]].map(([key,label]) => `<label class="tag-option"><input name="amenity_${key}" type="checkbox"><span>${label}</span></label>`).join("")}</div></fieldset><label><span>객실 이미지</span><input name="files" type="file" accept="image/*" multiple></label><button class="btn primary">객실 저장</button></form><div class="section" id="rateRoomResult"></div>`);
  const hotels = await safeLoadHotels();
  const selected = getHotelScope() || hotels[0]?.hotelId || "";
  document.querySelector("#rateRoomHotel").innerHTML = hotels.map((hotel) => `<option value="${hotel.hotelId}" ${String(hotel.hotelId) === String(selected) ? "selected" : ""}>${escapeHtml(hotel.name)}</option>`).join("");
  document.querySelector("#rateRoomForm").addEventListener("submit", async (event) => {
    event.preventDefault();
    const form = event.currentTarget;
    const data = qs(form);
    ["floor", "size", "basePrice", "maxAdult", "maxChild"].forEach((key) => data[key] = Number(data[key] || 0));
    const amenityKeys = ["wifi", "tv", "bathtub", "cityView", "oceanView", "breakfastIncluded", "nonSmoking"];
    data.amenities = Object.fromEntries(amenityKeys.map((key) => [key, form.elements[`amenity_${key}`].checked]));
    amenityKeys.forEach((key) => delete data[`amenity_${key}`]);
    delete data.files;
    try {
      const hotelId = document.querySelector("#rateRoomHotel").value;
      const body = new FormData();
      body.append("room", JSON.stringify(data));
      Array.from(form.elements.files.files).forEach((file) => body.append("files", file));
      const room = await request(`/api/rates/hotels/${hotelId}/rooms`, { method: "POST", body });
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

const hotelAmenityLabels = {
  wifi: "Wi-Fi",
  pool: "수영장",
  fitnessCenter: "피트니스",
  spa: "스파",
  restaurant: "레스토랑",
  valetParking: "발렛",
  freeParking: "무료 주차",
  concierge: "컨시어지",
  bar: "바",
  breakfast: "조식",
  airportShuttle: "공항 셔틀",
  roomService: "룸서비스",
  laundry: "세탁",
  lounge: "라운지",
  sauna: "사우나",
  freeCancel: "무료 취소",
  petFriendly: "반려동물"
};
const hotelAmenityKeys = Object.keys(hotelAmenityLabels);

async function toggleHotelAmenity(hotelId, amenityKey, enabled, managerUserId, button) {
  const userId = requireHotelManagerUserId(managerUserId);
  if (!userId) return;
  if (!hotelAmenityKeys.includes(amenityKey)) return;
  if (button) button.disabled = true;
  try {
    const existing = pageItems(await request(`/api/hotelamenities/hotel/${hotelId}?size=1`))[0];
    const body = { ...(existing || {}), hotelId: Number(hotelId), [amenityKey]: enabled };
    if (!existing) {
      hotelAmenityKeys.forEach((key) => {
        if (key !== amenityKey) body[key] = false;
      });
    }
    await request(`/api/hotelamenities?userId=${encodeURIComponent(userId)}`, {
      method: existing?.amenId ? "PATCH" : "POST",
      body: JSON.stringify(body)
    });
    toast(`${hotelAmenityLabels[amenityKey]} 태그가 ${enabled ? "추가" : "삭제"}되었습니다.`);
    await loadAdminHotelsV2();
  } catch (error) {
    toast(error.message);
    if (button) button.disabled = false;
  }
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

function hotelTimeValue(value) {
  return value ? String(value).slice(0, 5) : "";
}

function hotelEditForm(hotel) {
  const managerUserId = hotel.userId || hotel.user?.userId || "";
  const typeOptions = [
    ["HOTEL", "호텔"],
    ["RESORT", "리조트"],
    ["PENSION_GUESTHOUSE", "펜션/게스트하우스"]
  ].map(([value, label]) => `<option value="${value}" ${hotel.type === value ? "selected" : ""}>${label}</option>`).join("");
  return `<form class="hotel-edit-form" data-hotel-edit-form="${hotel.hotelId}" hidden>
    <input name="hotelId" type="hidden" value="${hotel.hotelId}">
    <div class="grid cols-2">
      <label><span>호텔명</span><input name="name" value="${escapeHtml(hotel.name || "")}" required></label>
      <label><span>도시</span><input name="city" value="${escapeHtml(hotel.city || "")}"></label>
      <label class="hotel-edit-wide"><span>설명</span><textarea name="description">${escapeHtml(hotel.description || "")}</textarea></label>
      <label class="hotel-edit-wide"><span>주소</span><input name="address" value="${escapeHtml(hotel.address || "")}"></label>
      <label><span>우편번호</span><input name="zipCode" value="${escapeHtml(hotel.zipCode || "")}"></label>
      <label><span>전화</span><input name="phone" value="${escapeHtml(hotel.phone || "")}"></label>
      <label><span>이메일</span><input name="email" type="email" value="${escapeHtml(hotel.email || "")}"></label>
      <label><span>등급</span><input name="starRate" type="number" min="1" max="5" value="${escapeHtml(hotel.starRate || "")}"></label>
      <label><span>위도</span><input name="latitude" type="number" step="0.000001" value="${escapeHtml(hotel.latitude ?? "")}" required></label>
      <label><span>경도</span><input name="longitude" type="number" step="0.000001" value="${escapeHtml(hotel.longitude ?? "")}" required></label>
      <label><span>체크인</span><input name="checkIn" type="time" value="${escapeHtml(hotelTimeValue(hotel.checkIn))}"></label>
      <label><span>체크아웃</span><input name="checkOut" type="time" value="${escapeHtml(hotelTimeValue(hotel.checkOut))}"></label>
      <label><span>유형</span><select name="type">${typeOptions}</select></label>
      <label><span>관리자 사용자 ID</span><input name="userId" type="number" value="${escapeHtml(managerUserId)}" required></label>
      <label class="hotel-active-option"><input name="isActive" type="checkbox" ${hotel.isActive !== false ? "checked" : ""}><span>운영 중</span></label>
    </div>
    <div class="form-row hotel-edit-actions"><button class="btn primary" type="submit">변경사항 저장</button><button class="btn" type="button" data-cancel-hotel-edit="${hotel.hotelId}">취소</button><span class="small muted" data-hotel-edit-status></span></div>
  </form>`;
}

function toggleHotelEditForm(hotelId, open) {
  const form = document.querySelector(`[data-hotel-edit-form="${hotelId}"]`);
  const button = document.querySelector(`[data-edit-hotel="${hotelId}"]`);
  if (!form || !button) return;
  const shouldOpen = open ?? form.hidden;
  form.hidden = !shouldOpen;
  button.setAttribute("aria-expanded", String(shouldOpen));
  button.textContent = shouldOpen ? "수정 닫기" : "수정";
  if (shouldOpen) form.elements.name.focus();
}

async function saveHotelEdit(form) {
  const hotelId = form.dataset.hotelEditForm;
  const status = form.querySelector("[data-hotel-edit-status]");
  const submitButton = form.querySelector('button[type="submit"]');
  submitButton.disabled = true;
  status.textContent = "저장 중...";
  try {
    const current = await request(`/api/hotels/${hotelId}`);
    const values = qs(form);
    const body = {
      ...current,
      ...values,
      hotelId: Number(hotelId),
      starRate: Number(values.starRate || 0),
      latitude: Number(values.latitude),
      longitude: Number(values.longitude),
      userId: Number(values.userId),
      isActive: form.elements.isActive.checked
    };
    await request("/api/hotels", { method: "PATCH", body: JSON.stringify(body) });
    toast("호텔 정보가 수정되었습니다.");
    await loadAdminHotelsV2();
  } catch (error) {
    status.textContent = error.message;
    submitButton.disabled = false;
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
    document.querySelector("#hotelList").innerHTML = details.length ? `<div class="grid">${details.map(({ hotel, amenities, transports, images }) => {
      const managerUserId = resolveHotelManagerUserId(hotel.userId || hotel.user?.userId);
      const tags = Object.entries(hotelAmenityLabels).map(([key, label]) => {
        const enabled = amenities?.[key] === true;
        return `<button class="amenity-tag${enabled ? " active" : ""}" type="button" data-toggle-amenity="${key}" data-hotel-id="${hotel.hotelId}" data-enabled="${enabled}" data-manager-id="${escapeHtml(managerUserId)}" aria-pressed="${enabled}" title="${label} 태그 ${enabled ? "삭제" : "추가"}">${label}</button>`;
      }).join("");
      const transportRows = transports.length
        ? transports.map((transport) => `<div class="hotel-resource-row"><div><strong>${escapeHtml(transport.name || "-")}</strong><div class="small muted">${escapeHtml(transport.time || "-")} · ${escapeHtml(transport.depart || "-")}</div></div><div class="form-row"><button class="btn" data-edit-transport="${transport.transId}" data-manager-id="${escapeHtml(managerUserId)}">수정</button><button class="btn danger" data-delete-transport="${transport.transId}" data-manager-id="${escapeHtml(managerUserId)}">삭제</button></div></div>`).join("")
        : `<span class="small muted">등록된 교통 정보가 없습니다.</span>`;
      const imageItems = images.length
        ? images.map((image) => `<div class="hotel-image-item"><img class="hotel-image-thumb" src="/api/hotelimage/image/${image.hotelImageId}" alt="${escapeHtml(image.fileName || hotel.name)}"><div class="small muted">${escapeHtml(image.fileName || `이미지 ${image.hotelImageId}`)}</div><form class="form-row" data-replace-hotel-image="${image.hotelImageId}" data-manager-id="${escapeHtml(managerUserId)}"><input type="file" name="file" accept="image/*" required><button class="btn" type="submit">교체</button><button class="btn danger" type="button" data-delete-hotel-image="${image.hotelImageId}" data-manager-id="${escapeHtml(managerUserId)}">삭제</button></form></div>`).join("")
        : `<span class="small muted">등록된 호텔 이미지가 없습니다.</span>`;
      return `<article class="card"><div class="card-body">
        <div class="toolbar" style="margin:0"><h3>${escapeHtml(hotel.name)}</h3><div class="form-row"><button class="btn" type="button" data-edit-hotel="${hotel.hotelId}" aria-expanded="false">수정</button><button class="btn danger" data-delete-hotel="${hotel.hotelId}" data-manager-id="${escapeHtml(managerUserId)}">삭제</button></div></div>
        <p class="muted">${escapeHtml(hotel.address || "")}</p>
        ${hotelEditForm(hotel)}
        <div class="hotel-resource"><div class="toolbar"><h4>편의시설 태그</h4></div><div class="amenity-tag-list">${tags}</div></div>
        <div class="hotel-resource"><div class="toolbar"><h4>교통 정보</h4><button class="btn" data-trans-hotel="${hotel.hotelId}" data-manager-id="${escapeHtml(managerUserId)}">추가</button></div><div class="hotel-resource-list">${transportRows}</div></div>
        <div class="hotel-resource"><div class="toolbar"><h4>호텔 이미지</h4><a class="btn" href="rooms.html?hotelId=${hotel.hotelId}">객실 관리</a></div><div class="hotel-image-grid">${imageItems}</div><form class="form-row section" data-hotel-image-form="${hotel.hotelId}" data-manager-id="${escapeHtml(managerUserId)}"><input type="file" name="file" accept="image/*" required><button class="btn" type="submit">새 이미지 추가</button></form></div>
      </div></article>`;
    }).join("")}</div>` : empty(scope ? "선택한 호텔을 찾을 수 없습니다." : "등록된 호텔이 없습니다.");
    document.querySelectorAll("[data-edit-hotel]").forEach((button) => button.addEventListener("click", () => toggleHotelEditForm(button.dataset.editHotel)));
    document.querySelectorAll("[data-cancel-hotel-edit]").forEach((button) => button.addEventListener("click", () => toggleHotelEditForm(button.dataset.cancelHotelEdit, false)));
    document.querySelectorAll("[data-hotel-edit-form]").forEach((form) => form.addEventListener("submit", (event) => {
      event.preventDefault();
      saveHotelEdit(form);
    }));
    document.querySelectorAll("[data-delete-hotel]").forEach((button) => button.addEventListener("click", async () => {
      const userId = requireHotelManagerUserId(button.dataset.managerId);
      if (!userId) { toast("호텔 삭제에는 관리자 사용자 ID가 필요합니다."); return; }
      await request(`/api/hotels/${button.dataset.deleteHotel}?userId=${encodeURIComponent(userId)}`, { method: "DELETE" });
      toast("호텔이 삭제되었습니다.");
      loadAdminHotelsV2();
    }));
    document.querySelectorAll("[data-toggle-amenity]").forEach((button) => button.addEventListener("click", () => toggleHotelAmenity(button.dataset.hotelId, button.dataset.toggleAmenity, button.dataset.enabled !== "true", button.dataset.managerId, button)));
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


export {
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
};
