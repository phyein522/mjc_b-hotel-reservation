package com.mjc.hotel.rates.controller;

import com.mjc.hotel.rates.dto.PricePreviewDto;
import com.mjc.hotel.rates.dto.SeasonRateDto;
import com.mjc.hotel.rooms.enums.RoomType;
import com.mjc.hotel.rates.enums.SeasonStatus;
import com.mjc.hotel.rates.service.SeasonRateService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(SeasonRateController.class)
public class TestSeasonRateController {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private SeasonRateService seasonRateService;

    @Autowired
    private ObjectMapper objectMapper;

    private SeasonRateDto sampleDto;
    private Long hotelId = 1L;

    @BeforeEach
    void setUp() {
        sampleDto = new SeasonRateDto();
        sampleDto.setSeasonRateId(100L);
        sampleDto.setSeasonName("Summer Season");
        sampleDto.setStartDate(LocalDate.of(2026, 7, 1));
        sampleDto.setEndDate(LocalDate.of(2026, 8, 31));
        sampleDto.setWeekdayPrice(new BigDecimal("150000.00"));
        sampleDto.setWeekendPrice(new BigDecimal("180000.00"));
        sampleDto.setStatus(SeasonStatus.ONGOING);
        sampleDto.setMinStayNights(1);
        sampleDto.setWeekdayPolicyEnabled(true);
        sampleDto.setRoomType(RoomType.Standard);
        sampleDto.setHotelId(hotelId);
        sampleDto.setPolicyId(10L);
    }

    @Test
    @DisplayName("GET /api/rates/hotels/{hotelId}/rooms/{roomType} - 객실 타입별 시즌 요금 조회 성공")
    void getSeasons_shouldReturnOk() throws Exception {
        when(seasonRateService.getSeasonsByHotelAndRoomType(hotelId, RoomType.Standard))
                .thenReturn(List.of(sampleDto));

        mockMvc.perform(get("/api/rates/hotels/{hotelId}/rooms/{roomType}", hotelId, "standard"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.responseCode").value("SELECT_OK"))
                .andExpect(jsonPath("$.message").value("ok"))
                .andExpect(jsonPath("$.responseData[0].seasonRateId").value(sampleDto.getSeasonRateId()))
                .andExpect(jsonPath("$.responseData[0].seasonName").value(sampleDto.getSeasonName()))
                .andExpect(jsonPath("$.responseData[0].roomType").value("Standard"));

        verify(seasonRateService, times(1)).getSeasonsByHotelAndRoomType(hotelId, RoomType.Standard);
    }

    @Test
    @DisplayName("GET /api/rates/hotels/{hotelId}/rooms/{roomType} - 잘못된 객실 유형인 경우 400 에러")
    void getSeasons_shouldReturnBadRequest_whenRoomTypeInvalid() throws Exception {
        mockMvc.perform(get("/api/rates/hotels/{hotelId}/rooms/{roomType}", hotelId, "invalid_room_type"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.responseCode").value("OTHER_ERROR"))
                .andExpect(jsonPath("$.message").value("bad_request"))
                .andExpect(jsonPath("$.responseData").value("지원하지 않는 객실 유형입니다: invalid_room_type"));

        verifyNoInteractions(seasonRateService);
    }

    @Test
    @DisplayName("POST /api/rates - 시즌 요금 추가 성공")
    void insertSeason_shouldReturnCreated() throws Exception {
        BigDecimal multiplier = new BigDecimal("10.0");
        when(seasonRateService.createSeason(any(SeasonRateDto.class), eq(multiplier)))
                .thenReturn(sampleDto);

        mockMvc.perform(post("/api/rates")
                        .param("multiplier", "10.0")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(sampleDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.responseCode").value("INSERT_OK"))
                .andExpect(jsonPath("$.message").value("ok"))
                .andExpect(jsonPath("$.responseData.seasonRateId").value(sampleDto.getSeasonRateId()));

        verify(seasonRateService, times(1)).createSeason(any(SeasonRateDto.class), eq(multiplier));
    }

    @Test
    @DisplayName("PUT /api/rates/{seasonRateId} - 시즌 요금 수정 성공")
    void updateSeason_shouldReturnOk() throws Exception {
        Long seasonRateId = 100L;
        BigDecimal multiplier = new BigDecimal("15.0");
        when(seasonRateService.updateSeason(eq(seasonRateId), any(SeasonRateDto.class), eq(multiplier)))
                .thenReturn(sampleDto);

        mockMvc.perform(put("/api/rates/{seasonRateId}", seasonRateId)
                        .param("multiplier", "15.0")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(sampleDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.responseCode").value("UPDATE_OK"))
                .andExpect(jsonPath("$.message").value("ok"))
                .andExpect(jsonPath("$.responseData.seasonRateId").value(sampleDto.getSeasonRateId()));

        verify(seasonRateService, times(1)).updateSeason(eq(seasonRateId), any(SeasonRateDto.class), eq(multiplier));
    }

    @Test
    @DisplayName("DELETE /api/rates/{seasonRateId} - 시즌 요금 삭제 성공")
    void deleteSeason_shouldReturnOk() throws Exception {
        Long seasonRateId = 100L;
        doNothing().when(seasonRateService).deleteSeason(seasonRateId);

        mockMvc.perform(delete("/api/rates/{seasonRateId}", seasonRateId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.responseCode").value("DELETE_OK"))
                .andExpect(jsonPath("$.message").value("ok"));

        verify(seasonRateService, times(1)).deleteSeason(seasonRateId);
    }

    @Test
    @DisplayName("POST /api/rates/preview - 요금 미리보기 성공")
    void getPreview_shouldReturnOk() throws Exception {
        PricePreviewDto previewDto = PricePreviewDto.builder()
                .weekdayPrice(new BigDecimal("100000.00"))
                .weekendPrice(new BigDecimal("120000.00"))
                .weekdayPolicyEnabled(true)
                .multiplier(new BigDecimal("20.0"))
                .build();

        PricePreviewDto resultDto = PricePreviewDto.builder()
                .weekdayPrice(new BigDecimal("100000.00"))
                .weekendPrice(new BigDecimal("120000.00"))
                .weekdayPolicyEnabled(true)
                .multiplier(new BigDecimal("20.0"))
                .calculatedWeekdayPrice(new BigDecimal("120000.00"))
                .calculatedWeekendPrice(new BigDecimal("144000.00"))
                .increaseRate(new BigDecimal("20.00"))
                .build();

        when(seasonRateService.calculatePreview(any(PricePreviewDto.class))).thenReturn(resultDto);

        mockMvc.perform(post("/api/rates/preview")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(previewDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.responseCode").value("SELECT_OK"))
                .andExpect(jsonPath("$.message").value("ok"))
                .andExpect(jsonPath("$.responseData.calculatedWeekdayPrice").value(120000.00))
                .andExpect(jsonPath("$.responseData.calculatedWeekendPrice").value(144000.00))
                .andExpect(jsonPath("$.responseData.increaseRate").value(20.00));

        verify(seasonRateService, times(1)).calculatePreview(any(PricePreviewDto.class));
    }
}
