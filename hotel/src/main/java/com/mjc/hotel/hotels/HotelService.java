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

        if (insertDto.getHotelIsActive() == null) {
            insertDto.setHotelIsActive(true);
        }

        HotelEntity savedHotel = hotelRepository.save(hotelMapper.toEntity(insertDto));

        return hotelMapper.toDto(savedHotel);
    }

    @Transactional
    public HotelDto update(Long hotelId, HotelDto hotelDto) {
        log.debug("update hotelId = {}, hotelDto = {}", hotelId, hotelDto);

        HotelEntity hotel = hotelRepository.findById(hotelId)
                .orElseThrow(() -> new IllegalArgumentException("호텔을 찾을 수 없습니다."));

        hotel.setHotelName(hotelDto.getHotelName());
        hotel.setHotelDescription(hotelDto.getHotelDescription());
        hotel.setHotelAddress(hotelDto.getHotelAddress());
        hotel.setHotelCity(hotelDto.getHotelCity());
        hotel.setHotelZipCode(hotelDto.getHotelZipCode());
        hotel.setHotelPhone(hotelDto.getHotelPhone());
        hotel.setHotelEmail(hotelDto.getHotelEmail());
        hotel.setHotelCheckInTime(hotelDto.getHotelCheckInTime());
        hotel.setHotelCheckOutTime(hotelDto.getHotelCheckOutTime());
        hotel.setHotelStarRating(hotelDto.getHotelStarRating());
        hotel.setHotelIsActive(hotelDto.getHotelIsActive());
        hotel.setLatitude(hotelDto.getLatitude());
        hotel.setHardness(hotelDto.getHardness());
        hotel.setHotelType(hotelDto.getHotelType());

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
