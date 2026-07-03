package com.mjc.hotel.hotelsimage;

import com.mjc.hotel.hotels.HotelEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HotelImageRepository extends JpaRepository<HotelImageEntity, Long> {
    Page<HotelImageEntity> findAllByHotelEquals(HotelEntity hotel, Pageable pageable);

}