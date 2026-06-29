package com.mjc.hotel.sales_analysis.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Schema(description = "채널별 예약 및 매출 비중")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChannelShareDto {

    @Schema(description = "채널 ID", example = "1")
    private Long channelId;

    @Schema(description = "채널명 (예: 야놀자, 아고다, 직접예약)", example = "야놀자")
    private String channelName;

    @Schema(description = "해당 채널 예약 건수", example = "15")
    private Long bookingCount;

    @Schema(description = "예약 건수 비중 (%)", example = "50.00")
    private Double bookingShareRate;

    @Schema(description = "해당 채널 발생 매출액", example = "2250000.00")
    private BigDecimal revenue;

    @Schema(description = "매출 비중 (%)", example = "45.00")
    private Double revenueShareRate;
}
