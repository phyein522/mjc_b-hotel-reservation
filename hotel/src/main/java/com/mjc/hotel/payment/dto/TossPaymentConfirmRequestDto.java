package com.mjc.hotel.payment.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class TossPaymentConfirmRequestDto {
    private Long bookingId;
    private String paymentKey;
    private String orderId;
    private BigDecimal amount;
}
