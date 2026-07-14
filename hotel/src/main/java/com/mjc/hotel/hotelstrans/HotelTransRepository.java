package com.mjc.hotel.hotelstrans;

import com.mjc.hotel.hotels.HotelEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HotelTransRepository extends JpaRepository<HotelTransEntity, Long> {
    Page<HotelTransEntity> findAllByHotelEquals(HotelEntity hotel, Pageable pageable);
}
