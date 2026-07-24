package com.mjc.hotel.rates.repository;

import com.mjc.hotel.rooms.dto.RoomEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoomRatesRepository extends JpaRepository<RoomEntity, Long> {

    /**
     * 특정 호텔의 활성화된 호실 목록 (페이징)
     */
    Page<RoomEntity> findByHotel_HotelIdAndIsActiveTrue(Long hotelId, Pageable pageable);

    /**
     * 특정 호텔의 전체 호실 목록 (페이징)
     */
    Page<RoomEntity> findByHotel_HotelId(Long hotelId, Pageable pageable);

    /**
     * 호실 ID로 활성 호실 조회
     */
    Optional<RoomEntity> findByRoomIdAndIsActiveTrue(Long roomId);

    /**
     * 동일 호텔 내 호실 번호 중복 여부 확인
     */
    @Query("SELECT COUNT(r) > 0 FROM RoomEntity r WHERE r.hotel.hotelId = :hotelId AND r.number = :number AND r.isActive = true")
    boolean existsByHotelIdAndNumberAndIsActiveTrue(@Param("hotelId") Long hotelId, @Param("number") String number);
}
