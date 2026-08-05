import { request, pageItems, qs, escapeHtml } from "./api.js";
import {
  userShell,
  title,
  empty,
  errorMessage,
  safeLoadHotels,
  loadHotelCovers,
  hotelCard,
  hotelImageUrl,
  roomRow,
  statusBadge,
  todayDate
} from "./core.js";

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
    const hotels = await loadHotelCovers(pageItems(await request("/api/hotels?size=6")));
    document.querySelector("#hotels").innerHTML = `<div class="toolbar"><h2>등록 호텔</h2><a class="btn" href="search.html">전체 보기</a></div>${hotels.length ? `<div class="grid cols-3">${hotels.map(hotelCard).join("")}</div>` : empty("DB에 등록된 호텔이 없습니다.", "관리자 호텔 관리에서 호텔을 추가하세요.")}`;
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
      const filteredHotels = pageItems(await request("/api/hotels?size=100")).filter((hotel) => !q || `${hotel.name} ${hotel.city} ${hotel.address}`.toLowerCase().includes(q));
      const hotels = await loadHotelCovers(filteredHotels);
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
      request(`/api/rates/hotels/${id}/rooms?size=100`),
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
      ${images.length ? `<section class="section grid cols-3">${images.map((image) => `<article class="card"><img class="cover-img" src="${hotelImageUrl(image)}" alt="${escapeHtml(image.fileName || hotel.name)}"></article>`).join("")}</section>` : ""}
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


export { homePage, searchPage, detailPage, reviewCard };
