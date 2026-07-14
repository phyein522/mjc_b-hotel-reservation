package com.mjc.hotel.payment.service;

import com.mjc.hotel.bookings.BookingEntity;
import com.mjc.hotel.bookings.BookingRepository;
import com.mjc.hotel.payment.dto.PaymentCreateRequest;
import com.mjc.hotel.payment.dto.PaymentResponse;
import com.mjc.hotel.payment.entity.Payment;
import com.mjc.hotel.payment.entity.PaymentStatus;
import com.mjc.hotel.payment.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final BookingRepository bookingRepository;

    @Transactional
    public PaymentResponse createPayment(PaymentCreateRequest request) {
        BookingEntity booking = bookingRepository.findById(request.getBookingId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 예약입니다. ID: " + request.getBookingId()));

        Payment payment = Payment.builder()
                .booking(booking)
                .paymentMethod(request.getPaymentMethod())
                .paymentStatus(PaymentStatus.READY)
                .amount(request.getAmount())
                .currency(request.getCurrency())
                .couponId(request.getCouponId())
                .usedPoint(request.getUsedPoint())
                .discountAmount(request.getDiscountAmount())
                .cardCompany(request.getCardCompany())
                .cardLast4(request.getCardLast4())
                .build();

        Payment savedPayment = paymentRepository.save(payment);
        return convertToResponse(savedPayment);
    }

    @Transactional
    public PaymentResponse confirmPayment(Long paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 결제 정보입니다. ID: " + paymentId));

        if (payment.getPaymentStatus() != PaymentStatus.READY) {
            throw new IllegalStateException("결제 완료 처리는 READY 상태의 결제만 가능합니다. 현재 상태: " + payment.getPaymentStatus());
        }

        payment.setPaymentStatus(PaymentStatus.PAID);
        payment.setPaidAt(LocalDateTime.now());
        payment.setPgTransactionId("PG_MOCK_" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());

        Payment savedPayment = paymentRepository.save(payment);
        return convertToResponse(savedPayment);
    }

    public PaymentResponse getPayment(Long paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 결제 정보입니다. ID: " + paymentId));
        return convertToResponse(payment);
    }

    private PaymentResponse convertToResponse(Payment payment) {
        return PaymentResponse.builder()
                .paymentId(payment.getId())
                .bookingId(payment.getBooking().getBookingId())
                .paymentMethod(payment.getPaymentMethod())
                .paymentStatus(payment.getPaymentStatus())
                .amount(payment.getAmount())
                .currency(payment.getCurrency())
                .paidAt(payment.getPaidAt())
                .cancelledAt(payment.getCancelledAt())
                .cardCompany(payment.getCardCompany())
                .cardLast4(payment.getCardLast4())
                .couponId(payment.getCouponId())
                .usedPoint(payment.getUsedPoint())
                .discountAmount(payment.getDiscountAmount())
                .pgTransactionId(payment.getPgTransactionId())
                .build();
    }
}
