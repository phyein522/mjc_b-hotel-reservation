package com.mjc.hotel.coupon;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface CouponRepository extends JpaRepository<CouponEntity, Long> {
    List<CouponEntity> findAllByStatusAndExpirationDateGreaterThanEqual(
            CouponStatusEnum status,
            LocalDate expirationDate
    );
}
