package com.mjc.hotel.payment.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mjc.hotel.bookings.BookingDto;
import com.mjc.hotel.bookings.BookingEntity;
import com.mjc.hotel.bookings.BookingRepository;
import com.mjc.hotel.payment.dto.*;
import com.mjc.hotel.payment.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final BookingRepository bookingRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final HttpClient httpClient = HttpClient.newHttpClient();

    @Value("${toss.payments.secret-key:}")
    private String tossSecretKey;

    public List<PaymentEntity> getPayments() {
        return paymentRepository.findAll();
    }

    public PaymentDto getPayment(Long paymentId) {
        PaymentEntity result = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 결제입니다. paymentId=" + paymentId));
        return (PaymentDto) new PaymentDto().copyMembers(result, true);
    }

    @Transactional
    public PaymentDto savePayment(PaymentDto dto) {
        PaymentEntity entity = (PaymentEntity) new PaymentEntity().copyMembers(dto, true);
        PaymentEntity result = paymentRepository.save(entity);
        return (PaymentDto) new PaymentDto().copyMembers(result, true);
    }

//    @Transactional
//    public TossPaymentReadyResponseDto readyTossPayment(TossPaymentReadyRequestDto dto) {
//        BookingDto booking = getBooking(dto.getBookingId());
//        BigDecimal amount = requirePositiveAmount(dto.getAmount());
//
////        BigDecimal bookingAmount = BigDecimal.valueOf(booking.getTotalAmount());
////        if (bookingAmount.compareTo(amount) != 0) {
////            throw new IllegalArgumentException("예약 금액과 결제 금액이 일치하지 않습니다.");
////        }
//
//        String orderId = createTossOrderId(booking.getBookingId());
//        String orderName = normalizeOrderName(dto.getOrderName(), booking);
//
//        PaymentEntity payment = PaymentEntity.builder()
//                .bookingId(booking.getBookingId())
//                .totalAmount(amount)
//                .orderId(orderId)
//                .paymentMethod(PaymentMethod.CreditCard)
//                .paymentStatus(PaymentStatus.Ready)
//                .provider("TOSS")
//                .build();
//
//        PaymentEntity saved = paymentRepository.save(payment);
//
//        return TossPaymentReadyResponseDto.builder()
//                .paymentId(saved.getPaymentId())
//                .bookingId(booking.getBookingId())
//                .orderId(orderId)
//                .orderName(orderName)
//                .amount(amount)
//                .build();
//    }

    @Transactional
    public PaymentDto confirmTossPayment(TossPaymentConfirmRequestDto dto) {
        PaymentEntity payment = paymentRepository.findByOrderId(dto.getOrderId())
                .orElseThrow(() -> new IllegalArgumentException("결제 요청 정보를 찾을 수 없습니다. orderId=" + dto.getOrderId()));

        if (!payment.getBooking().getBookingNo().equals(dto.getBookingId())) {
            throw new IllegalArgumentException("예약 정보와 결제 요청 정보가 일치하지 않습니다.");
        }

        BigDecimal amount = requirePositiveAmount(dto.getAmount());
        if (payment.getTotalAmount().compareTo(amount) != 0) {
            throw new IllegalArgumentException("결제 요청 금액과 승인 금액이 일치하지 않습니다.");
        }

        JsonNode tossResponse = requestTossConfirm(dto);
        PaymentDto result = (PaymentDto) new PaymentDto().copyMembers(payment, true);
        String approvedAt = text(tossResponse, "approvedAt");
        String receiptUrl = tossResponse.path("receipt").path("url").asText(null);

        result.setPaymentKey(text(tossResponse, "paymentKey"));
        result.setPgTransactionId(text(tossResponse, "paymentKey"));
        result.setPaymentMethod(resolvePaymentMethod(text(tossResponse, "method")));
        result.setPaymentStatus(PaymentStatus.Paid);
        result.setReceiptUrl(receiptUrl);
        result.setApprovedAt(parseTossDateTime(approvedAt));
//        result.setPaidAt(payment.getApprovedAt() != null ? payment.getApprovedAt() : LocalDateTime.now());
//        result.setPoint((int) Math.floor(amount.doubleValue() * 0.005));

        return result;
    }

    @Transactional
    public PaymentDto failTossPayment(TossPaymentFailRequestDto dto) {
        PaymentEntity payment = paymentRepository.findByOrderId(dto.getOrderId())
                .orElseThrow(() -> new IllegalArgumentException("결제 요청 정보를 찾을 수 없습니다. orderId=" + dto.getOrderId()));

        PaymentDto result = (PaymentDto) new PaymentDto().copyMembers(payment, true);
        result.setPaymentStatus(PaymentStatus.Failed);
        result.setFailCode(dto.getCode());
        result.setFailMessage(dto.getMessage());

        return result;
    }

    @Transactional
    public String refundPayment(PaymentEntity payment, BigDecimal refundAmount, String reason) {
        if (payment == null) {
            throw new IllegalArgumentException("환불할 결제 정보가 없습니다.");
        }

        if (refundAmount == null || refundAmount.compareTo(BigDecimal.ZERO) <= 0) {
            return null;
        }

        if (!"TOSS".equalsIgnoreCase(payment.getProvider())) {
            throw new IllegalStateException("토스 결제가 아니어서 자동 환불할 수 없습니다. provider=" + payment.getProvider());
        }

        if (payment.getPaymentKey() == null || payment.getPaymentKey().isBlank()) {
            throw new IllegalStateException("토스 결제 취소에 필요한 paymentKey가 없습니다.");
        }

        JsonNode cancelResponse = requestTossCancel(payment.getPaymentKey(), refundAmount, reason);
        String pgTransactionKey = extractCancelTransactionKey(cancelResponse);

        BigDecimal paidAmount = payment.getTotalAmount() != null ? payment.getTotalAmount() : BigDecimal.ZERO;
        if (refundAmount.compareTo(paidAmount) >= 0) {
            payment.setPaymentStatus(PaymentStatus.Refunded);
        } else {
            payment.setPaymentStatus(PaymentStatus.PartialRefunced);
        }

        return pgTransactionKey;
    }

    @Transactional
    public PaymentDto updatePayment(Long paymentId, PaymentDto dto) {
        PaymentDto find = getPayment(paymentId);
        if (dto.getBookingId() != null) {
            find.setBooking(getBooking(dto.getBookingId()));
        }
        find.copyMembers(dto, false);
        PaymentEntity update = (PaymentEntity) new PaymentEntity().copyMembers(find, true);
        PaymentEntity result = this.paymentRepository.save(update);
        return (PaymentDto) new PaymentDto().copyMembers(result, true);
    }

    @Transactional
    public PaymentDto deletePayment(Long paymentId) {
        PaymentDto payment = getPayment(paymentId);
        this.paymentRepository.deleteById(paymentId);
        return payment;
    }

    private BookingDto getBooking(Long bookingId) {
        BookingEntity result = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 예약입니다. bookingId=" + bookingId));
        return (BookingDto) new BookingDto().copyMembers(result, true);
    }

    private BigDecimal requirePositiveAmount(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("결제 금액이 올바르지 않습니다.");
        }
        return amount;
    }

    private String createTossOrderId(Long bookingId) {
        return "TOSS-" + bookingId + "-" + UUID.randomUUID().toString().replace("-", "").substring(0, 18);
    }

    private String normalizeOrderName(String orderName, BookingDto booking) {
        if (orderName != null && !orderName.isBlank()) {
            return orderName.trim();
        }
        return booking.getRoom().getHotel().getName() + " " + booking.getRoom().getName();
    }

    private JsonNode requestTossConfirm(TossPaymentConfirmRequestDto dto) {
        if (tossSecretKey == null || tossSecretKey.isBlank()) {
            throw new IllegalStateException("토스 시크릿 키가 설정되지 않았습니다.");
        }

        try {
            String requestBody = objectMapper.writeValueAsString(Map.of(
                    "paymentKey", dto.getPaymentKey(),
                    "orderId", dto.getOrderId(),
                    "amount", dto.getAmount().longValue()
            ));
            String basicToken = Base64.getEncoder()
                    .encodeToString((tossSecretKey + ":").getBytes(StandardCharsets.UTF_8));

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://api.tosspayments.com/v1/payments/confirm"))
                    .header("Authorization", "Basic " + basicToken)
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            JsonNode body = objectMapper.readTree(response.body());

            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                throw new IllegalStateException(body.path("message").asText("토스 결제 승인에 실패했습니다."));
            }

            return body;
        } catch (IllegalStateException e) {
            throw e;
        } catch (Exception e) {
            throw new IllegalStateException("토스 결제 승인 요청 중 오류가 발생했습니다.", e);
        }
    }

    private JsonNode requestTossCancel(String paymentKey, BigDecimal refundAmount, String reason) {
        if (tossSecretKey == null || tossSecretKey.isBlank()) {
            throw new IllegalStateException("토스 시크릿 키가 설정되지 않았습니다.");
        }

        try {
            String requestBody = objectMapper.writeValueAsString(Map.of(
                    "cancelReason", reason != null && !reason.isBlank() ? reason : "예약 취소",
                    "cancelAmount", refundAmount.longValue()
            ));
            String basicToken = Base64.getEncoder()
                    .encodeToString((tossSecretKey + ":").getBytes(StandardCharsets.UTF_8));
            String encodedPaymentKey = URLEncoder.encode(paymentKey, StandardCharsets.UTF_8);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://api.tosspayments.com/v1/payments/" + encodedPaymentKey + "/cancel"))
                    .header("Authorization", "Basic " + basicToken)
                    .header("Content-Type", "application/json")
                    .header("Idempotency-Key", "refund-" + paymentKey + "-" + refundAmount.longValue())
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            JsonNode body = objectMapper.readTree(response.body());

            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                throw new IllegalStateException(body.path("message").asText("토스 결제 취소에 실패했습니다."));
            }

            return body;
        } catch (IllegalStateException e) {
            throw e;
        } catch (Exception e) {
            throw new IllegalStateException("토스 결제 취소 요청 중 오류가 발생했습니다.", e);
        }
    }

    private String extractCancelTransactionKey(JsonNode cancelResponse) {
        JsonNode cancels = cancelResponse.path("cancels");
        if (cancels.isArray() && !cancels.isEmpty()) {
            JsonNode lastCancel = cancels.get(cancels.size() - 1);
            String transactionKey = lastCancel.path("transactionKey").asText(null);
            if (transactionKey != null && !transactionKey.isBlank()) {
                return transactionKey;
            }
        }

        return text(cancelResponse, "paymentKey");
    }

    private PaymentMethod resolvePaymentMethod(String method) {
        if (method == null) {
            return PaymentMethod.Online;
        }

        if (method.contains("카드") || method.equalsIgnoreCase("CARD")) {
            return PaymentMethod.CreditCard;
        }

        if (method.contains("계좌") || method.contains("이체")) {
            return PaymentMethod.BankTransfer;
        }

        return PaymentMethod.Online;
    }

    private String text(JsonNode node, String fieldName) {
        JsonNode value = node.path(fieldName);
        return value.isMissingNode() || value.isNull() ? null : value.asText();
    }

    private LocalDateTime parseTossDateTime(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }

        try {
            return OffsetDateTime.parse(value).toLocalDateTime();
        } catch (Exception e) {
            return LocalDateTime.now();
        }
    }
}
