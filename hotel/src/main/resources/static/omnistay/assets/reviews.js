import { request, pageItems, qs, escapeHtml } from "./api.js";
import { getCurrentUser, userShell, adminShell, title, empty, errorMessage, toast, paymentBookingId } from "./core.js";
import { reviewCard } from "./hotels.js";

async function loadReviews(adminMode = false, keyword = "") {
  try {
    const query = new URLSearchParams({ size: "100" });
    if (keyword.trim()) query.set("keyword", keyword.trim());
    const currentUser = getCurrentUser();
    const reviews = pageItems(await request(`/api/review?${query.toString()}`));
    const visibleReviews = adminMode ? reviews : reviews.filter((review) => Number(review.userId) === Number(currentUser?.userId));
    document.querySelector("#reviewList").innerHTML = visibleReviews.length ? `<div class="grid">${visibleReviews.map((review) => reviewCard(review, `<div class="form-row"><button class="btn" data-edit-review="${review.reviewId}">상세/수정</button>${adminMode ? `<button class="btn danger" data-delete-review="${review.reviewId}">삭제</button>` : ""}</div>`)).join("")}</div>` : empty(keyword ? "검색된 리뷰가 없습니다." : "등록된 리뷰가 없습니다.");
    document.querySelectorAll("[data-edit-review]").forEach((btn) => btn.addEventListener("click", () => openReviewEditor(btn.dataset.editReview, adminMode, keyword)));
    document.querySelectorAll("[data-delete-review]").forEach((btn) => btn.addEventListener("click", async () => {
      await request(`/api/review/${btn.dataset.deleteReview}`, { method: "DELETE" });
      toast("삭제되었습니다.");
      loadReviews(adminMode, keyword);
    }));
  } catch (error) {
    document.querySelector("#reviewList").innerHTML = errorMessage(error);
  }
}

async function openReviewEditor(reviewId, adminMode, keyword = "") {
  try {
    const review = await request(`/api/review/${reviewId}`);
    const dialog = ensureReviewDialog();
    const tagValues = new Set((review.tags || []).map((item) => item.tag));
    const ratingByCategory = new Map((review.ratings || []).map((item) => [item.category, Number(item.score)]));
    dialog.innerHTML = `<form method="dialog" class="backend-dialog-head"><h2>리뷰 상세/수정</h2><button class="btn" value="cancel">닫기</button></form>
      <form class="grid" id="reviewEditForm">
        <div class="message">${escapeHtml(review.hotelName || "호텔")} · ${escapeHtml(review.roomName || "객실")}<div class="small">예약 ID ${escapeHtml(review.reservationId)}</div></div>
        <div class="grid cols-2"><label><span>여행 유형</span><select name="tripType">${["FAMILY","COUPLE","FRIENDS","BUSINESS","SOLO","OTHER"].map((value) => `<option ${review.tripType === value ? "selected" : ""}>${value}</option>`).join("")}</select></label><label><span>종합 평점</span><select name="overallRating">${ratingOptions(Number(review.overallRating || 5))}</select></label></div>
        <label><span>제목</span><input name="title" value="${escapeHtml(review.title || "")}" maxlength="200" required></label>
        <fieldset class="review-fieldset"><legend>리뷰 태그</legend><div class="tag-picker">${reviewTagOptions.map(([value, label]) => `<label class="tag-option"><input type="checkbox" name="reviewTag" value="${value}" ${tagValues.has(value) ? "checked" : ""}><span>${label}</span></label>`).join("")}</div></fieldset>
        <fieldset class="review-fieldset"><legend>항목별 점수</legend><div class="rating-grid">${reviewRatingOptions.map(([category, label]) => `<label><span>${label}</span><select name="rating_${category}">${ratingOptions(ratingByCategory.get(category) || 5)}</select></label>`).join("")}</div></fieldset>
        <label><span>내용</span><textarea name="content" required>${escapeHtml(review.content || "")}</textarea></label>
        <div class="form-row"><button class="btn primary" type="submit">리뷰 저장</button><span id="reviewEditStatus"></span></div>
      </form>`;
    dialog.querySelector("#reviewEditForm").addEventListener("submit", async (event) => {
      event.preventDefault();
      const form = event.currentTarget;
      const data = qs(form);
      const updated = {
        ...review,
        tripType: data.tripType,
        overallRating: Number(data.overallRating),
        title: data.title,
        content: data.content,
        tags: Array.from(form.querySelectorAll('input[name="reviewTag"]:checked')).map((input) => ({ tag: input.value })),
        ratings: reviewRatingOptions.map(([category]) => ({ category, score: Number(data[`rating_${category}`]) }))
      };
      try {
        await request("/api/review", { method: "PATCH", body: JSON.stringify(updated) });
        toast("리뷰가 수정되었습니다.");
        dialog.close();
        loadReviews(adminMode, keyword);
      } catch (error) {
        dialog.querySelector("#reviewEditStatus").textContent = error.message;
      }
    });
    dialog.showModal();
  } catch (error) {
    document.querySelector("#reviewList").innerHTML = errorMessage(error);
  }
}

function ensureReviewDialog() {
  let dialog = document.querySelector("#reviewEditDialog");
  if (!dialog) {
    dialog = document.createElement("dialog");
    dialog.id = "reviewEditDialog";
    dialog.className = "backend-dialog";
    document.body.appendChild(dialog);
  }
  return dialog;
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
    await adminShell("reviews-admin", `${title("리뷰 관리", "백엔드 리뷰 검색, 상세, 수정, 삭제 기능을 관리합니다.")}<div class="filters"><input id="reviewKeyword" placeholder="제목 또는 내용 검색"><button class="btn primary" id="reviewSearch">검색</button></div><section id="reviewList">${empty("리뷰를 불러오는 중입니다.")}</section>`);
    document.querySelector("#reviewSearch").addEventListener("click", () => loadReviews(true, document.querySelector("#reviewKeyword").value));
    loadReviews(true, "");
    return;
  }

  const currentUser = getCurrentUser();
  if (!currentUser) {
    location.href = `login.html?reason=review&redirect=${encodeURIComponent("reviews.html")}`;
    return;
  }

  userShell("reviews", `${title("리뷰 작성", "결제가 완료된 예약만 리뷰를 작성할 수 있습니다.")}<div class="grid cols-2"><form class="card card-body grid" id="reviewForm"><div id="reviewEligibility">${empty("작성 가능한 예약을 확인하고 있습니다.")}</div><label><span>리뷰를 작성할 숙박</span><select name="reviewBooking" id="reviewBooking" disabled required><option value="">불러오는 중</option></select></label><label><span>여행 유형</span><select name="tripType"><option value="FAMILY">가족 여행</option><option value="COUPLE">커플 여행</option><option value="FRIENDS">친구 여행</option><option value="BUSINESS">출장</option><option value="SOLO">나홀로 여행</option><option value="OTHER">기타</option></select></label>${ratingStars("overallRating", "종합 평점")}<label><span>제목</span><input name="title" required maxlength="200"></label><fieldset class="review-fieldset"><legend>어떤 점이 인상적이었나요?</legend><div class="tag-picker">${reviewTagOptions.map(([value, label]) => `<label class="tag-option"><input type="checkbox" name="reviewTag" value="${value}"><span>${label}</span></label>`).join("")}</div></fieldset><fieldset class="review-fieldset"><legend>항목별 점수</legend><div class="rating-grid">${reviewRatingOptions.map(([category, label]) => ratingStars(`rating_${category}`, label)).join("")}</div></fieldset><label><span>사진 첨부</span><input name="photos" type="file" accept="image/png,image/jpeg,image/webp,image/gif" multiple><small class="muted">이미지 파일을 여러 장 선택할 수 있습니다.</small></label><label style="grid-column:1/-1"><span>내용</span><textarea name="content" required></textarea></label><button class="btn primary" id="reviewSubmit" disabled>리뷰 저장</button></form><section id="reviewList">${empty("리뷰를 불러오는 중입니다.")}</section></div>`);

  document.querySelector("#reviewList").insertAdjacentHTML("beforebegin", `<div class="filters"><input id="reviewKeyword" placeholder="내 리뷰 제목 또는 내용 검색"><button class="btn" id="reviewSearch" type="button">검색</button></div>`);
  document.querySelector("#reviewSearch").addEventListener("click", () => loadReviews(false, document.querySelector("#reviewKeyword").value));
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


export { reviewsPage, loadReviews };
