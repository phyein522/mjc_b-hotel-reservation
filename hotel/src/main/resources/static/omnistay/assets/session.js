import {
  app,
  rel,
  getCurrentUser,
  clearCurrentUser,
  hasAdminRole,
  hasAccessToken
} from "./core.js";

const authenticatedPages = new Set(["booking", "bookings", "reviews", "profile"]);

function redirectToLogin(reason = "authentication") {
  const currentPage = `${location.pathname.split("/").pop() || "index.html"}${location.search}`;
  location.replace(`${rel}login.html?reason=${encodeURIComponent(reason)}&redirect=${encodeURIComponent(currentPage)}`);
}

window.addEventListener("omnistay:authentication-required", () => {
  clearCurrentUser();
  redirectToLogin("expired");
});

export function startRoute(route, page) {
  const currentUser = getCurrentUser();
  const isAdminPage = page?.startsWith("admin-");

  if ((authenticatedPages.has(page) || isAdminPage) && (!currentUser || !hasAccessToken())) {
    clearCurrentUser();
    redirectToLogin(isAdminPage ? "admin" : page);
    return;
  }
  if (isAdminPage && !hasAdminRole(currentUser)) {
    app.innerHTML = `
      <main class="page">
        <div class="message error">관리자 권한이 필요한 화면입니다.</div>
        <a class="btn primary" href="../index.html">사용자 화면으로 이동</a>
      </main>
    `;
    return;
  }
  route?.();
}
