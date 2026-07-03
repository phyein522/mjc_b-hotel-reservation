package com.mjc.hotel.sales_analysis.dto;

import lombok.*;
import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class SalesDashboardQueryDto {
    private BigDecimal totalRevenue;
    private Long bookingCount;
    private Long totalNights;
}
