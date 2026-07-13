package com.mjc.hotel.rates.dto;

import com.mjc.hotel.common.dto.BaseDto;
import com.mjc.hotel.rates.enums.DayType;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@SuperBuilder
public class WeekdayRateDto extends BaseDto {
    private Long weekdayRateId;
    private DayType dayType;
    private BigDecimal rateMultiplierPercent;
    private Long seasonRateId;
}
