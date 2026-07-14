package com.mjc.hotel.bookings;

import com.mjc.hotel.common.dto.BaseEntity;
import com.mjc.hotel.rooms.dto.IRoom;
import com.mjc.hotel.rooms.dto.RoomEntity;
import com.mjc.hotel.user.dto.IUser;
import com.mjc.hotel.user.entity.UserEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "bookings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class BookingEntity extends BaseEntity implements IBooking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long bookingId;

    @Column(nullable = false, length = 17)
    private String bookingNo;

    @Column(nullable = false, length = 100)
    private String guestName;

    @Column(nullable = false)
    private Nationality nationality;

    @Column(nullable = false, length = 30)
    private String guestPhone;

    @Column(nullable = false)
    private String guestEmail;

    @Column(nullable = false)
    private String specialRequest;

    @Column(nullable = false)
    private Integer nights;

    @Column(nullable = false)
    private Integer adultCount;

    @Column(nullable = false)
    private Integer childCount;

    @Column(nullable = false)
    private LocalDate checkinDate;

    @Column(nullable = false)
    private LocalDate checkoutDate;

    @Column(nullable = false)
    private LocalTime checkinTime;

    @Column(nullable = false)
    private LocalTime checkoutTime;

    @Column(nullable = true)
    private LocalDateTime cancelledAt;

    @Transient
    private Long userId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @Transient
    private Long roomId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id", nullable = false)
    private RoomEntity room;

    @Override
    public Long getUserId() {
        if(this.user != null) {
            return this.user.getUserId();
        }
        return this.userId;
    }
    @Override
    public void setUserId(Long userId) {
        this.userId = userId;
        if(this.user == null) {
            this.user = new UserEntity();
        }
        this.user.setUserId(this.userId);
    }
    @Override
    public void setUser(IUser user) {
        if(user == null) {
            return;
        }
        if(this.user == null) {
            this.user = new UserEntity();
        }
        this.user.copyMembers(user, true);
        this.userId = this.user.getUserId();
    }

    @Override
    public Long getRoomId() {
        if(this.room != null) {
            return this.room.getRoomId();
        }
        return this.roomId;
    }
    @Override
    public void setRoomId(Long roomId) {
        this.roomId = roomId;
        if(this.room == null) {
            this.room = new RoomEntity();
        }
        this.room.setRoomId(this.roomId);
    }
    @Override
    public void setRoom(IRoom room) {
        if(room == null) {
            return;
        }
        if(this.room == null) {
            this.room = new RoomEntity();
        }
        this.room.copyMembers(room, true);
        this.roomId = this.room.getRoomId();
    }
}
