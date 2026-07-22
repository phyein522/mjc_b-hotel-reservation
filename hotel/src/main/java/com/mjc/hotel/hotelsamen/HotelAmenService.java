package com.mjc.hotel.hotelsamen;

import com.mjc.hotel.hotels.HotelEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class HotelAmenService {
    @Autowired
    private HotelAmenRepository hotelAmenRepository;

    // Entity 목록을 응답용 DTO 목록으로 변환한다.
    private List<HotelAmenDto> getListHotelAmenDto(List<HotelAmenEntity> list) {
        return list.stream()
                .map(x -> (HotelAmenDto) new HotelAmenDto().copyMembers(x, true))
                .toList();
    }

    // 편의시설 ID로 호텔 편의시설 정보를 조회한다.
    public HotelAmenDto findById(Long amenId) {
        HotelAmenEntity entity = hotelAmenRepository.findById(amenId)
                .orElseThrow(() -> new IllegalArgumentException("호텔 편의시설을 찾을 수 없습니다."));
        return (HotelAmenDto) new HotelAmenDto().copyMembers(entity, true);
    }

    // 호텔 편의시설 정보를 등록한다.
    public HotelAmenDto insert(IHotelAmen dto) {
        HotelAmenEntity entity = (HotelAmenEntity) new HotelAmenEntity().copyMembers(dto, true);
        entity.setAmenId(null);
        HotelAmenEntity inserted = hotelAmenRepository.save(entity);
        return (HotelAmenDto) new HotelAmenDto().copyMembers(inserted, true);
    }

    // 기존 호텔 편의시설 정보를 수정한다.
    public HotelAmenDto update(IHotelAmen dto) {
        HotelAmenDto findDto = this.findById(dto.getAmenId());
        findDto.copyMembers(dto, false);
        HotelAmenEntity entity =
                (HotelAmenEntity) new HotelAmenEntity().copyMembers(findDto, true);
        HotelAmenEntity updated = hotelAmenRepository.save(entity);
        return (HotelAmenDto) new HotelAmenDto().copyMembers(updated, true);
    }

    // 편의시설 ID로 삭제하고, 삭제 전 정보를 반환한다.
    public HotelAmenDto deleteById(Long amenId) {
        HotelAmenDto findDto = this.findById(amenId);
        hotelAmenRepository.deleteById(amenId);
        return findDto;
    }

    // 호텔 ID에 속한 편의시설 목록을 페이지 단위로 조회한다.
    public Page<HotelAmenDto> findAllByHotelIdEquals(Long hotelId, Pageable pageable) {
        HotelEntity hotel = HotelEntity.builder().hotelId(hotelId).build();
        Page<HotelAmenEntity> page = this.hotelAmenRepository.findAllByHotelEquals(hotel, pageable);
        List<HotelAmenDto> list = this.getListHotelAmenDto(page.getContent());
        return new PageImpl<>(list, pageable, page.getTotalElements());
    }
}
