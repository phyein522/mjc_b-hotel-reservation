package com.mjc.hotel.sales_analysis.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SalesDashboardResponse {
    private Long hotelId;
    private String hotelName;
    private Integer year;
    private Integer month;
    private BigDecimal targetRevenue;
    private DashboardMetrics metrics;
    private List<MonthlyRevenueDto> monthlyTrend;
    private List<RoomTypeRevenueDto> roomTypeRevenue;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class DashboardMetrics {
        private RevenueMetric totalRevenue;
        private RateMetric occupancyRate;
        private RateSimple todayOccupancyRate;
        private RevenueSimple averageDailyRate;
        private CountMetric bookingCount;
        private RateMetric returningGuestRate;
        private RateSimple vipReturningGuestRate;
    }

    // 금액용 메트릭 (매출 등)
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class RevenueMetric {
        private BigDecimal value;
        private Double changeRate;
        private Boolean isIncreased;
    }

    // 비율용 메트릭 (점유율, 재방문율 등)
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class RateMetric {
        private Double value;
        private Double changeRate;
        private Boolean isIncreased;
    }

    // 카운트용 메트릭 (예약건수 등)
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CountMetric {
        private Long value;
        private Double changeRate;
        private Boolean isIncreased;
    }

    // 금액 단순 수치 (평균 객단가 등)
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class RevenueSimple {
        private BigDecimal value;
    }

    // 비율 단순 수치 (VIP 재방문율 등)
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class RateSimple {
        private Double value;
    }
}
