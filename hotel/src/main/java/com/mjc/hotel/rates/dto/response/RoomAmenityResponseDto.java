package com.mjc.hotel.rates.dto.response;

import lombok.*;

/**
 * 편의시설 응답 DTO
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoomAmenityResponseDto {
    private Long roomAmenityId;
    private Boolean wifi;
    private Boolean tv;
    private Boolean bathtub;
    private Boolean cityView;
    private Boolean oceanView;
    private Boolean breakfastIncluded;
    private Boolean nonSmoking;
}
