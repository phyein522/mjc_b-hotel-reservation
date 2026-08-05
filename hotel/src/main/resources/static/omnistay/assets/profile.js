import { request, qs, escapeHtml } from "./api.js";
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

async function profilePage() {
  const sessionUser = getCurrentUser();
  if (!sessionUser?.userId) {
    location.href = `login.html?redirect=${encodeURIComponent("profile.html")}`;
    return;
  }
  userShell("profile", `${title("내 정보", "백엔드에 저장된 회원 정보를 조회하고 수정합니다.")}<section id="profileArea">${empty("회원 정보를 불러오는 중입니다.")}</section>`);
  const area = document.querySelector("#profileArea");
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
