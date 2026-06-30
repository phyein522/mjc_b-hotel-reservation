package com.mjc.hotel.hotelstrans;

import com.mjc.hotel.hotelsimage.HotelImageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HotelTransRepository extends JpaRepository<HotelTransEntity, Long> {
}
