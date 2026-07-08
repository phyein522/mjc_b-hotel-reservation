package com.mjc.hotel.bookings;

import com.mjc.hotel.hotels.HotelDto;
import com.mjc.hotel.hotelsimage.HotelImageResponseDto;
import com.mjc.hotel.room_images.dto.RoomImageResponseDto;
import com.mjc.hotel.rooms.dto.RoomDto;
import com.mjc.hotel.user.dto.UserDto;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class BookingResponseDto {
    private BookingDto booking;
    private UserDto user;
    private RoomDto room;
    private List<RoomImageResponseDto> roomImages;
    private HotelDto hotel;
    private List<HotelImageResponseDto> hotelImages;
}
