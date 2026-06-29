package com.mjc.hotel.sales_analysis.dto;

import com.mjc.hotel.sales_analysis.entity.Hotel;
import lombok.Getter;

@Getter
public class HotelResponseDto {

    private Long hotelId;
    private String hotelName;
    private String city;

    // Entity → DTO 변환 (정적 팩토리 메서드)
    public static HotelResponseDto from(Hotel hotel) {
        HotelResponseDto dto = new HotelResponseDto();
        dto.hotelId = hotel.getHotelId();
        dto.hotelName = hotel.getHotelName();
        dto.city = hotel.getCity();
        return dto;
    }
}
