package com.mjc.hotel.hotelstrans;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class HotelTransService {
    private final HotelTransRepository hotelTransRepository;
    private final HotelTransMapper hotelTransMapper;

    public List<HotelTransDto> findAll() {
        return hotelTransRepository.findAll()
                .stream()
                .map(hotelTransMapper::toDto)
                .toList();
    }
    public HotelTransDto findById(Long transId) {
        HotelTransEntity hotelTrans = hotelTransRepository.findById(transId).orElseThrow( () -> new IllegalArgumentException("호텔 이미지를 찾을 수 없습니다."));
        return hotelTransMapper.toDto(hotelTrans);
    }
    public HotelTransDto insert(HotelTransDto insertDto) {

        HotelTransEntity savedHotelTrans = hotelTransRepository.save(hotelTransMapper.toEntity(insertDto));

        return hotelTransMapper.toDto(savedHotelTrans);
    }
    public HotelTransDto update(Long transId, HotelTransDto hotelTransDto) {

        HotelTransEntity hotelTrans = hotelTransRepository.findById(transId)
                .orElseThrow(() -> new IllegalArgumentException("호텔 이미지를 찾을 수 없습니다."));

        hotelTrans.setName(hotelTransDto.getName());
        hotelTrans.setTime(hotelTransDto.getTime());
        hotelTrans.setDepart(hotelTransDto.getDepart());

        HotelTransEntity savedHotelTrans = hotelTransRepository.save(hotelTrans);

        return hotelTransMapper.toDto(savedHotelTrans);
    }
    public void deleteById(Long transId) {

        if (!hotelTransRepository.existsById(transId)) {
            throw new IllegalArgumentException("호텔 이미지를 찾을 수 없습니다.");
        }

        hotelTransRepository.deleteById(transId);
    }
}
