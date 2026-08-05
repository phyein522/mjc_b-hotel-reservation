import { request, qs, escapeHtml } from "./api.js";
import { loadReviews } from "./reviews.js";
import {
  getCurrentUser,
  setCurrentUser,
  clearCurrentUser,
  userShell,
  title,
  empty,
  errorMessage,
  toast
} from "./core.js";

function profileForm(user) {
  return `<form class="card card-body grid" id="profileForm">
    <div class="grid cols-2">
      <label><span>이메일</span><input name="email" type="email" value="${escapeHtml(user.email || "")}" required></label>
      <label><span>이름</span><input name="name" value="${escapeHtml(user.name || "")}" required></label>
      <label><span>전화번호</span><input name="phone" type="tel" value="${escapeHtml(user.phone || "")}"></label>
      <label><span>회원 등급</span><input value="${escapeHtml(user.membership || "-")}" readonly></label>
      <label><span>계정 상태</span><input value="${escapeHtml(user.status || "-")}" readonly></label>
      <label><span>포인트</span><input value="${escapeHtml(user.point ?? 0)}" readonly></label>
    </div>
    <label class="tag-option"><input name="marketingAgreed" type="checkbox" ${user.marketingAgreed ? "checked" : ""}><span>마케팅 정보 수신 동의</span></label>
    <div class="form-row"><button class="btn primary" type="submit">정보 저장</button><button class="btn danger" id="deleteAccount" type="button">회원 탈퇴</button></div>
    <div id="profileResult"></div>
  </form>`;
}

function profileTabs() {
  return `<div class="profile-tabs" role="tablist" aria-label="내 정보 메뉴">
    <button class="profile-tab active" type="button" data-profile-tab="account" role="tab" aria-selected="true">회원 정보</button>
    <button class="profile-tab" type="button" data-profile-tab="reviews" role="tab" aria-selected="false">리뷰 수정</button>
  </div>`;
}

async function profilePage() {
  const sessionUser = getCurrentUser();
  if (!sessionUser?.userId) {
    location.href = `login.html?redirect=${encodeURIComponent("profile.html")}`;
    return;
  }
  userShell("profile", `${title("내 정보", "회원 정보와 내가 작성한 리뷰를 관리합니다.")}${profileTabs()}<section id="profileAccountTab"><div id="profileArea">${empty("회원 정보를 불러오는 중입니다.")}</div></section><section id="profileReviewTab" hidden><div class="toolbar"><h2>리뷰 수정</h2><span class="muted">내가 작성한 리뷰를 수정하거나 삭제할 수 있습니다.</span></div><div class="filters"><input id="profileReviewKeyword" placeholder="리뷰 제목 또는 내용 검색"><button class="btn" id="profileReviewSearch" type="button">검색</button></div><section id="reviewList">${empty("리뷰를 불러오는 중입니다.")}</section></section>`);
  const area = document.querySelector("#profileArea");
  const accountTab = document.querySelector("#profileAccountTab");
  const reviewTab = document.querySelector("#profileReviewTab");
  let reviewsLoaded = false;
  document.querySelectorAll("[data-profile-tab]").forEach((tab) => tab.addEventListener("click", async () => {
    const reviewsActive = tab.dataset.profileTab === "reviews";
    accountTab.hidden = reviewsActive;
    reviewTab.hidden = !reviewsActive;
    document.querySelectorAll("[data-profile-tab]").forEach((item) => {
      const active = item === tab;
      item.classList.toggle("active", active);
      item.setAttribute("aria-selected", String(active));
    });
    if (reviewsActive && !reviewsLoaded) {
      reviewsLoaded = true;
      await loadReviews(false, "");
    }
  }));
  document.querySelector("#profileReviewSearch").addEventListener("click", () => {
    loadReviews(false, document.querySelector("#profileReviewKeyword").value);
  });
  try {
    const user = await request(`/api/users/${sessionUser.userId}`);
    area.innerHTML = profileForm(user);
    const form = document.querySelector("#profileForm");
    form.addEventListener("submit", async (event) => {
      event.preventDefault();
      const values = qs(form);
      try {
        const updated = await request("/api/users/update", {
          method: "PATCH",
          body: JSON.stringify({
            ...user,
            ...values,
            userId: Number(user.userId),
            marketingAgreed: form.elements.marketingAgreed.checked
          })
        });
        const safeUser = { ...updated };
        delete safeUser.password;
        setCurrentUser(safeUser);
        toast("회원 정보가 수정되었습니다.");
        await profilePage();
      } catch (error) {
        document.querySelector("#profileResult").innerHTML = errorMessage(error);
      }
    });
    document.querySelector("#deleteAccount").addEventListener("click", async () => {
      if (!confirm("회원 계정을 삭제하시겠습니까?")) return;
      try {
        await request(`/api/users/delete/${user.userId}`, { method: "DELETE" });
        clearCurrentUser();
        location.href = "index.html";
      } catch (error) {
        document.querySelector("#profileResult").innerHTML = errorMessage(error);
      }
    });
  } catch (error) {
    area.innerHTML = errorMessage(error);
  }
}

export { profilePage };
