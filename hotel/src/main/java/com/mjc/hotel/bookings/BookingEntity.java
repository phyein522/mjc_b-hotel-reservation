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
    @Column(name = "booking_id", comment = "예약id(기본키)")
    private Long bookingId;

    @Column(nullable = false, length = 17, comment = "예약번호")
    private String bookingNo;

    @Column(nullable = false, length = 100, comment = "예약자명")
    private String guestName;

    @Column(nullable = false, comment = "예약자의 국적")
    private Nationality nationality;

    @Column(nullable = false, length = 30, comment = "예약자 전화번호")
    private String guestPhone;

    @Column(nullable = false, comment = "예약자 이메일")
    private String guestEmail;

    @Column(nullable = true, comment = "요청사항")
    private String specialRequest;

    @Column(nullable = false, comment = "숙박일")
    private Integer nights;

    @Column(nullable = false, comment = "성인 인원")
    private Integer adultCount;

    @Column(nullable = false, comment = "어린이 인원")
    private Integer childCount;

    @Column(nullable = false, comment = "체크인 날짜")
    private LocalDate checkinDate;

    @Column(nullable = false, comment = "체크아웃 날짜")
    private LocalDate checkoutDate;

    @Column(nullable = false, comment = "체크인 시간")
    private LocalTime checkinTime;

    @Column(nullable = false, comment = "체크아웃 시간")
    private LocalTime checkoutTime;

    @Column(nullable = true, comment = "취소일")
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
