import { request, qs } from "./api.js";
import {
  setCurrentUser,
  setAuthTokens,
  safeRedirect,
  userShell,
  title,
  errorMessage,
  toast
} from "./core.js";

let googleIdentitySdkPromise = null;

function setAuthStatus(selector, message, type = "") {
  const node = document.querySelector(selector);
  if (!node) return;
  node.className = `auth-status ${type}`.trim();
  node.textContent = message;
}

function completeAuthentication(authenticatedUser, redirect = safeRedirect()) {
  const user = authenticatedUser?.user || authenticatedUser?.currentUser || authenticatedUser;
  if (!user?.userId) {
    throw new Error("로그인 응답에서 회원 정보를 확인할 수 없습니다.");
  }
  setAuthTokens(authenticatedUser);
  const safeUser = { ...user };
  delete safeUser.password;
  delete safeUser.accessToken;
  delete safeUser.refreshToken;
  setCurrentUser(safeUser);
  const target = new URL(redirect, window.location.href).href;
  location.assign(target);
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
          toast("Google 계정으로 로그인되었습니다.");
          completeAuthentication(user);
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
      toast("로그인되었습니다.");
      completeAuthentication(user, redirect);
    } catch (error) {
      document.querySelector("#loginResult").innerHTML = errorMessage(error);
    }
  });
  initGoogleLogin("#googleLoginButton", "#googleLoginStatus");
}


function signupPage() {
  const redirect = safeRedirect();
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
    const data = qs(form);
    delete data.verificationCode;
    try {
      let endpoint = "/api/auth/signup";
      if (!emailVerificationEnabled) {
        endpoint = "/api/users/signup";
        delete data.verificationToken;
      }
      let user = await request(endpoint, { method: "POST", body: JSON.stringify(data) });
      if (!emailVerificationEnabled) {
        user = await request("/api/auth/login", {
          method: "POST",
          body: JSON.stringify({ email: data.email, password: data.password })
        });
      }
      toast("회원가입이 완료되었습니다.");
      completeAuthentication(user, redirect);
    } catch (error) {
      document.querySelector("#signupResult").innerHTML = errorMessage(error);
    }
  });
  initGoogleLogin("#googleSignupButton", "#googleSignupStatus", "continue_with");
}


export { loginPage, signupPage };
