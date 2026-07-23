package com.mjc.hotel.rates.dto.request;

import lombok.*;

/**
 * 편의시설 토글 요청 DTO
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoomAmenityRequestDto {
    private Boolean wifi;
    private Boolean tv;
    private Boolean bathtub;
    private Boolean cityView;
    private Boolean oceanView;
    private Boolean breakfastIncluded;
    private Boolean nonSmoking;
}
