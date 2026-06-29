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
        private MetricValue totalRevenue;
        private MetricValue occupancyRate;
        private SimpleValue todayOccupancyRate;
        private SimpleValue averageDailyRate;
        private MetricValue bookingCount;
        private MetricValue returningGuestRate;
        private SimpleValue vipReturningGuestRate;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class MetricValue {
        private Double value;
        private Double changeRate;
        private Boolean isIncreased;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class SimpleValue {
        private Double value;
    }
}
