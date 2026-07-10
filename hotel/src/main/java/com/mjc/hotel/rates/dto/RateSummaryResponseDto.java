package com.mjc.hotel.rates.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class RateSummaryResponseDto {
    private BigDecimal averageBasePrice;
    private BigDecimal priceChangeRate;
    private String currentSeasonName;
    private BigDecimal currentStandardWeekdayPrice;
    private BigDecimal increaseRateComparedToOffSeason;
    private LocalDate nextSeasonStartDate;
    private Long nextSeasonDDay;
    private Integer nextSeasonRoomTypeCount;
}
