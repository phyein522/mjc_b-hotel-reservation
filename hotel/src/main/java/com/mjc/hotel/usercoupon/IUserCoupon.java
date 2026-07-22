package com.mjc.hotel.usercoupon;

import com.mjc.hotel.common.dto.IBase;
import com.mjc.hotel.coupon.ICoupon;
import com.mjc.hotel.user.dto.IUser;
import org.hibernate.LazyInitializationException;

import java.time.LocalDateTime;

@tools.jackson.databind.annotation.JsonDeserialize(as = UserCouponDto.class)
public interface IUserCoupon extends IBase {

    Long getUserCouponId();
    void setUserCouponId(Long userCouponId);

    LocalDateTime getIssuedAt();
    void setIssuedAt(LocalDateTime issuedAt);

    UserCouponStatusEnum getUserCouponStatus();
    void setUserCouponStatus(UserCouponStatusEnum userCouponStatus);

    LocalDateTime getUsedAt();
    void setUsedAt(LocalDateTime usedAt);

    Long getUsedPaymentId();
    void setUsedPaymentId(Long usedPaymentId);

    Long getUserId();
    void setUserId(Long userId);

    Long getCouponId();
    void setCouponId(Long couponId);

    IUser getUser();
    void setUser(IUser user);

    ICoupon getCoupon();
    void setCoupon(ICoupon coupon);

    default IUserCoupon copyMembers(IUserCoupon source, boolean forced) {

        if (source == null) {
            return this;
        }

        IBase.super.copyMembers(source, forced);

        if (forced || source.getUserCouponId() != null) {
            this.setUserCouponId(source.getUserCouponId());
        }

        if (forced || source.getIssuedAt() != null) {
            this.setIssuedAt(source.getIssuedAt());
        }

        if (forced || source.getUserCouponStatus() != null) {
            this.setUserCouponStatus(source.getUserCouponStatus());
        }

        if (forced || source.getUsedAt() != null) {
            this.setUsedAt(source.getUsedAt());
        }

        if (forced || source.getUsedPaymentId() != null) {
            this.setUsedPaymentId(source.getUsedPaymentId());
        }

        if (forced || source.getUserId() != null) {
            this.setUserId(source.getUserId());

            try {
                if (source.getUser() != null) {
                    this.setUser(source.getUser());
                }
            } catch (LazyInitializationException e) {
                System.err.println(e.getMessage());
            }
        }

        if (forced || source.getCouponId() != null) {
            this.setCouponId(source.getCouponId());

            try {
                if (source.getCoupon() != null) {
                    this.setCoupon(source.getCoupon());
                }
            } catch (LazyInitializationException e) {
                System.err.println(e.getMessage());
            }
        }

        return this;
    }
}