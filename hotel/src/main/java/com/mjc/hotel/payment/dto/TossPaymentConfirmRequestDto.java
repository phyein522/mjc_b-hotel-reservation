package com.mjc.hotel.payment.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TossPaymentConfirmRequestDto {
    private Long bookingId;
    private String paymentKey;
    private String orderId;
    private BigDecimal amount;
}
