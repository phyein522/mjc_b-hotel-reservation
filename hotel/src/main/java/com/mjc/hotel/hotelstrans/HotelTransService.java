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

    private List<HotelTransDto> getListHotelTransDto(List<HotelTransEntity> list) {

        return list.stream()
                .map(x -> (HotelTransDto) new HotelTransDto().copyMembers(x, true))
                .toList();
    }
    public HotelTransDto findById(Long transId) {

        HotelTransEntity entity = hotelTransRepository.findById(transId)
                .orElseThrow(() -> new IllegalArgumentException("호텔 교통편을 찾을 수 없습니다."));

        return (HotelTransDto) new HotelTransDto().copyMembers(entity, true);
    }

    public HotelTransDto insert(IHotelTrans dto) {

        HotelTransEntity entity =
                (HotelTransEntity) new HotelTransEntity().copyMembers(dto, true);

        entity.setTransId(null);

        HotelTransEntity inserted = hotelTransRepository.save(entity);

        return (HotelTransDto) new HotelTransDto().copyMembers(inserted, true);
    }
    public HotelTransDto update(IHotelTrans dto) {

        HotelTransDto findDto = this.findById(dto.getTransId());

        findDto.copyMembers(dto, false);

        HotelTransEntity entity =
                (HotelTransEntity) new HotelTransEntity().copyMembers(findDto, true);

        HotelTransEntity updated = hotelTransRepository.save(entity);

        return (HotelTransDto) new HotelTransDto().copyMembers(updated, true);
    }

    public HotelTransDto deleteById(Long transId) {

        HotelTransDto findDto = this.findById(transId);
        hotelTransRepository.deleteById(transId);
        return findDto;
    }

    public Page<HotelTransDto> findAllByHotelIdEquals(Long hotelId, Pageable pageable) {

        HotelEntity hotel = HotelEntity.builder().hotelId(hotelId).build();
        Page<HotelTransEntity> page = this.hotelTransRepository.findAllByHotelEquals(hotel, pageable);
        List<HotelTransDto> list = this.getListHotelTransDto(page.getContent());
        return new PageImpl<>(list, pageable, page.getTotalElements());
    }
}
