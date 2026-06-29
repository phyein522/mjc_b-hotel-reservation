package com.mjc.hotel.sales_analysis.repository;

import com.mjc.hotel.sales_analysis.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByHotelId(Long hotelId);
}
