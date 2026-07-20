package com.mjc.hotel.hotelstrans;

import com.mjc.hotel.hotels.HotelEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class HotelTransService {
    @Autowired
    private HotelTransRepository hotelTransRepository;

    // Entity 목록을 응답용 DTO 목록으로 변환한다.
    private List<HotelTransDto> getListHotelTransDto(List<HotelTransEntity> list) {

        return list.stream()
                .map(x -> (HotelTransDto) new HotelTransDto().copyMembers(x, true))
                .toList();
    }

    // 교통편 ID로 호텔 교통편 정보를 조회한다.
    public HotelTransDto findById(Long transId) {

        HotelTransEntity entity = hotelTransRepository.findById(transId)
                .orElseThrow(() -> new IllegalArgumentException("호텔 교통편을 찾을 수 없습니다."));

        return (HotelTransDto) new HotelTransDto().copyMembers(entity, true);
    }

    // 호텔 교통편 정보를 등록한다.
    public HotelTransDto insert(IHotelTrans dto) {

        HotelTransEntity entity =
                (HotelTransEntity) new HotelTransEntity().copyMembers(dto, true);

        entity.setTransId(null);

        HotelTransEntity inserted = hotelTransRepository.save(entity);

        return (HotelTransDto) new HotelTransDto().copyMembers(inserted, true);
    }

    // 기존 호텔 교통편 정보를 수정한다.
    public HotelTransDto update(IHotelTrans dto) {

        HotelTransDto findDto = this.findById(dto.getTransId());

        findDto.copyMembers(dto, false);

        HotelTransEntity entity =
                (HotelTransEntity) new HotelTransEntity().copyMembers(findDto, true);

        HotelTransEntity updated = hotelTransRepository.save(entity);

        return (HotelTransDto) new HotelTransDto().copyMembers(updated, true);
    }

    // 교통편 ID로 삭제하고, 삭제 전 정보를 반환한다.
    public HotelTransDto deleteById(Long transId) {

        HotelTransDto findDto = this.findById(transId);
        hotelTransRepository.deleteById(transId);
        return findDto;
    }

    // 호텔 ID에 속한 교통편 목록을 페이지 단위로 조회한다.
    public Page<HotelTransDto> findAllByHotelIdEquals(Long hotelId, Pageable pageable) {

        HotelEntity hotel = HotelEntity.builder().hotelId(hotelId).build();
        Page<HotelTransEntity> page = this.hotelTransRepository.findAllByHotelEquals(hotel, pageable);
        List<HotelTransDto> list = this.getListHotelTransDto(page.getContent());
        return new PageImpl<>(list, pageable, page.getTotalElements());
    }
}
