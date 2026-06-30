package com.mjc.hotel.hotels;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class HotelService {
    private final HotelRepository hotelRepository;
    private final HotelMapper hotelMapper;

    public List<HotelDto> findAll() {
        return hotelRepository.findAll()
                .stream()
                .map(hotelMapper::toDto)
                .toList();
    }

    public HotelDto findById(Long hotelId) {
        HotelEntity hotel = hotelRepository.findById(hotelId)
                .orElseThrow(() -> new IllegalArgumentException("호텔을 찾을 수 없습니다."));
        return hotelMapper.toDto(hotel);
    }

    @Transactional
    public HotelDto insert(HotelDto insertDto) {
        LocalDateTime now = LocalDateTime.now();

        if (insertDto.getIsActive() == null) {
            insertDto.setIsActive(true);
        }

        HotelEntity savedHotel = hotelRepository.save(hotelMapper.toEntity(insertDto));

        return hotelMapper.toDto(savedHotel);
    }

    @Transactional
    public HotelDto update(Long hotelId, HotelDto hotelDto) {
        log.debug("update hotelId = {}, hotelDto = {}", hotelId, hotelDto);

        HotelEntity hotel = hotelRepository.findById(hotelId)
                .orElseThrow(() -> new IllegalArgumentException("호텔을 찾을 수 없습니다."));

        hotel.setName(hotelDto.getName());
        hotel.setDescription(hotelDto.getDescription());
        hotel.setAddress(hotelDto.getAddress());
        hotel.setCity(hotelDto.getCity());
        hotel.setZipCode(hotelDto.getZipCode());
        hotel.setPhone(hotelDto.getPhone());
        hotel.setEmail(hotelDto.getEmail());
        hotel.setCheckIn(hotelDto.getCheckIn());
        hotel.setCheckOut(hotelDto.getCheckOut());
        hotel.setStarRate(hotelDto.getStarRate());
        hotel.setIsActive(hotelDto.getIsActive());
        hotel.setLatitude(hotelDto.getLatitude());
        hotel.setLongitude(hotelDto.getLongitude());
        hotel.setType(hotelDto.getType());

        return hotelMapper.toDto(hotel);
    }

    @Transactional
    public void deleteByID(Long hotelId) {
        if (!hotelRepository.existsById(hotelId)) {
            throw new IllegalArgumentException("호텔을 찾을 수 없습니다.");
        }

        hotelRepository.deleteById(hotelId);
    }
}
