package com.mjc.hotel.promotion;

import com.mjc.hotel.common.dto.BaseDto;
import com.mjc.hotel.rooms.dto.RoomDto;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PromotionDto extends BaseDto implements IPromotion {
    private Long proId;
    private String name;
    private String description;
    private DiscountTypeEnum disType;
    private BigDecimal disValue;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Integer resCount;
    private String status;

    private Long roomId;
    private RoomDto room;

    @Override
    public Long getRoomId() {

        if (room != null) {
            return room.getRoomId();
        }

        return roomId;
    }

    @Override
    public void setRoomId(Long roomId) {

        this.roomId = roomId;

        if (room == null) {
            room = new RoomDto();
        }

        room.setRoomId(roomId);
    }
}
