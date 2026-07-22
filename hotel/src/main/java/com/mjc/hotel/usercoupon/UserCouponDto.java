package com.mjc.hotel.usercoupon;

import com.mjc.hotel.common.dto.BaseDto;
import com.mjc.hotel.coupon.CouponDto;
import com.mjc.hotel.coupon.ICoupon;
import com.mjc.hotel.user.dto.IUser;
import com.mjc.hotel.user.dto.UserDto;
import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserCouponDto extends BaseDto implements IUserCoupon {
    private Long userCouponId;
    private LocalDateTime issuedAt;
    private UserCouponStatusEnum userCouponStatus;
    private LocalDateTime usedAt;
    private Long usedPaymentId;
    private Long userId;
    private Long couponId;

    private UserDto user;
    private CouponDto coupon;

    @Override
    public Long getUserId() {

        if (user != null) {
            return user.getUserId();
        }

        return userId;
    }

    @Override
    public void setUserId(Long userId) {

        this.userId = userId;

        if (user == null) {
            user = new UserDto();
        }

        user.setUserId(userId);
    }

    @Override
    public void setUser(IUser user) {

        if (user == null) {
            return;
        }

        if (this.user == null) {
            this.user = new UserDto();
        }

        this.user.copyMembers(user, true);
        this.userId = user.getUserId();
    }

    @Override
    public Long getCouponId() {

        if (coupon != null) {
            return coupon.getCouponId();
        }

        return couponId;
    }

    @Override
    public void setCouponId(Long couponId) {

        this.couponId = couponId;

        if (coupon == null) {
            coupon = new CouponDto();
        }

        coupon.setCouponId(couponId);
    }

    @Override
    public void setCoupon(ICoupon coupon) {

        if (coupon == null) {
            return;
        }

        if (this.coupon == null) {
            this.coupon = new CouponDto();
        }

        this.coupon.copyMembers(coupon, true);
        this.couponId = coupon.getCouponId();
    }
}
