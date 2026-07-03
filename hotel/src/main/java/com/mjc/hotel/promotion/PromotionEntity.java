package com.mjc.hotel.promotion;

import com.mjc.hotel.common.dto.BaseEntity;
import com.mjc.hotel.rooms.dto.RoomEntity;
import jakarta.persistence.*;
import lombok.*;

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
    private String disType;

    @Column(nullable = false)
    private String disValue;

    @Column(nullable = false)
    private String startDate;

    private String endDate;
    private String resCount;
    private String conversionRate;
    private String status;

    @Transient
    private Long roomId;

    @JoinColumn(name = "room_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private RoomEntity room;

    @Override
    public Long getRoomId() {

        if (room != null) {
            return room.getHotelId();
        }

        return roomId;
    }

    @Override
    public void setRoomId(Long roomId) {

        this.roomId = roomId;

        if (room == null) {
            room = new RoomEntity();
        }

        room.setHotelId(roomId);
    }
}
