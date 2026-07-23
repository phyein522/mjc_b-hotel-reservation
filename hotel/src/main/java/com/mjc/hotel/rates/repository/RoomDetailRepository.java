package com.mjc.hotel.rates.repository;

import com.mjc.hotel.rates.entity.RoomDetailEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoomDetailRepository extends JpaRepository<RoomDetailEntity, Long> {

    /**
     * 호실 ID로 상세 설명 조회
     */
    Optional<RoomDetailEntity> findByRoomId(Long roomId);

    /**
     * 호실 ID로 상세 설명 삭제
     */
    void deleteByRoomId(Long roomId);
}
