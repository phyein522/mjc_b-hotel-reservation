# 딥인터뷰 실행 스펙: review-crud

## 메타데이터
- 프로필: standard
- 컨텍스트 유형: brownfield
- 최종 모호성: 0.15
- 기준값: 0.20
- 컨텍스트 스냅샷: `.omx/context/review-crud-20260629T003442Z.md`

## 의도
예약관리프로그램의 리뷰 화면 구성을 참고해, 프론트엔드 없이 백엔드 리뷰 CRUD 기능을 먼저 구현한다.

## 원하는 결과
`reviews` 본문과 `reviews_photo`, `reviews_tags`, `reivews_rating`에 해당하는 사진, 태그, 카테고리별 평점을 함께 다루는 리뷰 CRUD API를 제공한다.

## 포함 범위
- 리뷰 생성
- 리뷰 목록 조회
- 리뷰 단건 조회
- 리뷰 수정
- 리뷰 삭제
- 리뷰 생성/수정/조회/삭제 시 사진 목록 포함
- 리뷰 생성/수정/조회/삭제 시 태그 목록 포함
- 리뷰 생성/수정/조회/삭제 시 카테고리별 평점 목록 포함

## 제외 범위
- 프론트엔드 구현
- H2 설정 또는 H2 의존성 추가
- `student` 패키지를 실제 도메인 기능으로 연결하는 작업
- 기존 사용자 변경사항 되돌리기
- Figma 화면 자체 구현

## 결정 경계
- `student` 패키지는 코드 구조 참고용으로만 사용한다.
- 실제 구현은 `com.mjc.hotel.review` 패키지 아래에 둔다.
- DB 연결은 `application.yaml`의 MariaDB 설정을 그대로 사용한다.
- `HELP.md`의 깨진 한글 컬럼명은 Java 코드에서는 의미가 분명한 영문 필드명으로 대응한다.

## 수용 기준
- `POST /reviews`로 리뷰 본문, 사진, 태그, 카테고리별 평점을 함께 생성할 수 있다.
- `GET /reviews`로 리뷰 목록과 하위 데이터를 함께 조회할 수 있다.
- `GET /reviews/{reviewId}`로 리뷰 단건과 하위 데이터를 함께 조회할 수 있다.
- `PUT /reviews/{reviewId}`로 리뷰 본문과 하위 데이터를 함께 수정할 수 있다.
- `DELETE /reviews/{reviewId}`로 리뷰와 하위 데이터를 함께 삭제할 수 있다.
- `./gradlew.bat compileJava`가 통과한다.

## 확인된 기술 맥락
- Spring Boot, Spring Data JPA, MyBatis, MariaDB, Lombok을 사용하는 프로젝트다.
- 리뷰 CRUD는 JPA Repository 기반으로 구현한다.
- `application.yaml`과 `build.gradle`은 변경하지 않는다.

## 잔여 리스크
- `HELP.md`의 리뷰 관련 컬럼명 일부가 깨져 있어 실제 운영 DB 컬럼명이 문서와 다를 수 있다.
- 기존 테스트 `UserServiceTests`는 현재 `UserBuilder.grade("bronze")` 호출 때문에 테스트 소스 컴파일이 실패한다.
