package com.mjc.hotel.payment.dto;

import com.mjc.hotel.common.dto.IBase;
import com.mjc.hotel.payment.enums.PaymentMethod;
import com.mjc.hotel.payment.enums.PaymentStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@tools.jackson.databind.annotation.JsonDeserialize(as = PaymentDto.class)
public interface IPayment extends IBase {

    Long getPaymentId();
    void setPaymentId(Long paymentId);

    Long getUserId();
    void setUserId(Long userId);

    Long getRoomId();
    void setRoomId(Long roomId);

    BigDecimal getAmount();
    void setAmount(BigDecimal amount);

    PaymentMethod getPaymentMethod();
    void setPaymentMethod(PaymentMethod paymentMethod);

    PaymentStatus getPaymentStatus();
    void setPaymentStatus(PaymentStatus paymentStatus);

    LocalDateTime getPaidAt();
    void setPaidAt(LocalDateTime paidAt);

    LocalDateTime getCancelledAt();
    void setCancelledAt(LocalDateTime cancelledAt);

    String getMemo();
    void setMemo(String memo);

    default IPayment copyMembers(IPayment source, boolean forced) {
        if (source == null) return this;
        IBase.super.copyMembers(source, forced);

        if (forced || source.getPaymentId() != null)
            this.setPaymentId(source.getPaymentId());
        if (forced || source.getUserId() != null)
            this.setUserId(source.getUserId());
        if (forced || source.getRoomId() != null)
            this.setRoomId(source.getRoomId());
        if (forced || source.getAmount() != null)
            this.setAmount(source.getAmount());
        if (forced || source.getPaymentMethod() != null)
            this.setPaymentMethod(source.getPaymentMethod());
        if (forced || source.getPaymentStatus() != null)
            this.setPaymentStatus(source.getPaymentStatus());
        if (forced || source.getPaidAt() != null)
            this.setPaidAt(source.getPaidAt());
        if (forced || source.getCancelledAt() != null)
            this.setCancelledAt(source.getCancelledAt());
        if (forced || source.getMemo() != null)
            this.setMemo(source.getMemo());

        return this;
    }
}