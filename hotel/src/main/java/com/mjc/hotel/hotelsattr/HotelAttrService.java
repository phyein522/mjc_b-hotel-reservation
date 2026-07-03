package com.mjc.hotel.hotelsattr;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HotelAttrService {
    @Autowired
    private HotelAttrRepository hotelAttrRepository;

    public List<HotelAttrDto> findAll() {

        List<HotelAttrEntity> list = hotelAttrRepository.findAll();

        return getListHotelAttrDto(list);
    }

    private List<HotelAttrDto> getListHotelAttrDto(List<HotelAttrEntity> list) {

        return list.stream()
                .map(x -> (HotelAttrDto) new HotelAttrDto().copyMembers(x, true))
                .toList();
    }
    public HotelAttrDto findById(Long attrId) {

        HotelAttrEntity entity = hotelAttrRepository.findById(attrId)
                .orElseThrow(() -> new IllegalArgumentException("호텔 매력사항을 찾을 수 없습니다."));

        return (HotelAttrDto) new HotelAttrDto().copyMembers(entity, true);
    }
    public HotelAttrDto insert(IHotelAttr dto) {

        HotelAttrEntity entity =
                (HotelAttrEntity) new HotelAttrEntity().copyMembers(dto, true);

        entity.setAttrId(null);

        HotelAttrEntity inserted = hotelAttrRepository.save(entity);

        return (HotelAttrDto) new HotelAttrDto().copyMembers(inserted, true);
    }

    public HotelAttrDto update(Long attrId, IHotelAttr dto) {

        HotelAttrDto findDto = this.findById(attrId);

        findDto.copyMembers(dto, false);

        HotelAttrEntity entity =
                (HotelAttrEntity) new HotelAttrEntity().copyMembers(findDto, true);

        HotelAttrEntity updated = hotelAttrRepository.save(entity);

        return (HotelAttrDto) new HotelAttrDto().copyMembers(updated, true);
    }
    public void deleteById(Long attrId) {

        if (!hotelAttrRepository.existsById(attrId)) {
            throw new IllegalArgumentException("호텔 매력사항을 찾을 수 없습니다.");
        }

        hotelAttrRepository.deleteById(attrId);
    }
}
