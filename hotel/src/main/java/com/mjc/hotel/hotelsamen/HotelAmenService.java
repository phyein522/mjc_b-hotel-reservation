package com.mjc.hotel.hotelsamen;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HotelAmenService {
    @Autowired
    private HotelAmenRepository hotelAmenRepository;

    public List<HotelAmenDto> findAll() {

        List<HotelAmenEntity> list = hotelAmenRepository.findAll();

        return getListHotelAmenDto(list);
    }

    private List<HotelAmenDto> getListHotelAmenDto(List<HotelAmenEntity> list) {

        return list.stream()
                .map(x -> (HotelAmenDto) new HotelAmenDto().copyMembers(x, true))
                .toList();
    }
    public HotelAmenDto findById(Long amenId) {

        HotelAmenEntity entity = hotelAmenRepository.findById(amenId)
                .orElseThrow(() -> new IllegalArgumentException("호텔 편의시설을 찾을 수 없습니다."));

        return (HotelAmenDto) new HotelAmenDto().copyMembers(entity, true);
    }
    public HotelAmenDto insert(IHotelAmen dto) {

        HotelAmenEntity entity =
                (HotelAmenEntity) new HotelAmenEntity().copyMembers(dto, true);

        entity.setAmenId(null);

        HotelAmenEntity inserted = hotelAmenRepository.save(entity);

        return (HotelAmenDto) new HotelAmenDto().copyMembers(inserted, true);
    }

    public HotelAmenDto update(Long amenId, IHotelAmen dto) {

        HotelAmenDto findDto = this.findById(amenId);

        findDto.copyMembers(dto, false);

        HotelAmenEntity entity =
                (HotelAmenEntity) new HotelAmenEntity().copyMembers(findDto, true);

        HotelAmenEntity updated = hotelAmenRepository.save(entity);

        return (HotelAmenDto) new HotelAmenDto().copyMembers(updated, true);
    }
    public void deleteById(Long amenId) {

        if (!hotelAmenRepository.existsById(amenId)) {
            throw new IllegalArgumentException("호텔 편의시설을 찾을 수 없습니다.");
        }

        hotelAmenRepository.deleteById(amenId);
    }
}
