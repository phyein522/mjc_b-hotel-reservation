package com.mjc.hotel.hotelstrans;

import com.mjc.hotel.hotelsimage.HotelImageDto;
import com.mjc.hotel.hotelsimage.HotelImageMapper;
import com.mjc.hotel.hotelsimage.HotelImageRepository;
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

    public List<HotelImageDto> findAll() {
        return hotelTransRepository.findAll()
                .stream()
                .map(hotelTransMapper::toDto)
                .toList();
    }
}
