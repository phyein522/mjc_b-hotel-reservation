package com.mjc.hotel.rates.repository;

import com.mjc.hotel.rates.entity.RoomAmenityEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoomAmenityRepository extends JpaRepository<RoomAmenityEntity, Long> {

    /**
     * 호실 ID로 편의시설 정보 조회
     */
    Optional<RoomAmenityEntity> findByRoomId(Long roomId);

    /**
     * 호실 ID로 편의시설 정보 삭제
     */
    void deleteByRoomId(Long roomId);
}
