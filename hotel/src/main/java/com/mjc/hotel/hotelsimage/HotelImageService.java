package com.mjc.hotel.hotelsimage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HotelImageService {
    @Autowired
    private HotelImageRepository hotelImageRepository;

    public List<HotelImageDto> findAll() {

        List<HotelImageEntity> list = hotelImageRepository.findAll();

        return getListHotelImageDto(list);
    }

    private List<HotelImageDto> getListHotelImageDto(List<HotelImageEntity> list) {

        return list.stream()
                .map(x -> (HotelImageDto) new HotelImageDto().copyMembers(x, true))
                .toList();
    }
    public HotelImageDto findById(Long imageId) {

        HotelImageEntity findEntity =
                hotelImageRepository.findById(imageId)
                        .orElseThrow(() -> new IllegalArgumentException("호텔 이미지를 찾을 수 없습니다."));

        return (HotelImageDto) new HotelImageDto().copyMembers(findEntity, true);
    }
    public HotelImageDto insert(IHotelImage hotelImageDto) {

        HotelImageEntity insertEntity =
                (HotelImageEntity) new HotelImageEntity().copyMembers(hotelImageDto, true);

        insertEntity.setImageId(null);

        HotelImageEntity insertedEntity =
                hotelImageRepository.save(insertEntity);

        return (HotelImageDto) new HotelImageDto().copyMembers(insertedEntity, true);
    }
    public HotelImageDto update(Long imageId, IHotelImage hotelImageDto) {

        HotelImageDto findDto = this.findById(imageId);

        findDto.copyMembers(hotelImageDto, false);

        HotelImageEntity updateEntity =
                (HotelImageEntity) new HotelImageEntity().copyMembers(findDto, true);

        HotelImageEntity updatedEntity =
                hotelImageRepository.save(updateEntity);

        return (HotelImageDto) new HotelImageDto().copyMembers(updatedEntity, true);
    }
    public void deleteById(Long imageId) {

        if (!hotelImageRepository.existsById(imageId)) {
            throw new IllegalArgumentException("호텔 이미지를 찾을 수 없습니다.");
        }

        hotelImageRepository.deleteById(imageId);
    }
}