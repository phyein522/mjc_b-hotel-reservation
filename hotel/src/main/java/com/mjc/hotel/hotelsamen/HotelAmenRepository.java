package com.mjc.hotel.hotelsamen;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HotelAmenRepository extends JpaRepository<HotelAmenEntity, Long>  {
}
