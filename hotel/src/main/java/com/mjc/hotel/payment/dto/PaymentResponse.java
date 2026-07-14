package com.mjc.hotel.payment.dto;

import com.mjc.hotel.payment.entity.PaymentStatus;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentResponse {
    private Long paymentId;
    private Long bookingId;
    private String paymentMethod;
    private PaymentStatus paymentStatus;
    private BigDecimal amount;
    private String currency;
    private LocalDateTime paidAt;
    private LocalDateTime cancelledAt;
    private String cardCompany;
    private String cardLast4;
    private Long couponId;
    private Integer usedPoint;
    private BigDecimal discountAmount;
    private String pgTransactionId;
}
