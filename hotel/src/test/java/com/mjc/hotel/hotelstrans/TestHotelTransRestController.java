package com.mjc.hotel.hotelstrans;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(HotelTransRestController.class)
public class TestHotelTransRestController {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private HotelTransService hotelTransService;

	@Autowired
	private ObjectMapper objectMapper;

	private static HotelTransDto SampleHotelTransDto;

	@BeforeEach
	void setUp() {
		SampleHotelTransDto = HotelTransDto.builder()
				.transId(1L)
				.name("공항 셔틀")
				.time("09:00")
				.depart("인천공항 T1")
				.hotelId(10L)
				.build();
	}

	@Test
	@DisplayName("GET /api/hoteltrans - 호텔 교통 목록 조회 성공")
	void getTrans_shouldReturnOk() throws Exception {
		when(hotelTransService.findAll()).thenReturn(List.of(SampleHotelTransDto));

		mockMvc.perform(get("/api/hoteltrans"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[0].transId").value(SampleHotelTransDto.getTransId()))
				.andExpect(jsonPath("$[0].name").value(SampleHotelTransDto.getName()))
				.andExpect(jsonPath("$[0].time").value(SampleHotelTransDto.getTime()))
				.andExpect(jsonPath("$[0].depart").value(SampleHotelTransDto.getDepart()))
				.andExpect(jsonPath("$[0].hotelId").value(SampleHotelTransDto.getHotelId()));

		verify(hotelTransService, times(1)).findAll();
	}

	@Test
	@DisplayName("GET /api/hoteltrans/{transId} - 호텔 교통 단건 조회 성공")
	void getTransById_shouldReturnOk() throws Exception {
		when(hotelTransService.findById(1L)).thenReturn(SampleHotelTransDto);

		mockMvc.perform(get("/api/hoteltrans/{transId}", 1L))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.transId").value(SampleHotelTransDto.getTransId()))
				.andExpect(jsonPath("$.name").value(SampleHotelTransDto.getName()))
				.andExpect(jsonPath("$.time").value(SampleHotelTransDto.getTime()))
				.andExpect(jsonPath("$.depart").value(SampleHotelTransDto.getDepart()))
				.andExpect(jsonPath("$.hotelId").value(SampleHotelTransDto.getHotelId()));

		verify(hotelTransService, times(1)).findById(1L);
	}

	@Test
	@DisplayName("POST /api/hoteltrans - 호텔 교통 등록 성공")
	void insertTrans_shouldReturnOk() throws Exception {
		when(hotelTransService.insert(any(HotelTransDto.class))).thenReturn(SampleHotelTransDto);

		mockMvc.perform(post("/api/hoteltrans")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(SampleHotelTransDto)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.transId").value(SampleHotelTransDto.getTransId()))
				.andExpect(jsonPath("$.name").value(SampleHotelTransDto.getName()))
				.andExpect(jsonPath("$.time").value(SampleHotelTransDto.getTime()))
				.andExpect(jsonPath("$.depart").value(SampleHotelTransDto.getDepart()))
				.andExpect(jsonPath("$.hotelId").value(SampleHotelTransDto.getHotelId()));

		verify(hotelTransService, times(1)).insert(any(HotelTransDto.class));
	}

	@Test
	@DisplayName("PUT /api/hoteltrans/{transId} - 호텔 교통 수정 성공")
	void updateTrans_shouldReturnOk() throws Exception {
		SampleHotelTransDto.setTime("10:00");
		when(hotelTransService.update(eq(1L), any(HotelTransDto.class))).thenReturn(SampleHotelTransDto);

		mockMvc.perform(put("/api/hoteltrans/{transId}", 1L)
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(SampleHotelTransDto)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.transId").value(SampleHotelTransDto.getTransId()))
				.andExpect(jsonPath("$.name").value(SampleHotelTransDto.getName()))
				.andExpect(jsonPath("$.time").value(SampleHotelTransDto.getTime()))
				.andExpect(jsonPath("$.depart").value(SampleHotelTransDto.getDepart()))
				.andExpect(jsonPath("$.hotelId").value(SampleHotelTransDto.getHotelId()));

		verify(hotelTransService, times(1)).update(eq(1L), any(HotelTransDto.class));
	}

	@Test
	@DisplayName("DELETE /api/hoteltrans/{transId} - 호텔 교통 삭제 성공")
	void deleteTrans_shouldReturnNoContent() throws Exception {
		doNothing().when(hotelTransService).deleteById(1L);

		mockMvc.perform(delete("/api/hoteltrans/{transId}", 1L))
				.andExpect(status().isNoContent());

		verify(hotelTransService, times(1)).deleteById(1L);
	}
}
