package com.mjc.hotel.hotelsattr;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HotelAttrRepository extends JpaRepository<HotelAttrEntity, Long> {
}
