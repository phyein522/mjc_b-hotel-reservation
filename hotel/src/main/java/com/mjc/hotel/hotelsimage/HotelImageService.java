package com.mjc.hotel.hotelsimage;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class HotelImageService {
    private final HotelImageRepository hotelImageRepository;
    private final HotelImageMapper hotelImageMapper;

    public List<HotelImageDto> findAll() {
        return hotelImageRepository.findAll()
                .stream()
                .map(hotelImageMapper::toDto)
                .toList();
    }
    public HotelImageDto findById(Long hotelImageId) {
        HotelImageEntity hotelImage = hotelImageRepository.findById(hotelImageId).orElseThrow( () -> new IllegalArgumentException("호텔 이미지를 찾을 수 없습니다."));
        return hotelImageMapper.toDto(hotelImage);
    }

    public HotelImageDto insert(HotelImageDto insertDto) {
        LocalDateTime now = LocalDateTime.now();

        if(insertDto.getCreatedAt() == null) {
            insertDto.setCreatedAt(now);
        }
        HotelImageEntity savedHotelImage =
                hotelImageRepository.save(hotelImageMapper.toEntity(insertDto));

        return hotelImageMapper.toDto(savedHotelImage);
    }

    public HotelImageDto update(Long hotelImageId, HotelImageDto hotelImageDto) {

        HotelImageEntity hotelImage = hotelImageRepository.findById(hotelImageId)
                .orElseThrow(() -> new IllegalArgumentException("호텔 이미지를 찾을 수 없습니다."));

        hotelImage.setHotelId(hotelImageDto.getHotelId());
        hotelImage.setImageUrl(hotelImageDto.getImageUrl());
        hotelImage.setSortOrder(hotelImageDto.getSortOrder());
        hotelImage.setIsThumbnail(hotelImageDto.getIsThumbnail());

        HotelImageEntity savedHotelImage = hotelImageRepository.save(hotelImage);

        return hotelImageMapper.toDto(savedHotelImage);
    }
    public void deleteById(Long hotelImageId) {

        if (!hotelImageRepository.existsById(hotelImageId)) {
            throw new IllegalArgumentException("호텔 이미지를 찾을 수 없습니다.");
        }

        hotelImageRepository.deleteById(hotelImageId);
    }
}