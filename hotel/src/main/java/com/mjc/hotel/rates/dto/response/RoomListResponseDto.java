package com.mjc.hotel.rates.dto.response;

import com.mjc.hotel.rooms.enums.RoomBedOption;
import com.mjc.hotel.rooms.enums.RoomStatus;
import com.mjc.hotel.rooms.enums.RoomType;
import com.mjc.hotel.rooms.enums.RoomViewOption;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 호실 목록 조회 응답 DTO (페이징용)
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoomListResponseDto {
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
    private String thumbnailUrl;

    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;
}
