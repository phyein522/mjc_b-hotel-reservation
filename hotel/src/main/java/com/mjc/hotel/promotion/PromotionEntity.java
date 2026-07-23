package com.mjc.hotel.promotion;

import com.mjc.hotel.common.dto.BaseEntity;
import com.mjc.hotel.rooms.dto.IRoom;
import com.mjc.hotel.rooms.dto.RoomEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name="promotion")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder

public class PromotionEntity extends BaseEntity implements IPromotion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long proId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private DiscountTypeEnum disType;

    @Column(nullable = false)
    private BigDecimal disValue;

    @Column(nullable = false)
    private LocalDateTime startDate;

    @Column(nullable = false)
    private LocalDateTime endDate;
    private Integer resCount;
    private PromotionStateTypeEnum status;



    @Transient
    private Long roomId;
    @Transient
    private Long userId;

    @JoinColumn(name = "room_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private RoomEntity room;

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
            this.room = new RoomEntity();
        }

        this.room.copyMembers(room, true);
        this.roomId = room.getRoomId();
    }
    @Override
    public void setRoomId(Long roomId) {

        this.roomId = roomId;

        if (this.room == null) {
            this.room = new RoomEntity();
        }

        this.room.setRoomId(roomId);
    }
}
