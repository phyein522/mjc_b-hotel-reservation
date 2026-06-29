package com.mjc.hotel.sales_analysis.repository;

import com.mjc.hotel.sales_analysis.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.math.BigDecimal;

public interface SalesRepository extends JpaRepository<Payment, Long> {

    // 특정 호텔 + 특정 연도의 총 매출 합산
    // PAID 상태 + 취소 안된 예약만 집계
    @Query("""
        SELECT COALESCE(SUM(p.amount), 0)
        FROM Payment p
        JOIN p.booking b
        WHERE b.hotel.hotelId = :hotelId
          AND p.paymentStatus = 'PAID'
          AND b.cancelledAt IS NULL
          AND YEAR(b.checkInDate) = :year
        """)
    BigDecimal sumSalesByHotelAndYear(
            @Param("hotelId") Long hotelId,
            @Param("year") int year
    );
}
