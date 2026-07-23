package com.mjc.hotel.rates.dto.request;

import com.mjc.hotel.rooms.enums.RoomBedOption;
import com.mjc.hotel.rooms.enums.RoomStatus;
import com.mjc.hotel.rooms.enums.RoomType;
import com.mjc.hotel.rooms.enums.RoomViewOption;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import lombok.*;

import java.math.BigDecimal;

/**
 * 호실 정보 수정 요청 DTO
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoomUpdateRequestDto {

    private String name;
    private String number;

    @Min(value = 1, message = "층수는 1층 이상이어야 합니다.")
    private Integer floor;

    @Min(value = 1, message = "면적은 1 이상이어야 합니다.")
    private Integer size;

    @DecimalMin(value = "0.0", inclusive = false, message = "기본 요금은 0원보다 커야 합니다.")
    private BigDecimal basePrice;

    @Min(value = 1, message = "최대 성인 인원은 최소 1명 이상이어야 합니다.")
    private Integer maxAdult;

    @Min(value = 0, message = "최대 어린이 인원은 0명 이상이어야 합니다.")
    private Integer maxChild;

    private Boolean isActive;
    private RoomType roomType;
    private RoomStatus roomStatus;
    private RoomViewOption roomViewOption;
    private RoomBedOption roomBedOption;

    private String description;
    private RoomAmenityRequestDto amenities;
}
