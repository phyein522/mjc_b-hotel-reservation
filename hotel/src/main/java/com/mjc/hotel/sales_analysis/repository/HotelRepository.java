package com.mjc.hotel.sales_analysis.repository;

import com.mjc.hotel.sales_analysis.entity.Hotel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HotelRepository extends JpaRepository<Hotel, Long> {
}
