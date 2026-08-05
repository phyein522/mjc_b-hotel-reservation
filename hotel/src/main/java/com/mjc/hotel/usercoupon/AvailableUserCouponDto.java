package com.mjc.hotel.usercoupon;

import com.mjc.hotel.coupon.CouponDiscountTypeEnum;
import com.mjc.hotel.coupon.CouponEntity;

import java.math.BigDecimal;
import java.time.LocalDate;

public record AvailableUserCouponDto(
        Long userCouponId,
        Long couponId,
        String code,
        String name,
        String description,
        CouponDiscountTypeEnum discountType,
        BigDecimal discountValue,
        BigDecimal minOrder,
        BigDecimal maxDiscount,
        LocalDate expirationDate
) {
    public static AvailableUserCouponDto from(UserCouponEntity userCoupon) {
        CouponEntity coupon = userCoupon.getCoupon();
        return new AvailableUserCouponDto(
                userCoupon.getUserCouponId(),
                coupon.getCouponId(),
                coupon.getCode(),
                coupon.getName(),
                coupon.getDescription(),
                coupon.getDiscountType(),
                coupon.getDiscountValue(),
                coupon.getMinOrder(),
                coupon.getMaxDiscount(),
                coupon.getExpirationDate()
        );
    }
}
