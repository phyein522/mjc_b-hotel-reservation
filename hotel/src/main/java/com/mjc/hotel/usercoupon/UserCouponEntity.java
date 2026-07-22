package com.mjc.hotel.usercoupon;

import com.mjc.hotel.common.dto.BaseEntity;
import com.mjc.hotel.coupon.CouponEntity;
import com.mjc.hotel.coupon.ICoupon;
import com.mjc.hotel.user.dto.IUser;
import com.mjc.hotel.user.entity.UserEntity;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_coupon")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserCouponEntity extends BaseEntity implements IUserCoupon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userCouponId;

    @Column(name = "쿠폰 발급날짜")
    private LocalDateTime issuedAt;

    @Column(name = "상태")
    private UserCouponStatusEnum userCouponStatus;

    @Column(name = "사용된 시각")
    private LocalDateTime usedAt;

    @Column(name = "처음 결제된 id")
    private Long usedPaymentId;

    @Transient
    private Long userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @Transient
    private Long couponId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "coupon_id", nullable = false)
    private CouponEntity coupon;

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
            user = new UserEntity();
        }

        user.setUserId(userId);
    }

    @Override
    public void setUser(IUser user) {

        if (user == null) {
            return;
        }

        if (this.user == null) {
            this.user = new UserEntity();
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
            coupon = new CouponEntity();
        }

        coupon.setCouponId(couponId);
    }

    @Override
    public void setCoupon(ICoupon coupon) {

        if (coupon == null) {
            return;
        }

        if (this.coupon == null) {
            this.coupon = new CouponEntity();
        }

        this.coupon.copyMembers(coupon, true);
        this.couponId = coupon.getCouponId();
    }
}
