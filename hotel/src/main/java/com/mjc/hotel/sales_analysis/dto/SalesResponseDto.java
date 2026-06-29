package com.mjc.hotel.sales_analysis.dto;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
public class SalesResponseDto {

    private Long hotelId;
    private String hotelName;
    private int year;

    private BigDecimal currentYearSales;   // 올해 매출
    private BigDecimal previousYearSales;  // 작년 매출
    private double changeRate;             // 증감률 (%)
    private String trend;                  // UP / DOWN / SAME
}
