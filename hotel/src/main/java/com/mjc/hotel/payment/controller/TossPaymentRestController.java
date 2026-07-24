package com.mjc.hotel.payment.controller;

import com.mjc.hotel.common.ApiResponse;
import com.mjc.hotel.common.ResponseCode;
import com.mjc.hotel.payment.dto.*;
import com.mjc.hotel.payment.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payment")
public class TossPaymentRestController {
    @Autowired
    private PaymentService paymentService;

    @PostMapping
    public ResponseEntity<ApiResponse<PaymentDto>> insert(@RequestBody PaymentDto dto) {
        PaymentDto insert = (PaymentDto) new PaymentDto().copyMembers(paymentService.savePayment(dto), true);
        return ResponseEntity.status(201).body(
                new ApiResponse<>(ResponseCode.SUCCESS, "payments insert success", insert)
        );
    }

//    @PostMapping("/toss/ready")
//    public ResponseEntity<ApiResponse<TossPaymentReadyResponseDto>> readyTossPayment(@RequestBody TossPaymentReadyRequestDto dto) {
//        return ResponseEntity.status(201).body(
//                new ApiResponse<>(ResponseCode.SUCCESS, "toss payment ready success", paymentService.readyTossPayment(dto))
//        );
//    }

    @Operation(
            summary = "토스 결제 승인",
            description = "토스 결제 성공 후 paymentKey, orderId, amount를 검증하고 승인합니다."
    )
    @PostMapping("/toss/confirm")
    public ResponseEntity<ApiResponse<PaymentDto>> confirmTossPayment(@RequestBody TossPaymentConfirmRequestDto dto) {
        PaymentDto result = paymentService.confirmTossPayment(dto);
        return ResponseEntity.ok(
                new ApiResponse<>(ResponseCode.SUCCESS, "toss payment confirm success", result)
        );
    }

    @Operation(
            summary = "토스 결제 실패 기록",
            description = "토스 결제 실패 또는 취소 정보를 저장합니다."
    )
    @PostMapping("/toss/fail")
    public ResponseEntity<ApiResponse<PaymentDto>> failTossPayment(@RequestBody TossPaymentFailRequestDto dto) {
        PaymentDto result = paymentService.failTossPayment(dto);
        return ResponseEntity.ok(
                new ApiResponse<>(ResponseCode.SUCCESS, "toss payment fail saved", result)
        );
    }

    @Operation(
            summary = "결제 전체 데이터 조회",
            description = "결제 전체 데이터를 조회합니다."
    )
    @GetMapping
    public ResponseEntity<ApiResponse<List<PaymentDto>>> getPayments() {
        List<PaymentDto> payments = paymentService.getPayments().stream()
                .map(x -> (PaymentDto) new PaymentDto().copyMembers(x, true) )
                .toList();

        return ResponseEntity.ok(
                new ApiResponse<>(ResponseCode.SUCCESS, "payments select success", payments)
        );
    }

    @GetMapping("/{paymentId}")
    public ResponseEntity<ApiResponse<PaymentDto>> getPayment(@PathVariable Long paymentId) {
        PaymentDto result = paymentService.getPayment(paymentId);
        return ResponseEntity.ok(
                new ApiResponse<>(ResponseCode.SUCCESS, "payments select success", result)
        );
    }

    @PatchMapping
    public ResponseEntity<ApiResponse<PaymentDto>> update(
            @RequestBody PaymentDto dto
    ) {
        PaymentDto result = paymentService.updatePayment(dto.getPaymentId(), dto);
        return ResponseEntity.ok(
                new ApiResponse<>(ResponseCode.SUCCESS, "payments update success", result)
        );
    }

    @Operation(
            summary = "결제 데이터 삭제",
            description = "결제 데이터를 삭제합니다."
    )
    @DeleteMapping("/{paymentId}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long paymentId) {
        paymentService.deletePayment(paymentId);
        return ResponseEntity.ok(
                new ApiResponse<>(ResponseCode.SUCCESS, "payments delete success", null)
        );
    }
}
