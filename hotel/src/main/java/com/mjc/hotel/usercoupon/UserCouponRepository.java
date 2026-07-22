package com.mjc.hotel.usercoupon;

import org.apache.ibatis.annotations.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UserCouponRepository extends JpaRepository<UserCouponEntity, Long> {
    // 중복 발급 방지
    @Query("""
        SELECT COUNT(u)
        FROM UserCouponEntity u
        WHERE u.user.userId = :userId
        AND u.coupon.couponId = :couponId
        """)
    Long countCoupon(@Param("userId") Long userId,
                     @Param("couponId") Long couponId);
}
