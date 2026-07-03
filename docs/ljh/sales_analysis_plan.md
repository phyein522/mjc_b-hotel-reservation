# [MJC Hotel] 매출 분석(Sales Analysis) 기능 설계 기획서 및 2주 구현 계획

본 문서는 MJC 호텔 예약 프로젝트 중 **매출 분석(Sales Analysis)** 기능을 2주 내에 성공적으로 구현하기 위한 세부 기획 및 마일스톤 계획서입니다. 피드백을 반영하여 타 팀 도메인 엔티티와의 의존성을 최소화하고, DTO 및 인터페이스 기반의 유연한 구조로 설계하였습니다.

---

## 1. 프로젝트 개요 및 구현 목표
* **목표**: 호텔 관리자가 특정 월의 매출, 객실 점유율, 채널별 비중, VIP 고객 재방문율 등을 한눈에 확인하고 의사결정을 내릴 수 있는 대시보드 API를 개발합니다.
* **기간**: 2주 (10 영업일)
* **협업 및 아키텍처 전략**:
  * **DTO 직접 프로젝션**: 독립적인 Stub Entity를 추가 생성하여 엔티티를 중복 관리하기보다, **MyBatis XML 매퍼**를 통해 SQL 조회 결과를 직접 **DTO 패키지**로 매핑합니다. 이를 통해 JPA 엔티티 의존성을 원천 제거하고 DB 테이블 레벨에서만 결합도를 가집니다.
  * **인터페이스 기반 분리**: 서비스 레이어를 인터페이스와 구현체로 분리하고 DTO를 반환하게 설계하여, 향후 타 팀 도메인 변경이나 프론트엔드 API 규격 변경 시에도 내부 비즈니스 로직만 최소한으로 수정될 수 있도록 격리합니다.

---

## 2. 시스템 아키텍처 및 폴더 구조
머지 충돌을 예방하고 독립성을 확보하기 위해 `sales_analysis` 폴더 내부를 다음과 같이 구조화합니다.

```
com.mjc.hotel.sales_analysis
├── config                 # 매출 분석 관련 설정 (MyBatis 설정 등)
├── controller             # REST Controller 레이어 (변경 가능성을 대비해 DTO 반환)
│   ├── SalesAnalysisController.java
│   └── advice             # 패키지 내부 예외 처리를 위한 컨트롤러 어드바이스
│       └── SalesAnalysisExceptionHandler.java
├── service                # 비즈니스 로직 및 인터페이스
│   ├── SalesAnalysisService.java (Interface)
│   ├── SalesAnalysisServiceImpl.java (Implementation - 실 데이터 쿼리용)
│   └── SalesAnalysisServiceStub.java (Implementation - 테스트용 모의 데이터 반환)
├── repository             # MyBatis Mapper 선언
│   └── mapper
│       └── SalesAnalysisMapper.java (Interface)
└── dto                    # API 응답 및 쿼리 프로젝션용 DTO (핵심 데이터 구조 격리)
    ├── SalesDashboardResponse.java
    ├── SalesDashboardQueryDto.java
    ├── RoomTypeRevenueDto.java
    ├── ChannelShareDto.java
    ├── TopBookingDto.java
    └── MonthlyRevenueDto.java
```
> **[구조적 특징]**: `sales_analysis` 패키지 내부에 별도의 JPA Entity를 선언하지 않습니다. 데이터 조회는 MyBatis 매퍼를 통해 데이터베이스 테이블로부터 DTO로 직접 매핑하거나, 타 팀의 공통 Repository를 단순 조회용으로만 참조합니다.

---

## 3. 핵심 비즈니스 로직 및 연산 공식
대시보드 지표를 산출할 때 적용할 계산식 및 예외 처리 로직입니다.

### ① 객실 점유율 (Occupancy Rate)
* **공식**:
  $$\text{당일 객실 점유율(\%)} = \left( \frac{\text{당일 판매(예약)된 객실 수}}{\text{호텔 내 총 활성화된 객실 수}} \right) \times 100$$
  $$\text{월평균 객실 점유율(\%)} = \left( \frac{\text{해당 월 총 판매 박수(Nights)}}{\text{호텔 내 총 활성화된 객실 수} \times \text{해당 월 총 일수(Days)}} \right) \times 100$$
* **주의**: 분모가 0이 되는 경우(등록된 객실이 없는 경우) `0.0`으로 반환 처리합니다.

### ② 평균 객단가 (ADR: Average Daily Rate)
* **공식**:
  $$\text{ADR} = \frac{\text{해당 월의 총 객실 매출액 (환불 제외)}}{\text{해당 월의 총 판매 객실 박수 (Nights)}}$$
* **주의**: 판매 박수가 0인 경우 `0`으로 반환합니다.

### ③ 전월 대비 매출/예약/점유율 증감률 (%)
* **공식**:
  $$\text{증감률(\%)} = \left( \frac{\text{당월 값} - \text{전월 값}}{\text{전월 값}} \right) \times 100$$
* **주의**: 전월 값이 0이거나 데이터가 없는 경우 증감률은 `0.0`으로 처리하고, `isIncreased`는 `false`로 반환합니다. 결과는 소수점 둘째 자리까지 반올림하여 반환합니다.

### ④ 재방문율 및 VIP 재방문율
* **일반 재방문율**: 해당 월에 투숙(체크인 기준)한 전체 유니크 고객 중, 과거 해당 호텔에 투숙 완료(정상 체크아웃)한 이력이 1회 이상 있는 고객의 비율.
* **VIP 재방문율**: 해당 월 투숙 고객 중 `Membership` 등급이 `VIP`인 고객을 대상으로 동일하게 산출합니다.

---

## 4. API 명세 설계
프론트엔드와 신속히 연동하고 협업 안정성을 위해 엔드포인트와 응답 DTO 규격을 확정합니다.

### ① 종합 대시보드 데이터 조회
* **Endpoint**: `GET /api/sales/dashboard`
* **Query Params**: 
  * `hotelId` (Long, 필수)
  * `targetMonth` (String, 필수, Format: `YYYY-MM`)
* **Response 예시 (`SalesDashboardResponse`)**:
```json
{
  "hotelId": 9999,
  "hotelName": "테스트 MJC 호텔",
  "year": 2026,
  "month": 6,
  "metrics": {
    "totalRevenue": {
      "value": 1150000.00,
      "changeRate": 64.29,
      "isIncreased": true
    },
    "bookingCount": {
      "value": 3,
      "changeRate": 50.00,
      "isIncreased": true
    },
    "occupancyRate": {
      "value": 45.50,
      "changeRate": 2.10,
      "isIncreased": true
    },
    "adr": {
      "value": 164285.71,
      "changeRate": -1.25,
      "isIncreased": false
    },
    "returningGuestRate": {
      "value": 66.67,
      "changeRate": 5.00,
      "isIncreased": true
    },
    "vipReturningGuestRate": {
      "value": 100.00,
      "changeRate": 0.00,
      "isIncreased": false
    }
  },
  "roomTypeRevenue": [
    { "roomTypeId": "STANDARD", "roomTypeName": "스탠다드 룸", "roomCount": 1, "revenue": 100000.00 },
    { "roomTypeId": "DELUXE", "roomTypeName": "디럭스 룸", "roomCount": 1, "revenue": 150000.00 },
    { "roomTypeId": "SUITE", "roomTypeName": "스위트 룸", "roomCount": 1, "revenue": 900000.00 }
  ],
  "channelShares": [
    { "channelId": 2, "channelName": "아고다", "bookingCount": 1, "bookingShareRate": 33.33, "revenue": 900000.00, "revenueShareRate": 78.26 },
    { "channelId": 1, "channelName": "야놀자", "bookingCount": 1, "bookingShareRate": 33.33, "revenue": 150000.00, "revenueShareRate": 13.04 },
    { "channelId": 3, "channelName": "직접예약", "bookingCount": 1, "bookingShareRate": 33.33, "revenue": 100000.00, "revenueShareRate": 8.70 }
  ],
  "topBookings": [
    { "rank": 1, "guestName": "박대표", "roomName": "T103호 (스위트 룸)", "amount": 900000.00, "nights": 3 },
    { "rank": 2, "guestName": "김철수", "roomName": "T102호 (디럭스 룸)", "amount": 150000.00, "nights": 1 },
    { "rank": 3, "guestName": "이영희", "roomName": "T101호 (스탠다드 룸)", "amount": 100000.00, "nights": 1 }
  ]
}
```

### ② 보조 API 목록
* `GET /api/sales/monthly?hotelId=9999&startDate=2025-11-01`: 최근 7개월 월별 매출 트렌드 데이터 리스트 반환
* `GET /api/sales/channels?hotelId=9999&targetMonth=2026-06`: 채널별 예약 건수 및 매출 비중 리스트 반환
* `GET /api/sales/top-bookings?hotelId=9999&targetMonth=2026-06`: 결제 금액 기준 TOP 5 예약 내역 목록 반환
* `GET /api/sales/rooms?hotelId=9999&targetMonth=2026-06`: 객실 유형별 통계 정보 반환

---

## 5. 데이터베이스 쿼리 및 MyBatis 매퍼 설계
[SalesAnalysisMapper.xml](file:///mnt/c/Users/LG/IdeaProjects/mjc_b-hotel-reservation/hotel/src/main/resources/mapper/sales_analysis/SalesAnalysisMapper.xml)에 설계된 쿼리를 기반으로 비즈니스 로직에 필요한 정보를 수집합니다.

1. **기본 매트릭 조회 (`getBasicMetrics`)**: 주어진 기간의 총 결제 완료 매출, 정상 예약 건수, 판매 숙박 일수(Nights) 합산 결과를 DTO(`SalesDashboardQueryDto`)로 직접 조회
2. **당일 점유 객실 조회 (`getTodayOccupiedRooms`)**: 오늘 날짜 기준으로 체크인 완료되었으며 체크아웃 전인 고유 객실 수 조회
3. **고객 재방문율 (`getReturningGuestRate`)**: 해당 기간 예약자 중 이전 예약 이력 존재 비율 계산
4. **VIP 고객 재방문율 (`getVipReturningGuestRate`)**: 해당 기간 예약자 중 Membership 등급이 'VIP'인 사용자의 이전 예약 이력 비율 계산
5. **최근 7개월 월 매출 추이 (`getMonthlyRevenueTrend`)**: 지정일 이후 월단위 매출 합계 그룹핑 조회
6. **채널별 매출 비중 (`getChannelShares`)**: 야놀자, 아고다, 직접예약 등 예약 경로(채널)별 점유율 계산하여 DTO(`ChannelShareDto`)로 반환
7. **매출 상위 5건 예약 (`getTopBookings`)**: 해당 월 결제 금액 내림차순 정렬 및 상위 5건 데이터 DTO(`TopBookingDto`)로 반환

---

## 6. 세부 구현 가이드 (보완 사항)

### ① Spring Profile을 활용한 Stub/Real 서비스 스위칭 구현
설정 변경만으로 Mock 데이터 기반의 Stub Service와 실제 DB를 조회하는 Real Service를 유연하게 바꿀 수 있도록 구현하는 상세 가이드입니다.

#### 1. 서비스 인터페이스 정의
`com.mjc.hotel.sales_analysis.service.SalesAnalysisService`
```java
package com.mjc.hotel.sales_analysis.service;

import com.mjc.hotel.sales_analysis.dto.SalesDashboardResponse;

public interface SalesAnalysisService {
    SalesDashboardResponse getDashboardData(Long hotelId, String targetMonth);
}
```

#### 2. Stub Service 구현체 작성 (로컬/테스트 단계용)
`com.mjc.hotel.sales_analysis.service.SalesAnalysisServiceStub`
* `@Profile("stub")` 어노테이션을 설정하여, `stub` 프로필이 활성화될 때만 Bean으로 자동 등록되도록 설정합니다.
```java
package com.mjc.hotel.sales_analysis.service;

import com.mjc.hotel.sales_analysis.dto.*;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.List;

@Service
@Profile("stub")
public class SalesAnalysisServiceStub implements SalesAnalysisService {
    @Override
    public SalesDashboardResponse getDashboardData(Long hotelId, String targetMonth) {
        // 하드코딩된 사실적인 테스트용 DTO 반환 로직 구성
        return SalesDashboardResponse.builder()
                .hotelId(hotelId)
                .hotelName("MJC 가상 테스트 호텔")
                .year(2026)
                .month(6)
                .metrics(buildStubMetrics())
                .build();
    }
    
    private DashboardMetricsDto buildStubMetrics() {
        return DashboardMetricsDto.builder()
                .totalRevenue(new MetricValueDto(new BigDecimal("1150000.00"), 64.29, true))
                .bookingCount(new MetricValueDto(3L, 50.00, true))
                .occupancyRate(new MetricValueDto(45.50, 2.10, true))
                .adr(new MetricValueDto(164285.71, -1.25, false))
                .returningGuestRate(new MetricValueDto(66.67, 5.00, true))
                .vipReturningGuestRate(new MetricValueDto(100.00, 0.00, false))
                .build();
    }
}
```

#### 3. Real Service 구현체 작성 (통합 배포용)
`com.mjc.hotel.sales_analysis.service.SalesAnalysisServiceImpl`
* `stub` 프로필이 꺼져 있을 때(`!stub`) 빈으로 등록되어 실제 MyBatis 및 JPA Repository 호출을 처리합니다.
```java
package com.mjc.hotel.sales_analysis.service;

import com.mjc.hotel.sales_analysis.dto.SalesDashboardResponse;
import com.mjc.hotel.sales_analysis.repository.mapper.SalesAnalysisMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Profile("!stub")
@RequiredArgsConstructor
public class SalesAnalysisServiceImpl implements SalesAnalysisService {
    private final SalesAnalysisMapper salesAnalysisMapper;

    @Override
    public SalesDashboardResponse getDashboardData(Long hotelId, String targetMonth) {
        // 실제 데이터베이스 쿼리 호출 및 계산 연산 수행 로직
        return null; // TODO: 2주차 구현 예정
    }
}
```

#### 4. 로컬 환경에서 Stub 설정하는 방법
[application.yaml](file:///mnt/c/Users/LG/IdeaProjects/mjc_b-hotel-reservation/hotel/src/main/resources/application.yaml) 파일에 아래와 같이 `active` 프로필을 활성화합니다.
```yaml
spring:
  profiles:
    active: stub
```
> **[효과]**: 이 설정을 추가하면 DB 쿼리가 불가능하거나 테이블 형식이 깨져 있어도 로컬 서버는 오류 없이 구동되고, 대시보드 API는 즉시 작동 가능한 가상 데이터를 응답합니다.

---

### ② 공통 Exception Handler 및 패키지 전용 예외 격리 구현
타 팀원의 공통 소스 파일인 `GlobalExceptionHandler.java`를 수정하여 충돌을 유발하기보다, `sales_analysis` 패키지 전용 로컬 예외 처리기를 작성하여 오류를 안전하게 공통 규격인 `ApiResponse`로 포장하는 방법입니다.

#### 1. 패키지 전용 예외 핸들러 작성
`com.mjc.hotel.sales_analysis.controller.advice.SalesAnalysisExceptionHandler`
* `@RestControllerAdvice(assignableTypes = SalesAnalysisController.class)` 설정을 통해, **해당 컨트롤러에서 발생하는 에러만 캡처**하도록 한정하여 전역 코드 오작동 위험을 방지합니다.
```java
package com.mjc.hotel.sales_analysis.controller.advice;

import com.mjc.hotel.common.ApiResponse;
import com.mjc.hotel.common.ResponseCode;
import com.mjc.hotel.sales_analysis.controller.SalesAnalysisController;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice(assignableTypes = SalesAnalysisController.class)
public class SalesAnalysisExceptionHandler {

    // 입력값 검증 실패 (예: YYYY-MM 형식이 아닌 값이 targetMonth로 유입 시)
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<String>> handleIllegalArgument(IllegalArgumentException ex) {
        log.warn("매출 분석 API 요청 입력값 오류: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.make(ResponseCode.BAD_REQUEST, ex.getMessage(), null));
    }

    // 예기치 못한 매출 데이터 연산 실패 등 기본 처리
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<String>> handleGeneralException(Exception ex) {
        log.error("매출 분석 처리 도중 에러 발생", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.make(ResponseCode.SELECT_ERROR, "매출 분석 데이터를 처리하는 도중 오류가 발생했습니다.", ex.getMessage()));
    }
}
```

---

## 7. 2주 (10일) 상세 구현 마일스톤 계획

### [1주차] DTO 설계 및 MyBatis 매핑 구조 확립 (Day 1 ~ Day 5)
* **Day 1: DTO 명세 및 Controller 뼈대 설계**
  * 프론트엔드 연동 유연성을 위해 사용될 DTO 클래스들(`SalesDashboardResponse`, `ChannelShareDto` 등) 정의
  * `SalesAnalysisController` 엔드포인트 뼈대 작성
* **Day 2: MyBatis Mapper 인터페이스 정의 및 XML 연결**
  * [SalesAnalysisMapper.java](file:///mnt/c/Users/LG/IdeaProjects/mjc_b-hotel-reservation/hotel/src/main/java/com/mjc/hotel/sales_analysis/mapper/SalesAnalysisMapper.java) 인터페이스 정의 및 XML의 `<select>` 태그 ID와 연동 검증
* **Day 3: 서비스 인터페이스 및 Stub 구현체 작성**
  * [SalesAnalysisService.java](file:///mnt/c/Users/LG/IdeaProjects/mjc_b-hotel-reservation/hotel/src/main/java/com/mjc/hotel/sales_analysis/service/SalesAnalysisService.java) 인터페이스 정의
  * `@Profile("stub")`을 가진 `SalesAnalysisServiceStub` 클래스 구현 완료하여 로컬 테스트 준비
* **Day 4: 패키지 전용 예외 핸들러 구현**
  * `SalesAnalysisExceptionHandler` 클래스를 구현하여 패키지 내 예외 상황 발생 시 공통 `ApiResponse` 에러 메시지가 정상 포맷으로 출력되도록 구성
* **Day 5: 1주차 동작 검증 및 Mock 테스트**
  * 로컬 실행 프로필에 `stub` 추가 후, Swagger UI 또는 API 클라이언트(Postman 등)로 API 호출 테스트 수행하여 정상 더미 데이터 및 유효성 검증 오류 테스트 완료

---

### [2주차] 비즈니스 로직 구현 및 통합 테스트 (Day 6 ~ Day 10)
* **Day 6: 실 데이터 연동 및 계산 로직(ADR, 점유율) 구현**
  * Stub 구현체를 실제 비즈니스 로직을 수행하는 구현체(`SalesAnalysisServiceImpl`)로 전환
  * 당월 및 전월의 `getBasicMetrics` 결과를 호출하고, 자바 코드 단에서 ADR 및 점유율, 증감률 계산 로직 구현
* **Day 7: 재방문율 및 채널 비중 연동**
  * MyBatis를 통해 산출된 재방문율, VIP 재방문율 데이터 및 채널 예약 점유율을 종합 DTO 객체에 삽입
* **Day 8: 예외 안전장치 및 기본값 처리 구현**
  * DB에 데이터가 비어 있거나 전월 데이터가 없는 경우 연산 에러가 아닌 기본값(`0.0` 또는 `0`)을 안전하게 리턴하도록 방어 코드 구현
* **Day 9: 통합 테스트 코드 작성 및 검증**
  * H2 또는 MySQL 테스트 스키마를 사용하여 데이터 적재 후 [SalesAnalysisServiceTest.java](file:///mnt/c/Users/LG/IdeaProjects/mjc_b-hotel-reservation/hotel/src/test/java/com/mjc/hotel/sales_analysis/SalesAnalysisServiceTest.java) 시나리오 검증
* **Day 10: 최종 코드 정리 및 머지 준비**
  * 사용하지 않는 임시 클래스 제거, 빌드 및 테스트 패스 확인 후 PR 작성

---

## 8. 리스크 관리 및 예외 전략
* **리스크 1**: 타 팀원이 Entity 구조를 수정하여 JPA 빌드나 필드명이 깨지는 현상
  * **해결**: 본 패키지는 JPA Entity 대신 직접 SQL 쿼리와 DTO를 매핑하므로 타 팀의 Java Entity 수정에 전혀 영향을 받지 않습니다. 오직 데이터베이스 테이블 컬럼명이 변경되는 경우에만 MyBatis XML의 SELECT 쿼리 부분만 수정하면 되므로 결합도가 매우 낮습니다.
* **리스크 2**: DB 데이터 부족으로 인해 API 호출 시 에러 발생
  * **해결**: 데이터가 없거나 쿼리 결과가 `null`인 경우, 서비스 단에서 에러가 아닌 기본값(0, 0.0, "-")으로 대체 처리하여 정상 응답(ApiResponse)을 리턴합니다.
