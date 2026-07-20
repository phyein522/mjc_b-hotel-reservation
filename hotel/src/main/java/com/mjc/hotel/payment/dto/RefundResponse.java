package com.mjc.hotel.payment.dto;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RefundResponse {
    private Long refundId;
    private Long paymentId;
    private BigDecimal refundAmount;
    private String refundReason;
    private String refundStatus;
    private LocalDateTime requestedAt;
    private LocalDateTime completedAt;
}
