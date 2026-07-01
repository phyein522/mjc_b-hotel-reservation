package com.mjc.hotel.hotelsattr;

import com.mjc.hotel.hotels.HotelEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class HotelAttrMapper {
    private final HotelAttrMapper hotelAttrMapper;

    public HotelAttrDto toDto(HotelAttrEntity entity) {

        if (entity == null) {
            return null;
        }

        HotelAttrDto dto = HotelAttrDto.builder()
                .attrId(entity.getAttrId())
                .context(entity.getContext())
                .build();

        if (entity.getHotel() != null) {
            dto.setHotelId(entity.getHotel().getHotelId());
            dto.setHotel(hotelMapper.toDto(entity.getHotel()));
        }

        return dto;
    }
    public HotelAttrEntity toEntity(HotelAttrDto dto) {

        if (dto == null) {
            return null;
        }

        HotelAttrEntity entity = HotelAttrEntity.builder()
                .attrId(dto.getAttrId())
                .context(dto.getContext())
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
