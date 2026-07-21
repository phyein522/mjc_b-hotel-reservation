package com.mjc.hotel.payment.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class TossPaymentReadyRequestDto {
    private Long memberId;
    private Long bookingId;
    private BigDecimal amount;
    private String orderName;
}
