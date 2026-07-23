package com.mjc.hotel.coupon;

import com.mjc.hotel.common.dto.BaseDto;
import com.mjc.hotel.user.dto.IUser;
import com.mjc.hotel.user.dto.UserDto;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CouponDto extends BaseDto implements ICoupon {
    private Long couponId;
    private String code;
    private String name;
    private String description;
    private CouponDiscountTypeEnum discountType;
    private BigDecimal discountValue;
    private BigDecimal minOrder;
    private BigDecimal maxDiscount;
    private LocalDate expirationDate;
    private CouponStatusEnum status;

    private Long userId;
    private UserDto user;

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
}
