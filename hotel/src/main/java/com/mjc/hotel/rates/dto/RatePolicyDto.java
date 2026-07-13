package com.mjc.hotel.rates.dto;

import com.mjc.hotel.common.dto.BaseDto;
import com.mjc.hotel.rates.enums.ChildRateType;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.time.LocalTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@SuperBuilder
@tools.jackson.databind.annotation.JsonDeserialize(as = RatePolicyDto.class)
public class RatePolicyDto extends BaseDto {
    private Long policyId;
    private Integer minStayNights;
    private LocalTime checkInTime;
    private LocalTime checkOutTime;
    private Integer cancelDeadlineDays;
    private BigDecimal cancelFeeRate;
    private Integer freeChildAge;
    private ChildRateType childRateType;
    private BigDecimal childDiscountRate;
    private Long hotelId;
}
