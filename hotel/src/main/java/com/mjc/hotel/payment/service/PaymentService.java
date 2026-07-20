package com.mjc.hotel.payment.service;

import com.mjc.hotel.bookings.BookingEntity;
import com.mjc.hotel.bookings.BookingRepository;
import com.mjc.hotel.payment.dto.PaymentConfirmRequest;
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
                .currency(request.getCurrency() != null ? request.getCurrency() : "KRW")
                .couponId(request.getCouponId())
                .usedPoint(request.getUsedPoint() != null ? request.getUsedPoint() : 0)
                .discountAmount(request.getDiscountAmount() != null ? request.getDiscountAmount() : java.math.BigDecimal.ZERO)
                .cardCompany(request.getCardCompany())
                .cardLast4(request.getCardLast4())
                .build();

        Payment savedPayment = paymentRepository.save(payment);
        return convertToResponse(savedPayment);
    }

    @Transactional
    public PaymentResponse confirmPayment(Long paymentId, PaymentConfirmRequest request) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 결제 정보입니다. ID: " + paymentId));

        if (payment.getPaymentStatus() != PaymentStatus.READY) {
            throw new IllegalStateException("결제 완료 처리는 READY 상태의 결제만 가능합니다. 현재 상태: " + payment.getPaymentStatus());
        }

        payment.setPaymentStatus(PaymentStatus.PAID);
        payment.setPaidAt(LocalDateTime.now());
        
        if (request != null && request.getPgTransactionId() != null && !request.getPgTransactionId().isBlank()) {
            payment.setPgTransactionId(request.getPgTransactionId());
        } else {
            payment.setPgTransactionId("PG_MOCK_" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        }

        Payment savedPayment = paymentRepository.save(payment);
        return convertToResponse(savedPayment);
    }

    public PaymentResponse getPayment(Long paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 결제 정보입니다. ID: " + paymentId));
        return convertToResponse(payment);
    }

    public PaymentResponse getPaymentByBookingId(Long bookingId) {
        Payment payment = paymentRepository.findByBookingBookingId(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("해당 예약에 대한 결제 정보가 존재하지 않습니다. Booking ID: " + bookingId));
        return convertToResponse(payment);
    }

    @Transactional
    public PaymentResponse cancelPayment(Long paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 결제 정보입니다. ID: " + paymentId));

        payment.setPaymentStatus(PaymentStatus.CANCELLED);
        payment.setCancelledAt(LocalDateTime.now());

        Payment savedPayment = paymentRepository.save(payment);
        return convertToResponse(savedPayment);
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
