package com.mjc.hotel.rates.controller;

import com.mjc.hotel.rates.dto.RatePolicyDto;
import com.mjc.hotel.rates.enums.ChildRateType;
import com.mjc.hotel.rates.service.RatePolicyService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.time.LocalTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RatePolicyController.class)
public class TestRatePolicyController {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private RatePolicyService ratePolicyService;

    @Autowired
    private ObjectMapper objectMapper;

    private RatePolicyDto sampleDto;
    private Long hotelId = 1L;

    @BeforeEach
    void setUp() {
        sampleDto = new RatePolicyDto();
        sampleDto.setPolicyId(10L);
        sampleDto.setHotelId(hotelId);
        sampleDto.setMinStayNights(2);
        sampleDto.setCheckInTime(LocalTime.of(15, 0));
        sampleDto.setCheckOutTime(LocalTime.of(11, 0));
        sampleDto.setCancelDeadlineDays(3);
        sampleDto.setCancelFeeRate(new BigDecimal("0.5"));
        sampleDto.setFreeChildAge(6);
        sampleDto.setChildRateType(ChildRateType.FREE);
        sampleDto.setChildDiscountRate(new BigDecimal("0.2"));
    }

    @Test
    @DisplayName("GET /api/rates/policies/hotels/{hotelId} - 정책 조회 성공")
    void getPolicy_shouldReturnOk() throws Exception {
        when(ratePolicyService.findByHotelId(hotelId)).thenReturn(sampleDto);

        mockMvc.perform(get("/api/rates/policies/hotels/{hotelId}", hotelId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.responseCode").value("SELECT_OK"))
                .andExpect(jsonPath("$.message").value("ok"))
                .andExpect(jsonPath("$.responseData.policyId").value(sampleDto.getPolicyId()))
                .andExpect(jsonPath("$.responseData.hotelId").value(sampleDto.getHotelId()))
                .andExpect(jsonPath("$.responseData.minStayNights").value(sampleDto.getMinStayNights()))
                .andExpect(jsonPath("$.responseData.checkInTime").value("15:00:00"))
                .andExpect(jsonPath("$.responseData.checkOutTime").value("11:00:00"));

        verify(ratePolicyService, times(1)).findByHotelId(hotelId);
    }

    @Test
    @DisplayName("PUT /api/rates/policies/hotels/{hotelId}/min-stay - 최소 투숙 기간 수정 성공")
    void updateMinStay_shouldReturnOk() throws Exception {
        when(ratePolicyService.updateMinStay(eq(hotelId), eq(3))).thenReturn(sampleDto);
        sampleDto.setMinStayNights(3);

        RatePolicyDto requestDto = new RatePolicyDto();
        requestDto.setMinStayNights(3);

        mockMvc.perform(put("/api/rates/policies/hotels/{hotelId}/min-stay", hotelId)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.responseCode").value("UPDATE_OK"))
                .andExpect(jsonPath("$.message").value("ok"))
                .andExpect(jsonPath("$.responseData.minStayNights").value(3));

        verify(ratePolicyService, times(1)).updateMinStay(eq(hotelId), eq(3));
    }

    @Test
    @DisplayName("PUT /api/rates/policies/hotels/{hotelId}/min-stay - minStayNights가 null인 경우 실패 (400)")
    void updateMinStay_shouldReturnBadRequest_whenMinStayNull() throws Exception {
        RatePolicyDto requestDto = new RatePolicyDto();
        requestDto.setMinStayNights(null);

        mockMvc.perform(put("/api/rates/policies/hotels/{hotelId}/min-stay", hotelId)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.responseCode").value("OTHER_ERROR"))
                .andExpect(jsonPath("$.message").value("bad_request"))
                .andExpect(jsonPath("$.responseData").value("최소 투숙 박수(minStayNights)는 필수입니다."));

        verifyNoInteractions(ratePolicyService);
    }

    @Test
    @DisplayName("PUT /api/rates/policies/hotels/{hotelId}/times - 체크인/아웃 시간 수정 성공")
    void updateTimes_shouldReturnOk() throws Exception {
        LocalTime newCheckIn = LocalTime.of(14, 0);
        LocalTime newCheckOut = LocalTime.of(12, 0);
        
        sampleDto.setCheckInTime(newCheckIn);
        sampleDto.setCheckOutTime(newCheckOut);

        when(ratePolicyService.updateTimes(eq(hotelId), eq(newCheckIn), eq(newCheckOut))).thenReturn(sampleDto);

        RatePolicyDto requestDto = new RatePolicyDto();
        requestDto.setCheckInTime(newCheckIn);
        requestDto.setCheckOutTime(newCheckOut);

        mockMvc.perform(put("/api/rates/policies/hotels/{hotelId}/times", hotelId)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.responseCode").value("UPDATE_OK"))
                .andExpect(jsonPath("$.message").value("ok"))
                .andExpect(jsonPath("$.responseData.checkInTime").value("14:00:00"))
                .andExpect(jsonPath("$.responseData.checkOutTime").value("12:00:00"));

        verify(ratePolicyService, times(1)).updateTimes(eq(hotelId), eq(newCheckIn), eq(newCheckOut));
    }

    @Test
    @DisplayName("PUT /api/rates/policies/hotels/{hotelId}/times - 체크인 또는 체크아웃 시간이 null인 경우 실패 (400)")
    void updateTimes_shouldReturnBadRequest_whenTimeNull() throws Exception {
        RatePolicyDto requestDto = new RatePolicyDto();
        requestDto.setCheckInTime(null);
        requestDto.setCheckOutTime(LocalTime.of(12, 0));

        mockMvc.perform(put("/api/rates/policies/hotels/{hotelId}/times", hotelId)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.responseCode").value("OTHER_ERROR"))
                .andExpect(jsonPath("$.message").value("bad_request"))
                .andExpect(jsonPath("$.responseData").value("체크인 시간 및 체크아웃 시간은 필수입니다."));

        verifyNoInteractions(ratePolicyService);
    }

    @Test
    @DisplayName("PUT /api/rates/policies/hotels/{hotelId}/cancellation - 취소 규정 수정 성공")
    void updateCancellation_shouldReturnOk() throws Exception {
        Integer newDeadline = 5;
        BigDecimal newRate = new BigDecimal("0.7");

        sampleDto.setCancelDeadlineDays(newDeadline);
        sampleDto.setCancelFeeRate(newRate);

        when(ratePolicyService.updateCancellation(eq(hotelId), eq(newDeadline), eq(newRate))).thenReturn(sampleDto);

        RatePolicyDto requestDto = new RatePolicyDto();
        requestDto.setCancelDeadlineDays(newDeadline);
        requestDto.setCancelFeeRate(newRate);

        mockMvc.perform(put("/api/rates/policies/hotels/{hotelId}/cancellation", hotelId)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.responseCode").value("UPDATE_OK"))
                .andExpect(jsonPath("$.message").value("ok"))
                .andExpect(jsonPath("$.responseData.cancelDeadlineDays").value(newDeadline))
                .andExpect(jsonPath("$.responseData.cancelFeeRate").value(newRate.doubleValue()));

        verify(ratePolicyService, times(1)).updateCancellation(eq(hotelId), eq(newDeadline), eq(newRate));
    }

    @Test
    @DisplayName("PUT /api/rates/policies/hotels/{hotelId}/cancellation - 취소 데드라인 또는 취소율이 null인 경우 실패 (400)")
    void updateCancellation_shouldReturnBadRequest_whenCancellationNull() throws Exception {
        RatePolicyDto requestDto = new RatePolicyDto();
        requestDto.setCancelDeadlineDays(null);
        requestDto.setCancelFeeRate(new BigDecimal("0.5"));

        mockMvc.perform(put("/api/rates/policies/hotels/{hotelId}/cancellation", hotelId)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.responseCode").value("OTHER_ERROR"))
                .andExpect(jsonPath("$.message").value("bad_request"))
                .andExpect(jsonPath("$.responseData").value("취소 기한 및 취소 수수료율은 필수입니다."));

        verifyNoInteractions(ratePolicyService);
    }

    @Test
    @DisplayName("PUT /api/rates/policies/hotels/{hotelId}/child-rates - 아동 요금 수정 성공")
    void updateChildRates_shouldReturnOk() throws Exception {
        Integer freeChildAge = 5;
        ChildRateType childRateType = ChildRateType.DISCOUNT;
        BigDecimal discountRate = new BigDecimal("0.3");

        sampleDto.setFreeChildAge(freeChildAge);
        sampleDto.setChildRateType(childRateType);
        sampleDto.setChildDiscountRate(discountRate);

        when(ratePolicyService.updateChildRates(eq(hotelId), eq(freeChildAge), eq(childRateType), eq(discountRate))).thenReturn(sampleDto);

        RatePolicyDto requestDto = new RatePolicyDto();
        requestDto.setFreeChildAge(freeChildAge);
        requestDto.setChildRateType(childRateType);
        requestDto.setChildDiscountRate(discountRate);

        mockMvc.perform(put("/api/rates/policies/hotels/{hotelId}/child-rates", hotelId)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.responseCode").value("UPDATE_OK"))
                .andExpect(jsonPath("$.message").value("ok"))
                .andExpect(jsonPath("$.responseData.freeChildAge").value(freeChildAge))
                .andExpect(jsonPath("$.responseData.childRateType").value(childRateType.toString()))
                .andExpect(jsonPath("$.responseData.childDiscountRate").value(discountRate.doubleValue()));

        verify(ratePolicyService, times(1)).updateChildRates(eq(hotelId), eq(freeChildAge), eq(childRateType), eq(discountRate));
    }

    @Test
    @DisplayName("PUT /api/rates/policies/hotels/{hotelId}/child-rates - 무료 아동 나이 또는 아동 요금 유형이 null인 경우 실패 (400)")
    void updateChildRates_shouldReturnBadRequest_whenChildRateNull() throws Exception {
        RatePolicyDto requestDto = new RatePolicyDto();
        requestDto.setFreeChildAge(null);
        requestDto.setChildRateType(ChildRateType.DISCOUNT);

        mockMvc.perform(put("/api/rates/policies/hotels/{hotelId}/child-rates", hotelId)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.responseCode").value("OTHER_ERROR"))
                .andExpect(jsonPath("$.message").value("bad_request"))
                .andExpect(jsonPath("$.responseData").value("무료 아동 나이 및 아동 요금 유형은 필수입니다."));

        verifyNoInteractions(ratePolicyService);
    }
}
