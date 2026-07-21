package com.mjc.hotel.payment.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class TossPaymentReadyResponseDto {
    private Long paymentId;
    private Long bookingId;
    private String orderId;
    private String orderName;
    private BigDecimal amount;
}
