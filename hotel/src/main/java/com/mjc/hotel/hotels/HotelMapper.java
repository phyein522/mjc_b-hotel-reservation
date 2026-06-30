package com.mjc.hotel.hotels;

import org.springframework.stereotype.Component;

@Component
public class HotelMapper {

    public HotelDto toDto(HotelEntity entity) {
        if (entity == null) {
            return null;
        }

        return HotelDto.builder()
                .hotelId(entity.getHotelId())
                .hotelName(entity.getHotelName())
                .hotelDescription(entity.getHotelDescription())
                .hotelAddress(entity.getHotelAddress())
                .hotelCity(entity.getHotelCity())
                .hotelZipCode(entity.getHotelZipCode())
                .hotelPhone(entity.getHotelPhone())
                .hotelEmail(entity.getHotelEmail())
                .hotelCheckInTime(entity.getHotelCheckInTime())
                .hotelCheckOutTime(entity.getHotelCheckOutTime())
                .hotelStarRating(entity.getHotelStarRating())
                .hotelIsActive(entity.getHotelIsActive())
                .latitude(entity.getLatitude())
                .hardness(entity.getHardness())
                .hotelType(entity.getHotelType())
                .build();
    }

    public HotelEntity toEntity(HotelDto dto) {
        if (dto == null) {
            return null;
        }

        return HotelEntity.builder()
                .hotelId(dto.getHotelId())
                .hotelName(dto.getHotelName())
                .hotelDescription(dto.getHotelDescription())
                .hotelAddress(dto.getHotelAddress())
                .hotelCity(dto.getHotelCity())
                .hotelZipCode(dto.getHotelZipCode())
                .hotelPhone(dto.getHotelPhone())
                .hotelEmail(dto.getHotelEmail())
                .hotelCheckInTime(dto.getHotelCheckInTime())
                .hotelCheckOutTime(dto.getHotelCheckOutTime())
                .hotelStarRating(dto.getHotelStarRating())
                .hotelIsActive(dto.getHotelIsActive())
                .latitude(dto.getLatitude())
                .hardness(dto.getHardness())
                .hotelType(dto.getHotelType())
                .build();
    }
}
