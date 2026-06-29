package com.mjc.hotel.sales_analysis.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SalesDashboardQueryDto {
    private BigDecimal totalRevenue;
    private Long bookingCount;
    private Long totalNights;
}
