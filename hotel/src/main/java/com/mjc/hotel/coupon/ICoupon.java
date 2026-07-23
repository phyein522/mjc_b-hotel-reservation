package com.mjc.hotel.coupon;

import com.mjc.hotel.common.dto.IBase;
import com.mjc.hotel.user.dto.IUser;
import org.hibernate.LazyInitializationException;

import java.math.BigDecimal;
import java.time.LocalDate;

@tools.jackson.databind.annotation.JsonDeserialize(as = CouponDto.class)
public interface ICoupon extends IBase {
    Long getCouponId();
    void setCouponId(Long couponId);

    String getCode();
    void setCode(String code);

    String getName();
    void setName(String name);

    String getDescription();
    void setDescription(String description);

    CouponDiscountTypeEnum getDiscountType();
    void setDiscountType(CouponDiscountTypeEnum discountType);

    BigDecimal getDiscountValue();
    void setDiscountValue(BigDecimal discountValue);

    BigDecimal getMinOrder();
    void setMinOrder(BigDecimal minOrder);

    BigDecimal getMaxDiscount();
    void setMaxDiscount(BigDecimal maxDiscount);

    LocalDate getExpirationDate();
    void setExpirationDate(LocalDate expirationDate);

    CouponStatusEnum getStatus();
    void setStatus(CouponStatusEnum status);

    Long getUserId();
    void setUserId(Long userId);

    IUser getUser();
    void setUser(IUser user);

    default ICoupon copyMembers(ICoupon source, boolean forced) {

        if (source == null) {
            return this;
        }
        IBase.super.copyMembers(source, forced);

        if (forced || source.getCouponId() != null) {
            this.setCouponId(source.getCouponId());
        }

        if (forced || source.getCode() != null) {
            this.setCode(source.getCode());
        }

        if (forced || source.getName() != null) {
            this.setName(source.getName());
        }

        if (forced || source.getDescription() != null) {
            this.setDescription(source.getDescription());
        }

        if (forced || source.getDiscountType() != null) {
            this.setDiscountType(source.getDiscountType());
        }


        if (forced || source.getDiscountValue() != null) {
            this.setDiscountValue(source.getDiscountValue());
        }


        if (forced || source.getMinOrder() != null) {
            this.setMinOrder(source.getMinOrder());
        }


        if (forced || source.getMaxDiscount() != null) {
            this.setMaxDiscount(source.getMaxDiscount());
        }

        if (forced || source.getExpirationDate() != null) {
            this.setExpirationDate(source.getExpirationDate());
        }

        if (forced || source.getStatus() != null) {
            this.setStatus(source.getStatus());
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
        return this;
    }
}
