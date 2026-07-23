package com.mjc.hotel.rates.service;

import com.mjc.hotel.rates.entity.RoomDetailEntity;
import com.mjc.hotel.rates.repository.RoomDetailRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RoomDetailService {

    private final RoomDetailRepository roomDetailRepository;

    /**
     * 상세 설명 저장 또는 수정 (Upsert)
     */
    @Transactional
    public RoomDetailEntity saveOrUpdateDescription(Long roomId, String description) {
        if (roomId == null) {
            return null;
        }

        Optional<RoomDetailEntity> existingOpt = roomDetailRepository.findByRoomId(roomId);
        if (existingOpt.isPresent()) {
            RoomDetailEntity detail = existingOpt.get();
            detail.setDescription(description);
            return roomDetailRepository.save(detail);
        } else {
            RoomDetailEntity newDetail = RoomDetailEntity.builder()
                    .roomId(roomId)
                    .description(description)
                    .build();
            return roomDetailRepository.save(newDetail);
        }
    }

    /**
     * 호실 ID로 상세 설명 조회
     */
    public String getDescriptionByRoomId(Long roomId) {
        if (roomId == null) {
            return null;
        }
        return roomDetailRepository.findByRoomId(roomId)
                .map(RoomDetailEntity::getDescription)
                .orElse(null);
    }

    /**
     * 호실 ID로 상세 설명 엔티티 조회
     */
    public Optional<RoomDetailEntity> findByRoomId(Long roomId) {
        if (roomId == null) {
            return Optional.empty();
        }
        return roomDetailRepository.findByRoomId(roomId);
    }

    /**
     * 호실 삭제 시 상세 설명 삭제
     */
    @Transactional
    public void deleteByRoomId(Long roomId) {
        if (roomId != null) {
            roomDetailRepository.deleteByRoomId(roomId);
        }
    }
}
