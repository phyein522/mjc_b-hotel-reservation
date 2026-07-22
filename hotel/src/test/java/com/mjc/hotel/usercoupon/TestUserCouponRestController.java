package com.mjc.hotel.usercoupon;

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

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserCouponRestController.class)
public class TestUserCouponRestController {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserCouponService userCouponService;

    @Autowired
    private ObjectMapper objectMapper;

    private static UserCouponDto sampleUserCouponDto;

    @BeforeEach
    void setUp() {

        sampleUserCouponDto = UserCouponDto.builder()
                .userCouponId(1L)
                .issuedAt(LocalDateTime.now())
                .userCouponStatus(UserCouponStatusEnum.AVAILABLE)
                .usedAt(null)
                .usedPaymentId(null)
                .userId(2L)
                .couponId(3L)
                .build();
    }

    @Test
    @DisplayName("GET /api/usercoupons - 사용자 쿠폰 목록 조회")
    void getUserCoupons_shouldReturnOk() throws Exception {

        Page<UserCouponDto> page =
                new PageImpl<>(List.of(sampleUserCouponDto),
                        PageRequest.of(0, 10), 1);

        when(userCouponService.findAll(any())).thenReturn(page);

        mockMvc.perform(get("/api/usercoupons")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.responseData.content[0].userCouponId").value(1))
                .andExpect(jsonPath("$.responseData.content[0].userId").value(2))
                .andExpect(jsonPath("$.responseData.content[0].couponId").value(3))
                .andExpect(jsonPath("$.responseData.totalElements").value(1));

        verify(userCouponService, times(1)).findAll(any());
    }

    @Test
    @DisplayName("GET /api/usercoupons/{userCouponId} - 사용자 쿠폰 단건 조회")
    void getUserCouponById_shouldReturnOk() throws Exception {

        when(userCouponService.findById(1L))
                .thenReturn(sampleUserCouponDto);

        mockMvc.perform(get("/api/usercoupons/{userCouponId}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.responseData.userCouponId").value(1))
                .andExpect(jsonPath("$.responseData.userId").value(2))
                .andExpect(jsonPath("$.responseData.couponId").value(3));

        verify(userCouponService, times(1)).findById(1L);
    }

    @Test
    @DisplayName("POST /api/usercoupons - 사용자 쿠폰 등록")
    void insertUserCoupon_shouldReturnCreated() throws Exception {

        when(userCouponService.insert(any(UserCouponDto.class)))
                .thenReturn(sampleUserCouponDto);

        mockMvc.perform(post("/api/usercoupons")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleUserCouponDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.responseData.userCouponId").value(1))
                .andExpect(jsonPath("$.responseData.userId").value(2))
                .andExpect(jsonPath("$.responseData.couponId").value(3));

        verify(userCouponService, times(1))
                .insert(any(UserCouponDto.class));
    }

    @Test
    @DisplayName("PATCH /api/usercoupons - 사용자 쿠폰 수정")
    void updateUserCoupon_shouldReturnOk() throws Exception {

        sampleUserCouponDto.setUserCouponStatus(UserCouponStatusEnum.USED);

        when(userCouponService.update(any(UserCouponDto.class)))
                .thenReturn(sampleUserCouponDto);

        mockMvc.perform(patch("/api/usercoupons")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleUserCouponDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.responseData.userCouponStatus").value("USED"));

        verify(userCouponService, times(1))
                .update(any(UserCouponDto.class));
    }

    @Test
    @DisplayName("DELETE /api/usercoupons/{userCouponId}/{userId} - 사용자 쿠폰 삭제")
    void deleteUserCoupon_shouldReturnOk() throws Exception {

        when(userCouponService.deleteById(1L, 2L))
                .thenReturn(sampleUserCouponDto);

        mockMvc.perform(delete("/api/usercoupons/{userCouponId}/{userId}", 1L, 2L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.responseData.userCouponId").value(1));

        verify(userCouponService, times(1))
                .deleteById(1L, 2L);
    }
}
