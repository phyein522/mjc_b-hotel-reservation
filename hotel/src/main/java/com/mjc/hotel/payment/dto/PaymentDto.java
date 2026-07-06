package com.mjc.hotel.payment.dto;

import com.mjc.hotel.common.dto.BaseDto;
import com.mjc.hotel.payment.enums.PaymentMethod;
import com.mjc.hotel.payment.enums.PaymentStatus;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class PaymentDto extends BaseDto implements IPayment {

    private Long paymentId;
    private Long userId;
    private Long roomId;
    private BigDecimal amount;
    private PaymentMethod paymentMethod;
    private PaymentStatus paymentStatus;
    private LocalDateTime paidAt;
    private LocalDateTime cancelledAt;
    private String memo;
}
