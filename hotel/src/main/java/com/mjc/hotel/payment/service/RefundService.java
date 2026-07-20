package com.mjc.hotel.payment.service;

import com.mjc.hotel.payment.dto.RefundCreateRequest;
import com.mjc.hotel.payment.dto.RefundResponse;
import com.mjc.hotel.payment.entity.Payment;
import com.mjc.hotel.payment.entity.PaymentStatus;
import com.mjc.hotel.payment.entity.Refund;
import com.mjc.hotel.payment.repository.PaymentRepository;
import com.mjc.hotel.payment.repository.RefundRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RefundService {

    private final RefundRepository refundRepository;
    private final PaymentRepository paymentRepository;

    @Transactional
    public RefundResponse requestRefund(RefundCreateRequest request) {
        Payment payment = paymentRepository.findById(request.getPaymentId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 결제 정보입니다. ID: " + request.getPaymentId()));

        if (payment.getPaymentStatus() != PaymentStatus.PAID && payment.getPaymentStatus() != PaymentStatus.PARTIAL_REFUNDED) {
            throw new IllegalStateException("환불 요청은 PAID 또는 PARTIAL_REFUNDED 상태의 결제만 가능합니다. 현재 상태: " + payment.getPaymentStatus());
        }

        boolean isFullRefund = request.getRefundAmount() == null || request.getRefundAmount().compareTo(payment.getAmount()) >= 0;
        String refundType = isFullRefund ? "CANCEL" : "PARTIAL";

        Refund refund = Refund.builder()
                .payment(payment)
                .refundAmount(isFullRefund ? payment.getAmount() : request.getRefundAmount())
                .reason(request.getRefundReason())
                .refundStatus("REQUESTED")
                .refundType(refundType)
                .build();

        if (isFullRefund) {
            payment.setPaymentStatus(PaymentStatus.REFUNDED);
        } else {
            payment.setPaymentStatus(PaymentStatus.PARTIAL_REFUNDED);
        }
        paymentRepository.save(payment);

        Refund savedRefund = refundRepository.save(refund);
        return convertToResponse(savedRefund);
    }

    @Transactional
    public RefundResponse processRefund(Long refundId) {
        Refund refund = refundRepository.findById(refundId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 환불 요청입니다. ID: " + refundId));

        refund.setRefundStatus("COMPLETED");
        refund.setRefundedAt(LocalDateTime.now());

        Payment payment = refund.getPayment();
        if (payment.getPaymentStatus() == PaymentStatus.REFUNDED) {
            payment.setCancelledAt(LocalDateTime.now());
            paymentRepository.save(payment);
        }

        Refund savedRefund = refundRepository.save(refund);
        return convertToResponse(savedRefund);
    }

    public List<RefundResponse> getRefundsByPaymentId(Long paymentId) {
        return refundRepository.findByPaymentId(paymentId).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    public RefundResponse getRefund(Long refundId) {
        Refund refund = refundRepository.findById(refundId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 환불 요청입니다. ID: " + refundId));
        return convertToResponse(refund);
    }

    private RefundResponse convertToResponse(Refund refund) {
        return RefundResponse.builder()
                .refundId(refund.getId())
                .paymentId(refund.getPayment().getId())
                .refundAmount(refund.getRefundAmount())
                .refundReason(refund.getReason())
                .refundStatus(refund.getRefundStatus())
                .requestedAt(refund.getCreatedAt())
                .completedAt(refund.getRefundedAt())
                .build();
    }
}
