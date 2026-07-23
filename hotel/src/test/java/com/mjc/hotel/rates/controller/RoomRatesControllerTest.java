package com.mjc.hotel.rates.controller;

import tools.jackson.databind.ObjectMapper;
import com.mjc.hotel.rates.dto.request.RoomCreateRequestDto;
import com.mjc.hotel.rates.dto.request.RoomUpdateRequestDto;
import com.mjc.hotel.rates.dto.response.RoomDetailResponseDto;
import com.mjc.hotel.rates.dto.response.RoomListResponseDto;
import com.mjc.hotel.rates.service.RoomRatesService;
import com.mjc.hotel.rooms.enums.RoomBedOption;
import com.mjc.hotel.rooms.enums.RoomStatus;
import com.mjc.hotel.rooms.enums.RoomType;
import com.mjc.hotel.rooms.enums.RoomViewOption;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RoomRatesController.class)
@AutoConfigureMockMvc(addFilters = false)
class RoomRatesControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private RoomRatesService roomRatesService;

    private RoomDetailResponseDto sampleDetailResponse;

    @BeforeEach
    void setUp() {
        sampleDetailResponse = RoomDetailResponseDto.builder()
                .roomId(1L)
                .hotelId(10L)
                .name("디럭스 201호")
                .number("201")
                .floor(2)
                .size(35)
                .basePrice(BigDecimal.valueOf(120000))
                .maxAdult(2)
                .maxChild(1)
                .isActive(true)
                .roomType(RoomType.Deluxe)
                .roomStatus(RoomStatus.EnableReservation)
                .roomViewOption(RoomViewOption.CityView)
                .roomBedOption(RoomBedOption.DoubleBed)
                .description("편안한 디럭스 룸")
                .build();
    }

    @Test
    @DisplayName("[POST] JSON 호실 생성 API 성공")
    void createRoomJson_Success() throws Exception {
        RoomCreateRequestDto requestDto = RoomCreateRequestDto.builder()
                .name("디럭스 201호")
                .number("201")
                .floor(2)
                .size(35)
                .basePrice(BigDecimal.valueOf(120000))
                .maxAdult(2)
                .maxChild(1)
                .roomType(RoomType.Deluxe)
                .roomViewOption(RoomViewOption.CityView)
                .roomBedOption(RoomBedOption.DoubleBed)
                .description("편안한 디럭스 룸")
                .build();

        given(roomRatesService.createRoomWithDetails(eq(10L), any(RoomCreateRequestDto.class), any()))
                .willReturn(sampleDetailResponse);

        mockMvc.perform(post("/api/rates/hotels/10/rooms")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.responseData.roomId").value(1))
                .andExpect(jsonPath("$.responseData.number").value("201"));
    }

    @Test
    @DisplayName("[GET] 호실 상세 조회 API 성공")
    void getRoomDetail_Success() throws Exception {
        given(roomRatesService.getRoomDetail(1L)).willReturn(sampleDetailResponse);

        mockMvc.perform(get("/api/rates/rooms/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.responseData.name").value("디럭스 201호"));
    }

    @Test
    @DisplayName("[GET] 호텔 호실 목록 조회 (페이징) API 성공")
    void getRoomList_Success() throws Exception {
        RoomListResponseDto listDto = RoomListResponseDto.builder()
                .roomId(1L)
                .hotelId(10L)
                .name("디럭스 201호")
                .number("201")
                .floor(2)
                .size(35)
                .basePrice(BigDecimal.valueOf(120000))
                .maxAdult(2)
                .maxChild(1)
                .isActive(true)
                .roomType(RoomType.Deluxe)
                .roomStatus(RoomStatus.EnableReservation)
                .roomViewOption(RoomViewOption.CityView)
                .roomBedOption(RoomBedOption.DoubleBed)
                .build();

        PageImpl<RoomListResponseDto> page = new PageImpl<>(List.of(listDto), PageRequest.of(0, 10), 1);
        given(roomRatesService.getRoomList(eq(10L), any())).willReturn(page);

        mockMvc.perform(get("/api/rates/hotels/10/rooms"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.responseData.content[0].number").value("201"));
    }

    @Test
    @DisplayName("[PUT] 호실 수정 API 성공")
    void updateRoom_Success() throws Exception {
        RoomUpdateRequestDto updateDto = RoomUpdateRequestDto.builder()
                .name("수정된 디럭스 201호")
                .build();

        given(roomRatesService.updateRoom(eq(1L), any(RoomUpdateRequestDto.class)))
                .willReturn(sampleDetailResponse);

        mockMvc.perform(put("/api/rates/rooms/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("[DELETE] 호실 Soft Delete API 성공")
    void deleteRoom_Success() throws Exception {
        given(roomRatesService.deleteRoom(1L)).willReturn(true);

        mockMvc.perform(delete("/api/rates/rooms/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("호실이 성공적으로 삭제되었습니다."));
    }
}
