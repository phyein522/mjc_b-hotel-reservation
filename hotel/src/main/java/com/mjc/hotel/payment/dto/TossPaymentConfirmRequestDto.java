package com.mjc.hotel.payment.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class TossPaymentConfirmRequestDto {
    private Long bookingId;
    private String paymentKey;
    private String orderId;
    private BigDecimal amount;
}
