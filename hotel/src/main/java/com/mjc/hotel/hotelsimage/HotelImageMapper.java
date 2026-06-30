package com.mjc.hotel.hotelsimage;

import com.mjc.hotel.hotels.HotelEntity;
import com.mjc.hotel.hotels.HotelMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class HotelImageMapper {

    private final HotelMapper hotelMapper;

    public HotelImageDto toDto(HotelImageEntity entity) {

        if (entity == null) {
            return null;
        }

        HotelImageDto dto = HotelImageDto.builder()
                .imageId(entity.getImageId())
                .url(entity.getUrl())
                .sortOrder(entity.getSortOrder())
                .isThumbnail(entity.getIsThumbnail())
                .build();

        if (entity.getHotel() != null) {
            dto.setHotelId(entity.getHotel().getHotelId());
            dto.setHotel(hotelMapper.toDto(entity.getHotel()));
        }

        return dto;
    }

    public HotelImageEntity toEntity(HotelImageDto dto) {

        if (dto == null) {
            return null;
        }

        HotelImageEntity entity = HotelImageEntity.builder()
                .imageId(dto.getImageId())
                .url(dto.getUrl())
                .sortOrder(dto.getSortOrder())
                .isThumbnail(dto.getIsThumbnail())
                .build();

        if (dto.getHotelId() != null) {
            HotelEntity hotel = new HotelEntity();
            hotel.setHotelId(dto.getHotelId());

            entity.setHotel(hotel);
            entity.setHotelId(dto.getHotelId());
        }

        return entity;
    }
}