package com.mjc.hotel.bookings;

import com.mjc.hotel.hotels.HotelDto;
import com.mjc.hotel.rooms.dto.RoomDto;
import org.apache.ibatis.annotations.Param;
import java.util.List;

public interface BookingMapper {
    public List<BookingDto> getBookings(@Param("userId") Long userId);
    public RoomDto getRoom(@Param("roomId") Long roomId);
    public HotelDto getHotel(@Param("hotelId") Long hotelId);
    public HotelDto getHotelByRoomId(@Param("roomId") Long roomId);
}
