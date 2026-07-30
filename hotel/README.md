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
MAIL_FROM=발신용 Gmail 주소
EMAIL_VERIFICATION_SECRET=충분히 긴 임의 문자열
GOOGLE_CLIENT_ID=Google Cloud 웹 애플리케이션 클라이언트 ID
```

Gmail을 사용할 때 `MAIL_PASSWORD`에는 계정 비밀번호가 아니라 Google 계정에서 발급한 앱 비밀번호를 입력한다.
다른 SMTP 서버를 사용하면 `MAIL_HOST`, `MAIL_PORT`, `MAIL_USERNAME`, `MAIL_PASSWORD`, `MAIL_FROM`을 해당 서버 값으로 설정한다.
실제 `.env` 파일은 Git에서 제외되며, 공유용 형식은 `.env.example`에서 확인할 수 있다.

인증 API:

- `GET /api/auth/config`: 프런트용 인증 설정 조회
- `POST /api/auth/email/send-code`: 이메일 인증번호 발송
- `POST /api/auth/email/verify`: 인증번호 확인 및 일회성 가입 토큰 발급
- `POST /api/auth/signup`: 인증된 이메일로 회원가입
- `POST /api/auth/google`: Google ID 토큰 검증 후 로그인 또는 신규 회원 생성
