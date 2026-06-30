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
                .name(entity.getName())
                .description(entity.getDescription())
                .address(entity.getAddress())
                .city(entity.getCity())
                .zipCode(entity.getZipCode())
                .phone(entity.getPhone())
                .email(entity.getEmail())
                .checkIn(entity.getCheckIn())
                .checkOut(entity.getCheckOut())
                .starRate(entity.getStarRate())
                .isActive(entity.getIsActive())
                .latitude(entity.getLatitude())
                .longitude(entity.getLongitude())
                .type(entity.getType())
                .build();
    }

    public HotelEntity toEntity(HotelDto dto) {
        if (dto == null) {
            return null;
        }

        return HotelEntity.builder()
                .hotelId(dto.getHotelId())
                .name(dto.getName())
                .description(dto.getDescription())
                .address(dto.getAddress())
                .city(dto.getCity())
                .zipCode(dto.getZipCode())
                .phone(dto.getPhone())
                .email(dto.getEmail())
                .checkIn(dto.getCheckIn())
                .checkOut(dto.getCheckOut())
                .starRate(dto.getStarRate())
                .isActive(dto.getIsActive())
                .latitude(dto.getLatitude())
                .longitude(dto.getLongitude())
                .type(dto.getType())
                .build();
    }
}
