package com.mjc.hotel.promotion;

import com.mjc.hotel.rooms.dto.RoomEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PromotionRepository extends JpaRepository<PromotionEntity, Long> {
    Page<PromotionEntity> findAllByRoomEquals(RoomEntity room, Pageable pageable);

    @Query("""
            SELECT p
            FROM PromotionEntity p
            WHERE p.room = :room
              AND (:proId IS NULL OR p.proId <> :proId)
              AND p.startDate <= :endDate
              AND p.endDate >= :startDate
            """)
    List<PromotionEntity> findOverlappingPromotions(
            @Param("room") RoomEntity room,
            @Param("proId") Long proId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    @Query("""
            SELECT h.user.userId
            FROM RoomEntity r
            JOIN r.hotel h
            WHERE r.roomId = :roomId
            """)
    Optional<Long> findHotelManagerUserIdByRoomId(@Param("roomId") Long roomId);
}
