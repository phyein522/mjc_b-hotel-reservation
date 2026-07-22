package com.mjc.hotel.coupon;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CouponRestController.class)
public class TestCouponRestController {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CouponService couponService;

    @Autowired
    private ObjectMapper objectMapper;

    private static CouponDto sampleCouponDto;

    @BeforeEach
    void setUp() {

        sampleCouponDto = CouponDto.builder()
                .couponId(1L)
                .code("WELCOME10")
                .name("신규회원 쿠폰")
                .description("신규회원 10% 할인")
                .discountType(CouponDiscountTypeEnum.RATE)
                .discountValue(BigDecimal.valueOf(10))
                .minOrder(BigDecimal.valueOf(30000))
                .maxDiscount(BigDecimal.valueOf(10000))
                .expirationDate(LocalDate.of(2026, 12, 31))
                .status(CouponStatusEnum.ACTIVE)
                .userId(2L)
                .build();
    }

    @Test
    @DisplayName("GET /api/coupons - 쿠폰 목록 조회")
    void getCoupons_shouldReturnOk() throws Exception {

        Page<CouponDto> page =
                new PageImpl<>(List.of(sampleCouponDto),
                        PageRequest.of(0, 10), 1);

        when(couponService.findAll(any())).thenReturn(page);

        mockMvc.perform(get("/api/coupons")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.responseData.content[0].couponId").value(1))
                .andExpect(jsonPath("$.responseData.content[0].code").value("WELCOME10"))
                .andExpect(jsonPath("$.responseData.totalElements").value(1));

        verify(couponService, times(1)).findAll(any());
    }

    @Test
    @DisplayName("GET /api/coupons/{couponId} - 쿠폰 단건 조회")
    void getCouponById_shouldReturnOk() throws Exception {

        when(couponService.findById(1L))
                .thenReturn(sampleCouponDto);

        mockMvc.perform(get("/api/coupons/{couponId}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.responseData.couponId").value(1))
                .andExpect(jsonPath("$.responseData.code").value("WELCOME10"))
                .andExpect(jsonPath("$.responseData.name").value("신규회원 쿠폰"));

        verify(couponService, times(1)).findById(1L);
    }

    @Test
    @DisplayName("POST /api/coupons - 쿠폰 등록")
    void insertCoupon_shouldReturnCreated() throws Exception {

        when(couponService.insert(any(CouponDto.class)))
                .thenReturn(sampleCouponDto);

        mockMvc.perform(post("/api/coupons")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleCouponDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.responseData.couponId").value(1))
                .andExpect(jsonPath("$.responseData.code").value("WELCOME10"))
                .andExpect(jsonPath("$.responseData.name").value("신규회원 쿠폰"));

        verify(couponService, times(1))
                .insert(any(CouponDto.class));
    }

    @Test
    @DisplayName("PATCH /api/coupons - 쿠폰 수정")
    void updateCoupon_shouldReturnOk() throws Exception {

        sampleCouponDto.setName("수정된 쿠폰");

        when(couponService.update(any(CouponDto.class)))
                .thenReturn(sampleCouponDto);

        mockMvc.perform(patch("/api/coupons")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleCouponDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.responseData.name").value("수정된 쿠폰"));

        verify(couponService, times(1))
                .update(any(CouponDto.class));
    }

    @Test
    @DisplayName("DELETE /api/coupons/{couponId}/{userId} - 쿠폰 삭제")
    void deleteCoupon_shouldReturnOk() throws Exception {

        when(couponService.deleteById(1L, 2L))
                .thenReturn(sampleCouponDto);

        mockMvc.perform(delete("/api/coupons/{couponId}/{userId}", 1L, 2L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.responseData.couponId").value(1));

        verify(couponService, times(1))
                .deleteById(1L, 2L);
    }
}