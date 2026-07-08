package com.mjc.hotel.bookings;

import com.mjc.hotel.hotels.HotelDto;
import com.mjc.hotel.hotelsimage.HotelImageDto;
import com.mjc.hotel.room_images.dto.RoomImageDto;
import com.mjc.hotel.room_images.dto.RoomImageResponseDto;
import com.mjc.hotel.rooms.dto.RoomDto;
import com.mjc.hotel.user.dto.UserDto;
import org.apache.ibatis.annotations.Param;
import java.util.List;

public interface BookingMapper {
    UserDto getUser(@Param("userId") Long userId);
    List<BookingDto> getBookingsByUserId(@Param("userId") Long userId);
    RoomDto getRoom(@Param("roomId") Long roomId);
    List<RoomImageDto> getRoomImages(@Param("roomId") Long roomId);
    RoomImageResponseDto getRoomImageUrl(@Param("roomImageId") Long roomImageId);
    HotelDto getHotel(@Param("hotelId") Long hotelId);
    HotelDto getHotelByRoomId(@Param("roomId") Long roomId);
    List<HotelImageDto> getHotelImages(@Param("hotelId") Long hotelId);
}
