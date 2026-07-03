package com.mjc.hotel.sales_analysis.repository;

import com.mjc.hotel.sales_analysis.entity.FakePayment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FakePaymentRepository extends JpaRepository<FakePayment, Long> {
}
