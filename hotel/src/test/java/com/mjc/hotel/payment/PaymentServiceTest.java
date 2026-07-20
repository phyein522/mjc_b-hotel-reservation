package com.mjc.hotel.payment;

import com.mjc.hotel.bookings.BookingEntity;
import com.mjc.hotel.bookings.BookingRepository;
import com.mjc.hotel.payment.dto.PaymentConfirmRequest;
import com.mjc.hotel.payment.dto.PaymentCreateRequest;
import com.mjc.hotel.payment.dto.PaymentResponse;
import com.mjc.hotel.payment.entity.Payment;
import com.mjc.hotel.payment.entity.PaymentStatus;
import com.mjc.hotel.payment.repository.PaymentRepository;
import com.mjc.hotel.payment.service.PaymentService;
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
class PaymentServiceTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private BookingRepository bookingRepository;

    @InjectMocks
    private PaymentService paymentService;

    @Test
    @DisplayName("결제 요청 생성 테스트 - READY 상태로 생성되어야 함")
    void createPaymentTest() {
        Long bookingId = 1L;
        BookingEntity booking = new BookingEntity();
        booking.setBookingId(bookingId);
        given(bookingRepository.findById(bookingId)).willReturn(Optional.of(booking));

        PaymentCreateRequest request = PaymentCreateRequest.builder()
                .bookingId(bookingId)
                .paymentMethod("CARD")
                .amount(new BigDecimal("150000"))
                .currency("KRW")
                .cardCompany("Kookmin")
                .cardLast4("1234")
                .build();

        Payment savedPayment = Payment.builder()
                .id(100L)
                .booking(booking)
                .paymentMethod("CARD")
                .paymentStatus(PaymentStatus.READY)
                .amount(new BigDecimal("150000"))
                .currency("KRW")
                .cardCompany("Kookmin")
                .cardLast4("1234")
                .build();

        given(paymentRepository.save(any(Payment.class))).willReturn(savedPayment);

        PaymentResponse response = paymentService.createPayment(request);

        assertThat(response).isNotNull();
        assertThat(response.getPaymentId()).isEqualTo(100L);
        assertThat(response.getPaymentStatus()).isEqualTo(PaymentStatus.READY);
    }

    @Test
    @DisplayName("결제 승인 테스트 - PAID 상태로 변경되어야 함")
    void confirmPaymentTest() {
        Long paymentId = 100L;
        BookingEntity booking = new BookingEntity();
        booking.setBookingId(1L);
        Payment payment = Payment.builder()
                .id(paymentId)
                .booking(booking)
                .paymentMethod("CARD")
                .paymentStatus(PaymentStatus.READY)
                .amount(new BigDecimal("150000"))
                .currency("KRW")
                .build();

        given(paymentRepository.findById(paymentId)).willReturn(Optional.of(payment));
        given(paymentRepository.save(any(Payment.class))).willAnswer(invocation -> invocation.getArgument(0));

        PaymentConfirmRequest confirmRequest = PaymentConfirmRequest.builder()
                .pgTransactionId("PG_TX_12345")
                .build();

        PaymentResponse response = paymentService.confirmPayment(paymentId, confirmRequest);

        assertThat(response.getPaymentStatus()).isEqualTo(PaymentStatus.PAID);
        assertThat(response.getPgTransactionId()).isEqualTo("PG_TX_12345");
        assertThat(response.getPaidAt()).isNotNull();
    }
}
