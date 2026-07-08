package com.mjc.hotel.bookings;

import com.mjc.hotel.hotels.HotelDto;
import com.mjc.hotel.hotelsimage.HotelImageResponseDto;
import com.mjc.hotel.room_images.dto.RoomImageResponseDto;
import com.mjc.hotel.rooms.dto.RoomDto;
import com.mjc.hotel.user.dto.UserDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface BookingMapper {
    UserDto getUser(@Param("userId") Long userId);
    List<BookingDto> getBookingsByUserId(@Param("userId") Long userId);
    RoomDto getRoom(@Param("roomId") Long roomId);
    List<RoomImageResponseDto> getRoomImages(@Param("roomId") Long roomId);
    HotelDto getHotel(@Param("hotelId") Long hotelId);
    HotelDto getHotelByRoomId(@Param("roomId") Long roomId);
    List<HotelImageResponseDto> getHotelImages(@Param("hotelId") Long hotelId);
}
