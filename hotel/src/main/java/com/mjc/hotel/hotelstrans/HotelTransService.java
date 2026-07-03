package com.mjc.hotel.hotelstrans;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class HotelTransService {
    @Autowired
    private HotelTransRepository hotelTransRepository;

    public List<HotelTransDto> findAll() {

        List<HotelTransEntity> list = hotelTransRepository.findAll();

        return getListHotelTransDto(list);
    }

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
    public HotelTransDto update(Long transId, IHotelTrans dto) {

        HotelTransDto findDto = this.findById(transId);

        findDto.copyMembers(dto, false);

        HotelTransEntity entity =
                (HotelTransEntity) new HotelTransEntity().copyMembers(findDto, true);

        HotelTransEntity updated = hotelTransRepository.save(entity);

        return (HotelTransDto) new HotelTransDto().copyMembers(updated, true);
    }

    public void deleteById(Long transId) {

        if (!hotelTransRepository.existsById(transId)) {
            throw new IllegalArgumentException("호텔 교통편을 찾을 수 없습니다.");
        }

        hotelTransRepository.deleteById(transId);
    }
}
