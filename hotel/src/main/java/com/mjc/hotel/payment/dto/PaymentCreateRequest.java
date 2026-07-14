package com.mjc.hotel.payment.dto;

import lombok.*;
import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentCreateRequest {
    private Long bookingId;
    private String paymentMethod;
    private BigDecimal amount;
    @Builder.Default
    private String currency = "KRW";
    private Long couponId;
    @Builder.Default
    private Integer usedPoint = 0;
    @Builder.Default
    private BigDecimal discountAmount = BigDecimal.ZERO;
    private String cardCompany;
    private String cardLast4;
}
