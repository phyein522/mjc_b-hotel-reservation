package com.mjc.hotel.bookings;

import com.mjc.hotel.hotelsimage.HotelImageResponseDto;
import com.mjc.hotel.room_images.dto.RoomImageResponseDto;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class BookingResponseDto {
    private BookingDto booking;
    private List<RoomImageResponseDto> roomImages;
    private List<HotelImageResponseDto> hotelImages;
}
