package com.mjc.hotel.sales_analysis.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Schema(description = "월별 매출 정보 (7개월 막대그래프 렌더링용)")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MonthlyRevenueDto {

    @Schema(description = "해당 연월", example = "2026-06")
    private String yearMonth;

    @Schema(description = "해당 월 매출액", example = "1150000.00")
    private BigDecimal revenue;
}
