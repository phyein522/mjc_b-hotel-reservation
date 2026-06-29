package com.mjc.hotel.sales_analysis.repository;

import com.mjc.hotel.sales_analysis.entity.Hotel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HotelRepository extends JpaRepository<Hotel, Long> {

    // 운영중인 호텔만 조회 (드롭다운용)
    List<Hotel> findAllByIsActiveTrue();
}

