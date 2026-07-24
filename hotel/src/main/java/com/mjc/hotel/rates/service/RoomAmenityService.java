package com.mjc.hotel.rates.service;

import com.mjc.hotel.rates.dto.request.RoomAmenityRequestDto;
import com.mjc.hotel.rates.dto.response.RoomAmenityResponseDto;
import com.mjc.hotel.rates.entity.RoomAmenityEntity;
import com.mjc.hotel.rates.repository.RoomAmenityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RoomAmenityService {

    private final RoomAmenityRepository roomAmenityRepository;

    /**
     * 호실 편의시설 저장 또는 수정 (Upsert)
     */
    @Transactional
    public RoomAmenityEntity saveOrUpdateAmenity(Long roomId, RoomAmenityRequestDto requestDto) {
        if (roomId == null) {
            return null;
        }

        Optional<RoomAmenityEntity> existingOpt = roomAmenityRepository.findByRoomId(roomId);
        RoomAmenityEntity amenity = existingOpt.orElseGet(() -> RoomAmenityEntity.builder()
                .roomId(roomId)
                .wifi(false)
                .tv(false)
                .bathtub(false)
                .cityView(false)
                .oceanView(false)
                .breakfastIncluded(false)
                .nonSmoking(false)
                .build());

        if (requestDto != null) {
            if (requestDto.getWifi() != null) amenity.setWifi(requestDto.getWifi());
            if (requestDto.getTv() != null) amenity.setTv(requestDto.getTv());
            if (requestDto.getBathtub() != null) amenity.setBathtub(requestDto.getBathtub());
            if (requestDto.getCityView() != null) amenity.setCityView(requestDto.getCityView());
            if (requestDto.getOceanView() != null) amenity.setOceanView(requestDto.getOceanView());
            if (requestDto.getBreakfastIncluded() != null) amenity.setBreakfastIncluded(requestDto.getBreakfastIncluded());
            if (requestDto.getNonSmoking() != null) amenity.setNonSmoking(requestDto.getNonSmoking());
        }

        return roomAmenityRepository.save(amenity);
    }

    /**
     * 호실 ID로 편의시설 응답 DTO 조회
     */
    public RoomAmenityResponseDto getAmenityResponseByRoomId(Long roomId) {
        if (roomId == null) {
            return null;
        }
        return roomAmenityRepository.findByRoomId(roomId)
                .map(this::convertToResponseDto)
                .orElse(null);
    }

    /**
     * Entity -> Response DTO 변환
     */
    public RoomAmenityResponseDto convertToResponseDto(RoomAmenityEntity entity) {
        if (entity == null) return null;
        return RoomAmenityResponseDto.builder()
                .roomAmenityId(entity.getRoomAmenityId())
                .wifi(entity.getWifi())
                .tv(entity.getTv())
                .bathtub(entity.getBathtub())
                .cityView(entity.getCityView())
                .oceanView(entity.getOceanView())
                .breakfastIncluded(entity.getBreakfastIncluded())
                .nonSmoking(entity.getNonSmoking())
                .build();
    }

    /**
     * 호실 삭제 시 편의시설 정보 삭제
     */
    @Transactional
    public void deleteByRoomId(Long roomId) {
        if (roomId != null) {
            roomAmenityRepository.deleteByRoomId(roomId);
        }
    }
}
