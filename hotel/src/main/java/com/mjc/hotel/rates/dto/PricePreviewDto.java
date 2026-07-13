package com.mjc.hotel.rates.dto;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class PricePreviewDto {
    private BigDecimal weekdayPrice;
    private BigDecimal weekendPrice;
    private Boolean weekdayPolicyEnabled;
    private BigDecimal multiplier;

    private BigDecimal calculatedWeekdayPrice;
    private BigDecimal calculatedWeekendPrice;
    private BigDecimal increaseRate;
}
