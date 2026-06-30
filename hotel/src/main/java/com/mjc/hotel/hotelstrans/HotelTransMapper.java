package com.mjc.hotel.hotelstrans;

import com.mjc.hotel.hotels.HotelEntity;
import com.mjc.hotel.hotels.HotelMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class HotelTransMapper {

    private final HotelMapper hotelMapper;

    public HotelTransDto toDto(HotelTransEntity entity) {

        if (entity == null) {
            return null;
        }

        HotelTransDto dto = HotelTransDto.builder()
                .transId(entity.getTransId())
                .name(entity.getName())
                .time(entity.getTime())
                .depart(entity.getDepart())
                .build();

        if (entity.getHotel() != null) {
            dto.setHotelId(entity.getHotel().getHotelId());
            dto.setHotel(hotelMapper.toDto(entity.getHotel()));
        }

        return dto;
    }

    public HotelTransEntity toEntity(HotelTransDto dto) {

        if (dto == null) {
            return null;
        }

        HotelTransEntity entity = HotelTransEntity.builder()
                .transId(dto.getTransId())
                .name(dto.getName())
                .time(dto.getTime())
                .depart(dto.getDepart())
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