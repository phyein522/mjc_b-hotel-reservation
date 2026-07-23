package com.mjc.hotel.rates.dto.response;

import com.mjc.hotel.room_images.dto.RoomImageResponseDto;
import com.mjc.hotel.rooms.enums.RoomBedOption;
import com.mjc.hotel.rooms.enums.RoomStatus;
import com.mjc.hotel.rooms.enums.RoomType;
import com.mjc.hotel.room_images.dto.RoomImageResponseDto;
import com.mjc.hotel.rooms.enums.RoomViewOption;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;


/**
 * 호실 상세 정보 응답 DTO
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoomDetailResponseDto {
    private Long roomId;
    private Long hotelId;
    private String name;
    private String number;
    private Integer floor;
    private Integer size;
    private BigDecimal basePrice;
    private Integer maxAdult;
    private Integer maxChild;
    private Boolean isActive;
    private RoomType roomType;
    private RoomStatus roomStatus;
    private RoomViewOption roomViewOption;
    private RoomBedOption roomBedOption;

    private String description;
    private RoomAmenityResponseDto amenities;
    private List<RoomImageResponseDto> images;

    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;
}
