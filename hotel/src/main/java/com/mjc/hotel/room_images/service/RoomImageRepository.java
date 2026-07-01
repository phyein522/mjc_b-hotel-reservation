package com.mjc.hotel.room_images.service;

import com.mjc.hotel.room_images.dto.RoomImageEntity;
import com.mjc.hotel.rooms.dto.RoomEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoomImageRepository extends JpaRepository<RoomImageEntity, Long> {
	Page<RoomImageEntity> findAllByRoomEquals(RoomEntity room, Pageable pageable);
}
