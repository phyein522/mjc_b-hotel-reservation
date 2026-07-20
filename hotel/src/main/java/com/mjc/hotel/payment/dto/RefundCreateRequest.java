package com.mjc.hotel.payment.dto;

import lombok.*;
import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RefundCreateRequest {
    private Long paymentId;
    private BigDecimal refundAmount;
    private String refundReason;
}
