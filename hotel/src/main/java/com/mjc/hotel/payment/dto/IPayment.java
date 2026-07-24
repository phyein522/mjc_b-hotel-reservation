package com.mjc.hotel.payment.dto;

import com.mjc.hotel.bookings.IBooking;
import com.mjc.hotel.common.dto.IBase;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@tools.jackson.databind.annotation.JsonDeserialize(as = PaymentDto.class)
public interface IPayment extends IBase {
    Long getPaymentId();
    void setPaymentId(Long paymentId);

    Long getBookingId();
    void setBookingId(Long bookingId);

    String getTransactionNum();
    void setTransactionNum(String transactionNum);

    IBooking getBooking();
    void setBooking(IBooking booking);

    PaymentMethod getPaymentMethod();
    void setPaymentMethod(PaymentMethod paymentMethod);

    BigDecimal getTotalAmount();
    void setTotalAmount(BigDecimal totalAmount);

    String getCurrency();
    void setCurrency(String currency);

    PaymentStatus getPaymentStatus();
    void setPaymentStatus(PaymentStatus paymentStatus);

    LocalDateTime getApprovedAt();
    void setApprovedAt(LocalDateTime approvedAt);

    LocalDateTime getCancelledAt();
    void setCancelledAt(LocalDateTime cancelledAt);

    String getPgTransactionId();
    void setPgTransactionId(String pgTransactionId);

    String getCardCompany();
    void setCardCompany(String cardCompany);

    Long getCouponId();
    void setCouponId(Long couponId);

    BigDecimal getUsedPoint();
    void setUsedPoint(BigDecimal usedPoint);

    String getCardNumber();
    void setCardNumber(String cardNumber);

    /**
     * 유일한 번호
     */
    String getOrderId();
    void setOrderId(String orderId);

    /**
     * booking 의 bookingNo 필드의 값과 같아야 한다. "SN-2026-0724-5928"
     */
    String getPaymentKey();
    void setPaymentKey(String paymentKey);

    String getProvider();
    void setProvider(String provider);

    String getReceiptUrl();
    void setReceiptUrl(String receiptUrl);

    String getFailCode();
    void setFailCode(String failCode);

    String getFailMessage();
    void setFailMessage(String failMessage);

    default IPayment copyMembers(IPayment source, boolean forced) {
        if (source == null) {
            return this;
        }
        IBase.super.copyMembers(source, forced);
        if (forced || source.getPaymentId() != null) {
            this.setPaymentId(source.getPaymentId());
        }
        if ( forced || source.getBookingId() != null ) {
            this.setBookingId(source.getBookingId());
        }
        if ( forced || source.getBooking() != null ) {
            this.getBooking().copyMembers(source.getBooking(), forced);
        }
        if (forced || source.getTransactionNum() != null) {
            this.setTransactionNum(source.getTransactionNum());
        }
        if (forced || source.getPaymentMethod() != null) {
            this.setPaymentMethod(source.getPaymentMethod());
        }
        if (forced || source.getTotalAmount() != null) {
            this.setTotalAmount(source.getTotalAmount());
        }

        if (forced || source.getCurrency() != null) {
            this.setCurrency(source.getCurrency());
        }
        if (forced || source.getPaymentStatus() != null) {
            this.setPaymentStatus(source.getPaymentStatus());
        }
        if (forced || source.getApprovedAt() != null) {
            this.setApprovedAt(source.getApprovedAt());
        }
        if (forced || source.getCancelledAt() != null) {
            this.setCancelledAt(source.getCancelledAt());
        }
        if (forced || source.getPgTransactionId() != null) {
            this.setPgTransactionId(source.getPgTransactionId());
        }
        if (forced || source.getCardCompany() != null) {
            this.setCardCompany(source.getCardCompany());
        }
        if (forced || source.getCouponId() != null) {
            this.setCouponId(source.getCouponId());
        }
        if (forced || source.getUsedPoint() != null) {
            this.setUsedPoint(source.getUsedPoint());
        }
        if (forced || source.getCardNumber() != null) {
            this.setCardNumber(source.getCardNumber());
        }
        if (forced || source.getOrderId() != null) {
            this.setOrderId(source.getOrderId());
        }
        if (forced || source.getPaymentKey() != null) {
            this.setPaymentKey(source.getPaymentKey());
        }
        if (forced || source.getProvider() != null) {
            this.setProvider(source.getProvider());
        }
        if (forced || source.getReceiptUrl() != null) {
            this.setReceiptUrl(source.getReceiptUrl());
        }
        if (forced || source.getFailCode() != null) {
            this.setFailCode(source.getFailCode());
        }
        if (forced || source.getFailMessage() != null) {
            this.setFailMessage(source.getFailMessage());
        }
        return this;
    }
}
