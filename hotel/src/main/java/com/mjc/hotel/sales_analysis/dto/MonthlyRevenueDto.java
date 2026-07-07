package com.mjc.hotel.sales_analysis.dto;

import lombok.*;
import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class MonthlyRevenueDto {
    private String yearMonth;
    private BigDecimal revenue;
}
