package com.mjc.hotel.coupon;

import com.mjc.hotel.common.dto.BaseEntity;
import com.mjc.hotel.user.dto.IUser;
import com.mjc.hotel.user.entity.UserEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "coupon")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CouponEntity extends BaseEntity implements ICoupon {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "coupon_id", comment = "기본키")
    private Long couponId;

    @Column(nullable = false, length = 100, comment = "쿠폰 코드")
    private String code;

    @Column(nullable = false, length = 200, comment = "쿠폰 이름")
    private String name;

    @Column(comment = "쿠폰 설명")
    private String description;

    @Column(nullable = false, comment = "할인 종류")
    private CouponDiscountTypeEnum discountType;

    @Column(nullable = false, comment = "할인 값")
    private BigDecimal discountValue;

    @Column(comment = "최소 주문 금액")
    private BigDecimal minOrder;

    @Column(comment = "최대 할인 금액")
    private BigDecimal maxDiscount;

    @Column(nullable = false, comment = "만료일")
    private LocalDate expirationDate;

    @Column(nullable = false, comment = "쿠폰 상태")
    private CouponStatusEnum status;

    @Transient
    private Long userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

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
}
