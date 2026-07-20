package com.mjc.hotel.payment;

import com.mjc.hotel.bookings.BookingEntity;
import com.mjc.hotel.payment.dto.RefundCreateRequest;
import com.mjc.hotel.payment.dto.RefundResponse;
import com.mjc.hotel.payment.entity.Payment;
import com.mjc.hotel.payment.entity.PaymentStatus;
import com.mjc.hotel.payment.entity.Refund;
import com.mjc.hotel.payment.repository.PaymentRepository;
import com.mjc.hotel.payment.repository.RefundRepository;
import com.mjc.hotel.payment.service.RefundService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class RefundServiceTest {

    @Mock
    private RefundRepository refundRepository;

    @Mock
    private PaymentRepository paymentRepository;

    @InjectMocks
    private RefundService refundService;

    @Test
    @DisplayName("환불 신청 테스트 - 전액 환불 시 REFUNDED 상태로 변경되어야 함")
    void requestRefundTest() {
        Long paymentId = 100L;
        BookingEntity booking = new BookingEntity();
        booking.setBookingId(1L);

        Payment payment = Payment.builder()
                .id(paymentId)
                .booking(booking)
                .paymentMethod("CARD")
                .paymentStatus(PaymentStatus.PAID)
                .amount(new BigDecimal("150000"))
                .build();

        given(paymentRepository.findById(paymentId)).willReturn(Optional.of(payment));

        Refund savedRefund = Refund.builder()
                .id(50L)
                .payment(payment)
                .refundAmount(new BigDecimal("150000"))
                .reason("고객 단순 변심")
                .refundStatus("REQUESTED")
                .refundType("CANCEL")
                .build();

        given(refundRepository.save(any(Refund.class))).willReturn(savedRefund);

        RefundCreateRequest request = RefundCreateRequest.builder()
                .paymentId(paymentId)
                .refundAmount(new BigDecimal("150000"))
                .refundReason("고객 단순 변심")
                .build();

        RefundResponse response = refundService.requestRefund(request);

        assertThat(response).isNotNull();
        assertThat(response.getRefundId()).isEqualTo(50L);
        assertThat(response.getRefundStatus()).isEqualTo("REQUESTED");
        assertThat(payment.getPaymentStatus()).isEqualTo(PaymentStatus.REFUNDED);
    }
}
