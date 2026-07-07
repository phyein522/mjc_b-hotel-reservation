package com.mjc.hotel.promotion;

import com.mjc.hotel.common.dto.BaseDto;
import com.mjc.hotel.rooms.dto.IRoom;
import com.mjc.hotel.rooms.dto.RoomDto;
import lombok.*;
import lombok.experimental.SuperBuilder;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class PromotionDto extends BaseDto implements IPromotion {
    private Long proId;
    private String name;
    private String description;
    private DiscountTypeEnum disType;
    private String disValue;
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
    public void setRoom(IRoom room) {

        if (room == null) {
            return;
        }

        if (this.room == null) {
            this.room = new RoomDto();
        }

        this.room.copyMembers(room, true);
        this.roomId = room.getRoomId();
    }
    @Override
    public void setRoomId(Long roomId) {

        this.roomId = roomId;

        if (this.room == null) {
            this.room = new RoomDto();
        }

        this.room.setRoomId(roomId);
    }
}
