package com.mjc.hotel.sales_analysis.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Schema(description = "객실 유형별 보유 개수 및 매출 통계 구조")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RoomTypeRevenueDto {

    @Schema(description = "객실 유형 ID (코드)", example = "STANDARD")
    private String roomTypeId;

    @Schema(description = "객실 유형 이름", example = "스탠다드")
    private String roomTypeName;

    @Schema(description = "해당 유형의 활성 객실 총 개수", example = "1")
    private Integer roomCount;

    @Schema(description = "해당 유형의 이번달 총 매출액", example = "100000.00")
    private BigDecimal revenue;
}
