# 호텔 예약 프로젝트

## MySQL DB 쿼리
```
CREATE DATABASE hotel DEFAULT CHARACTER SET utf8mb4;
CREATE USER 'hotel_user'@'%' IDENTIFIED BY 'hotel9876!';
GRANT ALL PRIVILEGES ON hotel.* TO 'hotel_user'@'%';
FLUSH PRIVILEGES;

CREATE DATABASE hotel_test DEFAULT CHARACTER SET utf8mb4;
GRANT ALL PRIVILEGES ON hotel_test.* TO 'hotel_user'@'%';
FLUSH PRIVILEGES;
```

## 이메일 인증과 Google 로그인 설정

`hotel/.env` 파일에 아래 값을 입력하면 애플리케이션 시작 시 자동으로 읽는다.

```properties
MAIL_USERNAME=발신용 Gmail 주소
MAIL_PASSWORD=Google 앱 비밀번호
EMAIL_VERIFICATION_SECRET=충분히 긴 임의 문자열
GOOGLE_CLIENT_ID=Google Cloud 웹 애플리케이션 클라이언트 ID
```

Gmail을 사용할 때 `MAIL_PASSWORD`에는 계정 비밀번호가 아니라 Google 계정에서 발급한 앱 비밀번호를 입력한다.
`MAIL_FROM`을 생략하면 `MAIL_USERNAME`이 발신 주소로 자동 사용된다.
`MAIL_USERNAME`은 인증 메일 발신 계정일 뿐이며, 회원가입 이메일 도메인은 제한하지 않는다.
Gmail, 네이버, 다음, Outlook과 사용자 도메인 등 정상 이메일 주소를 모두 사용할 수 있다.
다른 SMTP 서버를 사용하면 `MAIL_HOST`, `MAIL_PORT`, `MAIL_USERNAME`, `MAIL_PASSWORD`, `MAIL_FROM`을 해당 서버 값으로 설정한다.
실제 `.env` 파일은 Git에서 제외되며, 공유용 형식은 `.env.example`에서 확인할 수 있다.

자격 증명을 콘솔 출력에 노출하지 않고 설정하려면 Google에서 앱 비밀번호와 웹 클라이언트 ID를
발급한 뒤 아래 스크립트를 실행한다.

```powershell
.\configure-auth.ps1
```

스크립트는 Gmail 앱 비밀번호와 Google OAuth 웹 클라이언트 ID 형식을 확인하고 `hotel/.env`에
저장한다. 설정 후 Spring Boot 서버를 재시작해야 한다.

Google 로그인은 Google Cloud에서 OAuth 2.0 웹 애플리케이션 클라이언트를 만든 뒤
`GOOGLE_CLIENT_ID`에 웹 클라이언트 ID를 입력한다. 로컬 개발용 승인된 JavaScript 원본에는
`http://localhost:8989`를 등록한다. 모든 Google 계정에서 사용하려면 앱 대상 사용자를
`External`로 설정하고 게시 상태를 `In production`으로 전환한다.

인증 API:

- `GET /api/auth/config`: 프런트용 인증 설정 조회
- `POST /api/auth/email/send-code`: 이메일 인증번호 발송
- `POST /api/auth/email/verify`: 인증번호 확인 및 일회성 가입 토큰 발급
- `POST /api/auth/signup`: 인증된 이메일로 회원가입
- `POST /api/auth/login`: 일반 이메일과 비밀번호로 로그인
- `POST /api/auth/google`: Google ID 토큰 검증 후 로그인 또는 신규 회원 생성
