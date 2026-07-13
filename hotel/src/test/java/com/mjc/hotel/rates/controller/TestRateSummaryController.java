package com.mjc.hotel.rates.controller;

import com.mjc.hotel.rates.dto.RateSummaryResponseDto;
import com.mjc.hotel.rates.service.RateSummaryService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RateSummaryController.class)
public class TestRateSummaryController {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private RateSummaryService rateSummaryService;

    @Test
    @DisplayName("GET /api/rates/hotels/{hotelId}/summary - 요금 요약 정보 조회 성공")
    void getSummary_shouldReturnOk() throws Exception {
        Long hotelId = 1L;
        RateSummaryResponseDto summaryDto = RateSummaryResponseDto.builder()
                .averageBasePrice(new BigDecimal("150000.00"))
                .priceChangeRate(new BigDecimal("10.50"))
                .currentSeasonName("PEAK")
                .currentStandardWeekdayPrice(new BigDecimal("180000.00"))
                .increaseRateComparedToOffSeason(new BigDecimal("20.00"))
                .nextSeasonStartDate(LocalDate.of(2026, 9, 1))
                .nextSeasonDDay(50L)
                .nextSeasonRoomTypeCount(3)
                .build();

        when(rateSummaryService.getRateSummary(hotelId)).thenReturn(summaryDto);

        mockMvc.perform(get("/api/rates/hotels/{hotelId}/summary", hotelId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.responseCode").value("SELECT_OK"))
                .andExpect(jsonPath("$.message").value("ok"))
                .andExpect(jsonPath("$.responseData.averageBasePrice").value(150000.00))
                .andExpect(jsonPath("$.responseData.priceChangeRate").value(10.50))
                .andExpect(jsonPath("$.responseData.currentSeasonName").value("PEAK"))
                .andExpect(jsonPath("$.responseData.currentStandardWeekdayPrice").value(180000.00))
                .andExpect(jsonPath("$.responseData.increaseRateComparedToOffSeason").value(20.00))
                .andExpect(jsonPath("$.responseData.nextSeasonStartDate").value("2026-09-01"))
                .andExpect(jsonPath("$.responseData.nextSeasonDDay").value(50))
                .andExpect(jsonPath("$.responseData.nextSeasonRoomTypeCount").value(3));

        verify(rateSummaryService, times(1)).getRateSummary(hotelId);
    }
}
