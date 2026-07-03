package com.mjc.hotel.sales_analysis.repository;

import com.mjc.hotel.sales_analysis.entity.FakeBooking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FakeBookingRepository extends JpaRepository<FakeBooking, Long> {
}
