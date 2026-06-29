package com.mjc.hotel.sales_analysis.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Schema(description = "호텔 매출/점유율 대시보드 최종 응답 구조")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SalesDashboardResponse {

    @Schema(description = "호텔 ID", example = "1")
    private Long hotelId;

    @Schema(description = "호텔 이름", example = "그랜드 MJC 호텔")
    private String hotelName;

    @Schema(description = "조회 대상 연도", example = "2026")
    private Integer year;

    @Schema(description = "조회 대상 월", example = "6")
    private Integer month;

    @Schema(description = "이번달 매출 목표액", example = "50000000.00")
    private BigDecimal targetRevenue;

    @Schema(description = "핵심 KPIs 지표 목록")
    private DashboardMetrics metrics;

    @Schema(description = "최근 7개월 월별 매출 흐름 리스트 (막대그래프용)")
    private List<MonthlyRevenueDto> monthlyTrend;

    @Schema(description = "객실 유형별 보유 개수 및 이번달 매출액 리스트")
    private List<RoomTypeRevenueDto> roomTypeRevenue;

    @Schema(description = "대시보드 핵심 KPIs 상세 지표")
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class DashboardMetrics {
        @Schema(description = "이번달 총 매출액 및 전월비")
        private RevenueMetric totalRevenue;

        @Schema(description = "객실 점유율 및 전월비")
        private RateMetric occupancyRate;

        @Schema(description = "오늘(당일) 실시간 점유율")
        private RateSimple todayOccupancyRate;

        @Schema(description = "평균 객단가 (ADR)")
        private RevenueSimple averageDailyRate;

        @Schema(description = "이번달 총 예약 건수 및 전월비")
        private CountMetric bookingCount;

        @Schema(description = "고객 재방문율 및 전월비")
        private RateMetric returningGuestRate;

        @Schema(description = "VIP 고객 재방문율")
        private RateSimple vipReturningGuestRate;
    }

    @Schema(description = "금액용 메트릭 구조 (매출 등)")
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class RevenueMetric {
        @Schema(description = "지표 수치 (금액)", example = "1150000.00")
        private BigDecimal value;

        @Schema(description = "전월 대비 변동률 (%)", example = "64.29")
        private Double changeRate;

        @Schema(description = "전월 대비 상승 여부", example = "true")
        private Boolean isIncreased;
    }

    @Schema(description = "비율용 메트릭 구조 (점유율, 재방문율 등)")
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class RateMetric {
        @Schema(description = "지표 수치 (비율 %)", example = "66.67")
        private Double value;

        @Schema(description = "전월 대비 변동폭 (%p 차이 또는 % 변동)", example = "16.67")
        private Double changeRate;

        @Schema(description = "전월 대비 상승 여부", example = "true")
        private Boolean isIncreased;
    }

    @Schema(description = "건수용 메트릭 구조 (예약건수 등)")
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CountMetric {
        @Schema(description = "지표 수치 (건수)", example = "3")
        private Long value;

        @Schema(description = "전월 대비 변동률 (%)", example = "50.00")
        private Double changeRate;

        @Schema(description = "전월 대비 상승 여부", example = "true")
        private Boolean isIncreased;
    }

    @Schema(description = "금액 단순 수치 구조 (평균 객단가 등)")
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class RevenueSimple {
        @Schema(description = "지표 수치 (금액)", example = "230000.00")
        private BigDecimal value;
    }

    @Schema(description = "비율 단순 수치 구조 (VIP 재방문율 등)")
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class RateSimple {
        @Schema(description = "지표 수치 (비율 %)", example = "100.00")
        private Double value;
    }
}
