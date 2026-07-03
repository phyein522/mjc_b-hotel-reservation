package com.mjc.hotel.sales_analysis.dto;

import lombok.*;
import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class DashboardMetricsDto {
    private MetricValueDto<BigDecimal> totalRevenue;
    private MetricValueDto<Long> bookingCount;
    private MetricValueDto<Double> occupancyRate;
    private MetricValueDto<Double> adr;
    private MetricValueDto<Double> returningGuestRate;
    private MetricValueDto<Double> vipReturningGuestRate;
}
