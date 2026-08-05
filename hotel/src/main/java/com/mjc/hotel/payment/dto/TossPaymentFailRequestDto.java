package com.mjc.hotel.payment.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TossPaymentFailRequestDto {
    private String paymentKey;
    private String code;
    private String message;
}
