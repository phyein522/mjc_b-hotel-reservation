package com.mjc.hotel.payment.dto;

import lombok.Data;

@Data
public class TossPaymentFailRequestDto {
    private String orderId;
    private String code;
    private String message;
}
