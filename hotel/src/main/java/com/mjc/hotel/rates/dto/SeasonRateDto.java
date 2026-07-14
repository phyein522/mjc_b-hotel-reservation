package com.mjc.hotel.rates.dto;

import com.mjc.hotel.common.dto.BaseDto;
import com.mjc.hotel.rooms.enums.RoomType;
import com.mjc.hotel.rates.enums.SeasonStatus;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@SuperBuilder
@tools.jackson.databind.annotation.JsonDeserialize(as = SeasonRateDto.class)
public class SeasonRateDto extends BaseDto {
    private Long seasonRateId;
    private String seasonName;
    private LocalDate startDate;
    private LocalDate endDate;
    private BigDecimal weekdayPrice;
    private BigDecimal weekendPrice;
    private SeasonStatus status;
    private Integer minStayNights;
    private Boolean weekdayPolicyEnabled;
    private RoomType roomType;
    private Long hotelId;
    private Long policyId;
}
