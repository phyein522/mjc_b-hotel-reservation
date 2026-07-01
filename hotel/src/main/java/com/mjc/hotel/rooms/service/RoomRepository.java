package com.mjc.hotel.rooms.service;

import com.mjc.hotel.rooms.dto.RoomEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoomRepository extends JpaRepository<RoomEntity, Long> {
	Page<RoomEntity> findAllByHotelIdEquals(Long hotelId, Pageable pageable);
}
