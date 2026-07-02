package com.mjc.hotel.hotels;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class HotelService {
    @Autowired
    private HotelRepository hotelRepository;

    public List<HotelDto> findAll() {
        List<HotelEntity> list = hotelRepository.findAll();
        return getListHotelDto(list);
    }

    private List<HotelDto> getListHotelDto(List<HotelEntity> list) {
        return list.stream()
                .map(x -> (HotelDto) new HotelDto().copyMembers(x, true))
                .toList();
    }

    public HotelDto findById(Long hotelId) {
        HotelEntity hotel = hotelRepository.findById(hotelId)
                .orElseThrow(() -> new IllegalArgumentException("호텔을 찾을 수 없습니다."));

        return (HotelDto) new HotelDto().copyMembers(hotel, true);
    }

    public HotelDto insert(IHotel insertDto) {

        if (insertDto.getIsActive() == null) {
            insertDto.setIsActive(true);
        }

        HotelEntity insertEntity =
                (HotelEntity) new HotelEntity().copyMembers(insertDto, true);

        insertEntity.setHotelId(null);

        HotelEntity insertedEntity =
                hotelRepository.save(insertEntity);

        return (HotelDto) new HotelDto().copyMembers(insertedEntity, true);
    }

    public HotelDto update(Long hotelId, IHotel hotelDto) {

        HotelDto findDto = this.findById(hotelId);

        findDto.copyMembers(hotelDto, false);

        HotelEntity updateEntity =
                (HotelEntity) new HotelEntity().copyMembers(findDto, true);

        HotelEntity updatedEntity =
                hotelRepository.save(updateEntity);

        return (HotelDto) new HotelDto().copyMembers(updatedEntity, true);
    }

    public void deleteByID(Long hotelId) {

        if (!hotelRepository.existsById(hotelId)) {
            throw new IllegalArgumentException("호텔을 찾을 수 없습니다.");
        }

        hotelRepository.deleteById(hotelId);
    }
}
