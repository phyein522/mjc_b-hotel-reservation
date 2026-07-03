package com.mjc.hotel.sales_analysis.repository;

import com.mjc.hotel.sales_analysis.entity.FakeRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository("salesFakeRoomRepository")
public interface FakeRoomRepository extends JpaRepository<FakeRoom, Long> {
}
