package com.mjc.hotel.payment.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TossPaymentFailRequestDto {
    private String paymentKey;
    private String code;
    private String message;
}
