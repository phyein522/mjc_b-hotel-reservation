package com.mjc.hotel.hotels;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HotelRepository extends JpaRepository<HotelEntity, Long> {
    Page<HotelEntity> findAllByUser_UserId(Long userId, Pageable pageable);
}
