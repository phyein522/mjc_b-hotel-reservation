package com.mjc.hotel.sales_analysis.repository;

import com.mjc.hotel.sales_analysis.entity.FakeHotel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FakeHotelRepository extends JpaRepository<FakeHotel, Long> {
}
