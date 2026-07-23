package com.mjc.hotel.rates.repository;

import com.mjc.hotel.room_images.dto.RoomImageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoomRatesImageRepository extends JpaRepository<RoomImageEntity, Long> {

    /**
     * 특정 호실의 객실 이미지 목록 조회
     */
    List<RoomImageEntity> findByRoom_RoomId(Long roomId);

    /**
     * 이미지 ID 및 호실 ID로 이미지 조회
     */
    Optional<RoomImageEntity> findByRoomImageIdAndRoom_RoomId(Long roomImageId, Long roomId);
}
