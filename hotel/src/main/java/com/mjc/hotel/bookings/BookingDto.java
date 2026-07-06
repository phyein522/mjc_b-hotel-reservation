package com.mjc.hotel.bookings;

import com.mjc.hotel.common.dto.BaseDto;
import com.mjc.hotel.hotels.HotelDto;
import com.mjc.hotel.rooms.dto.RoomDto;
import com.mjc.hotel.user.dto.UserDto;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class BookingDto extends BaseDto implements IBooking {
    private Long bookingId;
    private String bookingNo;
    private String guestName;
    private Nationality nationality;
    private String guestPhone;
    private String guestEmail;
    private String specialRequest;
    private Integer nights;
    private Integer adultCount;
    private Integer childCount;
    private LocalDate checkinDate;
    private LocalDate checkoutDate;
    private LocalTime checkinTime;
    private LocalTime checkoutTime;
    private Integer point;
    private LocalDateTime cancelledAt;
    private Integer totalAmount;

    private Long userId;
    private UserDto user;

    private Long roomId;
    private RoomDto room;

    private Long hotelId;
    private HotelDto hotel;

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
            this.user = new UserDto();
        }
        this.user.setUserId(this.userId);
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
            this.room = new RoomDto();
        }
        this.room.setRoomId(this.roomId);
    }

    @Override
    public Long getHotelId() {
        if(this.hotel != null) {
            return this.hotel.getHotelId();
        }
        return this.hotelId;
    }
    @Override
    public void setHotelId(Long hotelId) {
        this.hotelId = hotelId;
        if(this.hotel == null) {
            this.hotel = new HotelDto();
        }
        this.hotel.setHotelId(this.hotelId);
    }
}
