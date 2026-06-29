package com.mjc.hotel.sales_analysis.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Schema(description = "매출 상위 예약 상세 정보")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TopBookingDto {

    @Schema(description = "순위", example = "1")
    private Integer rank;

    @Schema(description = "예약자 성함", example = "박대표")
    private String guestName;

    @Schema(description = "이용 객실명", example = "103호 (스위트 룸)")
    private String roomName;

    @Schema(description = "총 결제금액 (매출)", example = "900000.00")
    private BigDecimal amount;

    @Schema(description = "총 숙박 수 (박수)", example = "3")
    private Integer nights;
}
