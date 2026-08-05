package com.mjc.hotel.payment.controller;

import com.mjc.hotel.payment.dto.*;
import com.mjc.hotel.payment.service.TossPaymentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * TossPaymentRestController의 모든 URL Mapping 메소드에 대한 단위 테스트
 *
 * @WebMvcTest를 사용하여 웹 계층만 슬라이스 테스트하고,
 * PaymentService는 @MockitoBean으로 목(mock) 처리한다.
 *
 * ApiResponse의 실제 페이로드 필드명은 "responseData"이다.
 */
@WebMvcTest(TossPaymentRestController.class)
public class TestTossPaymentRestController {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TossPaymentService tossPaymentService;

    @Autowired
    private ObjectMapper objectMapper;

    private PaymentDto samplePaymentDto;

    @BeforeEach
    void setUp() {
        // 단위 테스트에 사용할 기본 샘플 데이터 설정
        samplePaymentDto = new PaymentDto();
        samplePaymentDto.setPaymentId(1L);
        samplePaymentDto.setOrderId("ORDER_20260724_001");
        samplePaymentDto.setPaymentKey("toss_pk_sample_key");
        samplePaymentDto.setTotalAmount(new BigDecimal("50000"));
        samplePaymentDto.setPaymentStatus(PaymentStatus.Ready);
    }

    @Test
    @DisplayName("POST /api/payment - 결제 정보 등록 성공")
    void insert_shouldReturnCreated() throws Exception {
        // given
        when(tossPaymentService.insertPayment(any(PaymentDto.class))).thenReturn(samplePaymentDto);

        // when & then
        mockMvc.perform(post("/api/payment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(samplePaymentDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.responseData.paymentId").value(samplePaymentDto.getPaymentId()))
                .andExpect(jsonPath("$.responseData.orderId").value(samplePaymentDto.getOrderId()))
                .andExpect(jsonPath("$.responseData.paymentKey").value(samplePaymentDto.getPaymentKey()))
                .andExpect(jsonPath("$.responseData.totalAmount").value(samplePaymentDto.getTotalAmount()))
                .andExpect(jsonPath("$.responseData.paymentStatus").value(samplePaymentDto.getPaymentStatus().toString()));

        verify(tossPaymentService, times(1)).insertPayment(any(PaymentDto.class));
    }

    @Test
    @DisplayName("POST /api/payment/toss/confirm - 토스 결제 승인 성공")
    void confirmTossPayment_shouldReturnOk() throws Exception {
        // given
        TossPaymentConfirmRequestDto confirmRequestDto = new TossPaymentConfirmRequestDto();
        confirmRequestDto.setPaymentKey("toss_pk_sample_key");
        confirmRequestDto.setOrderId("ORDER_20260724_001");
        confirmRequestDto.setAmount(new BigDecimal("50000"));

        when(tossPaymentService.confirmTossPayment(any(TossPaymentConfirmRequestDto.class))).thenReturn(samplePaymentDto);

        // when & then
        mockMvc.perform(post("/api/payment/toss/confirm")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(confirmRequestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.responseData.paymentId").value(samplePaymentDto.getPaymentId()))
                .andExpect(jsonPath("$.responseData.orderId").value(samplePaymentDto.getOrderId()))
                .andExpect(jsonPath("$.responseData.paymentKey").value(samplePaymentDto.getPaymentKey()))
                .andExpect(jsonPath("$.responseData.totalAmount").value(samplePaymentDto.getTotalAmount()));

        verify(tossPaymentService, times(1)).confirmTossPayment(any(TossPaymentConfirmRequestDto.class));
    }

    @Test
    @DisplayName("POST /api/payment/toss/fail - 토스 결제 실패 기록 성공")
    void failTossPayment_shouldReturnOk() throws Exception {
        // given
        TossPaymentFailRequestDto failRequestDto = new TossPaymentFailRequestDto();
        failRequestDto.setOrderId("ORDER_20260724_001");
        failRequestDto.setCode("PAY_PROCESS_FAILED");
        failRequestDto.setMessage("잔액 부족으로 결제에 실패했습니다.");

        samplePaymentDto.setPaymentStatus(PaymentStatus.Failed);
        when(tossPaymentService.failTossPayment(any(TossPaymentFailRequestDto.class))).thenReturn(samplePaymentDto);

        // when & then
        mockMvc.perform(post("/api/payment/toss/fail")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(failRequestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.responseData.paymentId").value(samplePaymentDto.getPaymentId()))
                .andExpect(jsonPath("$.responseData.paymentStatus").value(PaymentStatus.Failed.toString()));

        verify(tossPaymentService, times(1)).failTossPayment(any(TossPaymentFailRequestDto.class));
    }

    @Test
    @DisplayName("GET /api/payment - 결제 전체 목록 조회 성공")
    void getPayments_shouldReturnOk() throws Exception {
        // given
        PaymentEntity paymentEntity = (PaymentEntity) new PaymentEntity().copyMembers(samplePaymentDto, true);
        when(tossPaymentService.getPayments()).thenReturn(List.of(paymentEntity));

        // when & then
        mockMvc.perform(get("/api/payment"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.responseData[0].paymentId").value(samplePaymentDto.getPaymentId()))
                .andExpect(jsonPath("$.responseData[0].orderId").value(samplePaymentDto.getOrderId()))
                .andExpect(jsonPath("$.responseData[0].totalAmount").value(samplePaymentDto.getTotalAmount()));

        verify(tossPaymentService, times(1)).getPayments();
    }

    @Test
    @DisplayName("GET /api/payment/{paymentId} - 결제 단건 조회 성공")
    void getPayment_shouldReturnOk() throws Exception {
        // given
        when(tossPaymentService.getPayment(1L)).thenReturn(samplePaymentDto);

        // when & then
        mockMvc.perform(get("/api/payment/{paymentId}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.responseData.paymentId").value(samplePaymentDto.getPaymentId()))
                .andExpect(jsonPath("$.responseData.orderId").value(samplePaymentDto.getOrderId()))
                .andExpect(jsonPath("$.responseData.paymentKey").value(samplePaymentDto.getPaymentKey()));

        verify(tossPaymentService, times(1)).getPayment(1L);
    }

    @Test
    @DisplayName("PATCH /api/payment - 결제 데이터 수정 성공")
    void update_shouldReturnOk() throws Exception {
        // given
        samplePaymentDto.setTotalAmount(new BigDecimal("55000"));
        when(tossPaymentService.updatePayment(eq(1L), any(PaymentDto.class))).thenReturn(samplePaymentDto);

        // when & then
        mockMvc.perform(patch("/api/payment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(samplePaymentDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.responseCode").exists())
                .andExpect(jsonPath("$.responseData.paymentId").value(1L))
                .andExpect(jsonPath("$.responseData.totalAmount").value(new BigDecimal("55000")));

        verify(tossPaymentService, times(1)).updatePayment(eq(1L), any(PaymentDto.class));
    }

    @Test
    @DisplayName("DELETE /api/payment/{paymentId} - 결제 데이터 삭제 성공")
    void delete_shouldReturnOk() throws Exception {
        // given
        when(tossPaymentService.deletePayment(eq(1L))).thenReturn(samplePaymentDto);

        // when & then
        mockMvc.perform(delete("/api/payment/{paymentId}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.responseCode").exists())
                .andExpect(jsonPath("$.responseData.paymentId").value(1L));

        verify(tossPaymentService, times(1)).deletePayment(1L);
    }
}