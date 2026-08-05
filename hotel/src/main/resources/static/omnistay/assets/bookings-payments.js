import { request, pageItems, qs, money, escapeHtml } from "./api.js";
import {
  TOSS_SAMPLE_CLIENT_KEY,
  getCurrentUser,
  userShell,
  title,
  empty,
  errorMessage,
  toast,
  safeLoadHotels,
  hotelImageUrl,
  statusBadge,
  screenOnlyBadge,
  todayDate,
  loadTossPaymentsSdk,
  tossMethodToPaymentMethod,
  paymentBookingId
} from "./core.js";

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
    const rooms = selectedHotel ? pageItems(await request(`/api/rates/hotels/${selectedHotel}/rooms?size=100`)) : [];
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
  userShell("bookings", `<section class="toss-page"><div class="toss-wrapper"><form class="toss-box" id="paymentForm"><h1>일반 결제</h1><input name="bookingId" type="hidden" value="${escapeHtml(bookingId)}"><input name="bookingNo" type="hidden" value="${escapeHtml(bookingNo)}"><input name="totalAmount" type="hidden" value="${escapeHtml(totalAmount)}"><input name="orderName" type="hidden" value="${escapeHtml(orderName)}"><input name="customerName" type="hidden" value="${escapeHtml(customerName)}"><input name="customerEmail" type="hidden" value="${escapeHtml(customerEmail)}"><input name="customerMobilePhone" type="hidden" value="${escapeHtml(customerPhone)}"><div class="toss-summary"><div class="toss-row"><span>결제자</span><strong>${escapeHtml(customerName)}</strong></div><div class="toss-row"><span>예약 금액</span><strong>${totalAmount ? money(totalAmount) : "예약 금액 없음"}</strong></div><div class="toss-row"><span>쿠폰 할인</span><strong id="paymentDiscount">${money(0)}</strong></div><div class="toss-row total"><span>최종 결제금액</span><strong id="paymentFinalAmount">${totalAmount ? money(totalAmount) : "-"}</strong></div></div><div class="toss-field"><label for="paymentCouponSelect">쿠폰 선택</label><div class="coupon-apply-row"><select name="couponId" id="paymentCouponSelect"><option value="">사용 안 함</option></select><button class="btn primary" id="applyPaymentCoupon" type="button">쿠폰 적용</button></div><div class="coupon-apply-status" id="couponApplyStatus">주문 조건에 맞는 쿠폰을 선택한 뒤 적용하세요.</div></div><div class="toss-methods" id="payment-method"><button class="toss-method active" type="button" data-payment-method="CARD">카드</button><button class="toss-method" type="button" data-payment-method="TRANSFER">계좌이체</button><button class="toss-method" type="button" data-payment-method="VIRTUAL_ACCOUNT">가상계좌</button><button class="toss-method" type="button" data-payment-method="MOBILE_PHONE">휴대폰</button><button class="toss-method" type="button" data-payment-method="CULTURE_GIFT_CERTIFICATE">문화상품권</button></div><button class="toss-button" id="openTossPayment" type="submit">결제하기</button><div class="toss-note" id="tossPaymentStatus">토스페이먼츠 결제창을 사용합니다. 결제 금액은 예약 정보에서 자동으로 들어갑니다.</div></form></div></section>`);
  if (currentUser?.userId) {
    await loadPaymentCoupons(totalAmount);
  }
  let selectedPaymentMethod = "CARD";
  let appliedCouponId = "";
  let appliedDiscount = 0;
  const couponSelect = document.querySelector("#paymentCouponSelect");
  const couponApplyStatus = document.querySelector("#couponApplyStatus");
  const discountEl = document.querySelector("#paymentDiscount");
  const finalAmountEl = document.querySelector("#paymentFinalAmount");
  const statusEl = document.querySelector("#tossPaymentStatus");
  const calculateSelectedDiscount = () => {
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
    const finalAmount = Math.max(totalAmount - appliedDiscount, 0);
    discountEl.textContent = money(appliedDiscount);
    finalAmountEl.textContent = totalAmount ? money(finalAmount) : "-";
    return { discount: appliedDiscount, finalAmount };
  };
  couponSelect.addEventListener("change", () => {
    appliedCouponId = "";
    appliedDiscount = 0;
    couponApplyStatus.textContent = couponSelect.value ? "쿠폰 적용 버튼을 눌러주세요." : "쿠폰을 사용하지 않습니다.";
    updateAmount();
  });
  document.querySelector("#applyPaymentCoupon").addEventListener("click", () => {
    const option = couponSelect.selectedOptions?.[0];
    if (!option?.value) {
      appliedCouponId = "";
      appliedDiscount = 0;
      couponApplyStatus.textContent = "쿠폰을 사용하지 않습니다.";
      updateAmount();
      return;
    }
    const discount = calculateSelectedDiscount();
    if (discount <= 0) {
      appliedCouponId = "";
      appliedDiscount = 0;
      couponApplyStatus.textContent = `최소 결제금액 ${money(option.dataset.minOrder || 0)}을 충족하지 못했습니다.`;
      updateAmount();
      return;
    }
    appliedCouponId = option.value;
    appliedDiscount = discount;
    couponApplyStatus.textContent = `${option.textContent}이 적용되었습니다.`;
    updateAmount();
  });
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
    if (data.couponId && data.couponId !== appliedCouponId) {
      statusEl.className = "message error toss-note";
      statusEl.textContent = "선택한 쿠폰을 먼저 적용해주세요.";
      return;
    }
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
      const existingPayments = pageItems(await request("/api/payment").catch(() => []));
      const draftStorageKey = `omnistayPaymentDraft:${data.bookingId}`;
      const storedDraftPaymentId = Number(localStorage.getItem(draftStorageKey) || 0) || null;
      const existingPayment = existingPayments.find((item) => String(paymentBookingId(item)) === String(data.bookingId))
        || (storedDraftPaymentId ? { paymentId: storedDraftPaymentId } : null);
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
      let draftPayment;
      try {
        draftPayment = await request("/api/payment", {
          method: existingPayment?.paymentId ? "PATCH" : "POST",
          body: JSON.stringify(draftPayload)
        });
      } catch (error) {
        if (!existingPayment?.paymentId) throw error;
        localStorage.removeItem(draftStorageKey);
        draftPayload.paymentId = undefined;
        draftPayment = await request("/api/payment", { method: "POST", body: JSON.stringify(draftPayload) });
      }
      if (draftPayment.paymentId) {
        localStorage.setItem(draftStorageKey, String(draftPayment.paymentId));
      }
      const query = new URLSearchParams({
        bookingId: data.bookingId,
        paymentId: String(draftPayment.paymentId || ""),
        couponId: String(couponId || ""),
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

async function loadAvailableCoupons(orderAmount) {
  const query = orderAmount ? `?orderAmount=${encodeURIComponent(orderAmount)}` : "";
  return pageItems(await request(`/api/coupons/available${query}`));
}

async function loadPaymentCoupons(totalAmount) {
  try {
    const availableCoupons = await loadAvailableCoupons(totalAmount);
    const options = availableCoupons.map((coupon) => {
      const minOrder = Number(coupon.minOrder || 0);
      const discountLabel = coupon.discountType === "RATE" ? `${coupon.discountValue}% 할인` : `${money(coupon.discountValue)} 할인`;
      return `<option value="${coupon.couponId}" data-coupon-id="${escapeHtml(coupon.couponId)}" data-discount-type="${escapeHtml(coupon.discountType)}" data-discount-value="${escapeHtml(coupon.discountValue)}" data-min-order="${escapeHtml(minOrder)}" data-max-discount="${escapeHtml(coupon.maxDiscount || 0)}">${escapeHtml(coupon.name)} · ${escapeHtml(discountLabel)}</option>`;
    });
    document.querySelector("#paymentCouponSelect").innerHTML = `<option value="">사용 안 함</option>${options.join("")}`;
    document.querySelector("#couponApplyStatus").textContent = availableCoupons.length ? "사용할 쿠폰을 선택한 뒤 적용하세요." : "현재 주문에 사용할 수 있는 쿠폰이 없습니다.";
  } catch (error) {
    document.querySelector("#paymentCouponSelect").innerHTML = `<option value="">쿠폰 불러오기 실패</option>`;
    document.querySelector("#couponApplyStatus").textContent = error.message;
  }
}

async function paymentResultPage(status) {
  const params = new URLSearchParams(location.search);
  const orderId = params.get("orderId") || "-";
  const paymentKey = params.get("paymentKey") || "-";
  const amount = params.get("amount") || "-";
  const bookingId = params.get("bookingId") || "";
  const paymentId = params.get("paymentId") || "";
  const couponId = Number(params.get("couponId") || 0);
  const discountAmount = Number(params.get("discountAmount") || 0);
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
          couponId,
          discountAmount,
          currency: result.currency || "KRW",
          orderId: params.get("orderId"),
          paymentKey: params.get("paymentKey"),
          provider: "TOSS"
        })
      });
      localStorage.removeItem(`omnistayPaymentDraft:${bookingId}`);
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
    document.querySelector(selector).innerHTML = payments.length ? `<div class="table-wrap"><table><thead><tr><th>ID</th><th>예약</th><th>상태</th><th>금액</th><th>수단</th>${admin ? "<th></th>" : ""}</tr></thead><tbody>${payments.map((p) => `<tr><td>${p.paymentId}</td><td>${p.bookingId || p.booking?.bookingId || "-"}</td><td>${escapeHtml(p.paymentStatus)}</td><td>${money(p.totalAmount)}</td><td>${escapeHtml(p.paymentMethod)}</td>${admin ? `<td><div class="actions"><button class="btn" data-edit-payment="${p.paymentId}">상세/수정</button><button class="btn danger" data-delete-payment="${p.paymentId}">삭제</button></div></td>` : ""}</tr>`).join("")}</tbody></table></div>` : empty("등록된 결제가 없습니다.");
    document.querySelectorAll("[data-edit-payment]").forEach((btn) => btn.addEventListener("click", () => openPaymentEditor(btn.dataset.editPayment, selector)));
    document.querySelectorAll("[data-delete-payment]").forEach((btn) => btn.addEventListener("click", async () => {
      await request(`/api/payment/${btn.dataset.deletePayment}`, { method: "DELETE" });
      toast("삭제되었습니다.");
      loadPayments(selector, admin);
    }));
  } catch (error) {
    document.querySelector(selector).innerHTML = errorMessage(error);
  }
}

async function openPaymentEditor(paymentId, selector) {
  try {
    const payment = await request(`/api/payment/${paymentId}`);
    let dialog = document.querySelector("#paymentEditDialog");
    if (!dialog) {
      dialog = document.createElement("dialog");
      dialog.id = "paymentEditDialog";
      dialog.className = "backend-dialog";
      document.body.appendChild(dialog);
    }
    const options = (values, selected) => values.map((value) => `<option ${value === selected ? "selected" : ""}>${value}</option>`).join("");
    dialog.innerHTML = `<form method="dialog" class="backend-dialog-head"><h2>결제 ${escapeHtml(payment.paymentId)} 상세</h2><button class="btn" value="cancel">닫기</button></form>
      <form class="grid" id="paymentEditForm">
        <div class="grid cols-2">
          <label><span>예약 ID</span><input value="${escapeHtml(paymentBookingId(payment))}" readonly></label><label><span>주문 ID</span><input value="${escapeHtml(payment.orderId || "-")}" readonly></label>
          <label><span>상태</span><select name="paymentStatus">${options(["Ready","Paid","Failed","Cancelled","PartialRefunced","Refunded"], payment.paymentStatus)}</select></label><label><span>결제 수단</span><select name="paymentMethod">${options(["Cache","Online","CreditCard","CheckCard","BankTransfer"], payment.paymentMethod)}</select></label>
          <label><span>결제 금액</span><input name="totalAmount" type="number" min="0" value="${escapeHtml(payment.totalAmount || 0)}"></label><label><span>할인 금액</span><input name="discountAmount" type="number" min="0" value="${escapeHtml(payment.discountAmount || 0)}"></label>
          <label><span>거래번호</span><input name="transactionNum" value="${escapeHtml(payment.transactionNum || "")}"></label><label><span>PG 제공자</span><input name="provider" value="${escapeHtml(payment.provider || "")}"></label>
          <label><span>카드사</span><input name="cardCompany" value="${escapeHtml(payment.cardCompany || "")}"></label><label><span>통화</span><input name="currency" value="${escapeHtml(payment.currency || "KRW")}"></label>
        </div>
        ${payment.receiptUrl ? `<a class="btn" href="${escapeHtml(payment.receiptUrl)}" target="_blank" rel="noreferrer">영수증 열기</a>` : ""}
        ${payment.failMessage ? `<div class="message error">${escapeHtml(payment.failCode || "")}: ${escapeHtml(payment.failMessage)}</div>` : ""}
        <div class="form-row"><button class="btn primary" type="submit">결제 정보 저장</button><span id="paymentEditStatus"></span></div>
      </form>`;
    dialog.querySelector("#paymentEditForm").addEventListener("submit", async (event) => {
      event.preventDefault();
      const values = qs(event.currentTarget);
      try {
        await request("/api/payment", {
          method: "PATCH",
          body: JSON.stringify({
            ...payment,
            ...values,
            paymentId: Number(payment.paymentId),
            totalAmount: Number(values.totalAmount || 0),
            discountAmount: Number(values.discountAmount || 0),
            ...(values.paymentStatus === "Cancelled" && !payment.cancelledAt ? { cancelledAt: new Date().toISOString().slice(0, 19) } : {})
          })
        });
        toast("결제 정보가 수정되었습니다.");
        dialog.close();
        loadPayments(selector, true);
      } catch (error) {
        dialog.querySelector("#paymentEditStatus").textContent = error.message;
      }
    });
    dialog.showModal();
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
  userShell("coupons", `${title("쿠폰함", "생성된 쿠폰 중 현재 사용할 수 있는 쿠폰을 조회합니다.")}<section class="card card-body"><div class="toolbar" style="margin:0 0 10px"><h2>사용 가능한 쿠폰</h2><span class="status ok">API 연결</span></div><div id="myCoupons">${empty("쿠폰을 불러오는 중입니다.")}</div></section>`);
  await loadCoupons();
}

async function loadCoupons() {
  try {
    const mine = await loadAvailableCoupons();
    document.querySelector("#myCoupons").innerHTML = mine.length ? `<div class="table-wrap"><table><thead><tr><th>쿠폰</th><th>할인</th><th>최소 결제금액</th><th>만료일</th></tr></thead><tbody>${mine.map((coupon) => {
      const discount = coupon.discountType === "RATE" ? `${coupon.discountValue}%` : money(coupon.discountValue);
      return `<tr><td><strong>${escapeHtml(coupon.name)}</strong><div class="small muted">${escapeHtml(coupon.description || coupon.code || "")}</div></td><td>${escapeHtml(discount)}</td><td>${money(coupon.minOrder)}</td><td>${escapeHtml(coupon.expirationDate || "-")}</td></tr>`;
    }).join("")}</tbody></table></div>` : empty("사용 가능한 쿠폰이 없습니다.");
  } catch (error) {
    document.querySelector("#myCoupons").innerHTML = errorMessage(error);
  }
}


async function loadBookings(selector, userId, adminMode) {
  if (!userId) {
    document.querySelector(selector).innerHTML = empty("사용자 ID를 입력하세요.");
    return;
  }
  try {
    const [bookingResponses, payments] = await Promise.all([
      request(`/api/bookings/${userId}`).then(pageItems),
      request("/api/payment").then(pageItems).catch(() => [])
    ]);
    const paymentsByBookingId = new Map();
    payments.forEach((payment) => {
      const bookingId = Number(paymentBookingId(payment));
      const current = paymentsByBookingId.get(bookingId);
      if (!current || Number(payment.paymentId || 0) > Number(current.paymentId || 0)) {
        paymentsByBookingId.set(bookingId, payment);
      }
    });
    const rows = bookingResponses.map((response) => {
      const booking = response.booking || response;
      const room = booking.room || {};
      const hotel = room.hotel || {};
      const coverUrl = hotelImageUrl(response.hotelImages?.[0]);
      const cancelled = Boolean(booking.cancelledAt);
      const payment = paymentsByBookingId.get(Number(booking.bookingId));
      const paymentStatus = String(payment?.paymentStatus ?? "").toLowerCase();
      const paid = paymentStatus === "paid" || paymentStatus === "1";
      const statusLabel = cancelled ? "취소된 예약" : paid ? "결제 완료" : paymentStatus === "failed" || paymentStatus === "2" ? "결제 실패" : payment ? "결제 대기" : "예약 완료";
      return `<tr>
        <td><strong>${escapeHtml(booking.bookingNo || booking.bookingId || "-")}</strong><div class="small muted">${statusLabel}</div></td>
        <td><div class="booking-place">${coverUrl ? `<img class="booking-thumb" src="${coverUrl}" alt="${escapeHtml(hotel.name || "호텔 이미지")}">` : ""}<div><strong>${escapeHtml(hotel.name || "호텔 정보 없음")}</strong><div class="small muted">${escapeHtml(room.number ? `${room.number}호 ${room.name || ""}`.trim() : room.name || `객실 ${booking.roomId || "-"}`)}</div></div></div></td>
        <td>${escapeHtml(booking.guestName || booking.user?.name || "-")}<div class="small muted">${escapeHtml(booking.guestEmail || "")}</div></td>
        <td>${escapeHtml(booking.checkinDate || "-")} ~ ${escapeHtml(booking.checkoutDate || "-")}<div class="small muted">${booking.nights ?? "-"}박</div></td>
        <td>성인 ${booking.adultCount ?? 0}, 아동 ${booking.childCount ?? 0}</td>
        ${adminMode ? `<td>${cancelled ? `<span class="status warn">취소됨</span>` : `<button class="btn danger" data-cancel-booking="${booking.bookingId}">취소</button>`}</td>` : `<td>${paid && !cancelled ? `<a class="btn" href="reviews.html?bookingId=${booking.bookingId}">리뷰 작성</a>` : "-"}</td>`}
      </tr>`;
    });
    document.querySelector(selector).innerHTML = bookingResponses.length ? `<div class="table-wrap"><table><thead><tr><th>예약번호</th><th>호텔 / 객실</th><th>투숙객</th><th>일정</th><th>인원</th><th>${adminMode ? "관리" : "리뷰"}</th></tr></thead><tbody>${rows.join("")}</tbody></table></div>` : empty("예약 내역이 없습니다.");
    document.querySelectorAll("[data-cancel-booking]").forEach((btn) => btn.addEventListener("click", async () => {
      await request(`/api/bookings/cancel/${btn.dataset.cancelBooking}`, { method: "PATCH" });
      toast("예약이 취소되었습니다.");
      loadBookings(selector, userId, adminMode);
    }));
  } catch (error) {
    document.querySelector(selector).innerHTML = errorMessage(error);
  }
}


export {
  bookingPage,
  paymentPage,
  paymentResultPage,
  bookingsPage,
  couponsPage,
  loadBookings,
  loadPayments
};
