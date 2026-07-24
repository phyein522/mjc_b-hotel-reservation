package com.mjc.hotel.rates.dto.request;

import com.mjc.hotel.rooms.enums.RoomBedOption;
import com.mjc.hotel.rooms.enums.RoomStatus;
import com.mjc.hotel.rooms.enums.RoomType;
import com.mjc.hotel.rooms.enums.RoomViewOption;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;

/**
 * 호실 생성 요청 DTO
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoomCreateRequestDto {

    @NotBlank(message = "호실 이름은 필수 입력 항목입니다.")
    private String name;

    @NotBlank(message = "호실 번호는 필수 입력 항목입니다.")
    private String number;

    @NotNull(message = "층수는 필수 입력 항목입니다.")
    @Min(value = 1, message = "층수는 1층 이상이어야 합니다.")
    private Integer floor;

    @NotNull(message = "면적(크기)은 필수 입력 항목입니다.")
    @Min(value = 1, message = "면적은 1 이상이어야 합니다.")
    private Integer size;

    @NotNull(message = "기본 요금은 필수 입력 항목입니다.")
    @DecimalMin(value = "0.0", inclusive = false, message = "기본 요금은 0원보다 커야 합니다.")
    private BigDecimal basePrice;

    @NotNull(message = "성인 수용 인원은 필수입니다.")
    @Min(value = 1, message = "최대 성인 인원은 최소 1명 이상이어야 합니다.")
    private Integer maxAdult;

    @NotNull(message = "어린이 수용 인원은 필수입니다.")
    @Min(value = 0, message = "최대 어린이 인원은 0명 이상이어야 합니다.")
    private Integer maxChild;

    @NotNull(message = "객실 타입은 필수입니다.")
    private RoomType roomType; // Standard, Suite, Deluxe, Premium

    private RoomStatus roomStatus; // EnableReservation, DisableReservation

    @NotNull(message = "전망 옵션은 필수입니다.")
    private RoomViewOption roomViewOption; // CityView, RiverView, MountainView, OceanView

    @NotNull(message = "침대 옵션은 필수입니다.")
    private RoomBedOption roomBedOption; // Floor, DoubleBed, QueenBed

    private String description;

    private RoomAmenityRequestDto amenities;
}
