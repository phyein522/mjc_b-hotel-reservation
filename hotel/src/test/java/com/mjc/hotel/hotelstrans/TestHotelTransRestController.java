package com.mjc.hotel.hotelstrans;

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
	@DisplayName("GET /api/hoteltrans/hotel/{hotelId} - 호텔별 교통 목록 조회 성공")
	void getTrans_shouldReturnOk() throws Exception {
		Page<HotelTransDto> pageResult = new PageImpl<>(
				List.of(SampleHotelTransDto),
				PageRequest.of(0, 10),
				1
		);
		when(hotelTransService.findAllByHotelIdEquals(eq(10L), any())).thenReturn(pageResult);

		mockMvc.perform(get("/api/hoteltrans/hotel/{hotelId}", 10L)
						.param("page", "0")
						.param("size", "10"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.responseData.content[0].transId").value(SampleHotelTransDto.getTransId()))
				.andExpect(jsonPath("$.responseData.content[0].name").value(SampleHotelTransDto.getName()))
				.andExpect(jsonPath("$.responseData.content[0].time").value(SampleHotelTransDto.getTime()))
				.andExpect(jsonPath("$.responseData.content[0].depart").value(SampleHotelTransDto.getDepart()))
				.andExpect(jsonPath("$.responseData.content[0].hotelId").value(SampleHotelTransDto.getHotelId()))
				.andExpect(jsonPath("$.responseData.totalElements").value(1));

		verify(hotelTransService, times(1)).findAllByHotelIdEquals(eq(10L), any());
	}

	@Test
	@DisplayName("GET /api/hoteltrans/{transId} - 호텔 교통 단건 조회 성공")
	void getTransById_shouldReturnOk() throws Exception {
		when(hotelTransService.findById(1L)).thenReturn(SampleHotelTransDto);

		mockMvc.perform(get("/api/hoteltrans/{transId}", 1L))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.responseData.transId").value(SampleHotelTransDto.getTransId()))
				.andExpect(jsonPath("$.responseData.name").value(SampleHotelTransDto.getName()))
				.andExpect(jsonPath("$.responseData.time").value(SampleHotelTransDto.getTime()))
				.andExpect(jsonPath("$.responseData.depart").value(SampleHotelTransDto.getDepart()))
				.andExpect(jsonPath("$.responseData.hotelId").value(SampleHotelTransDto.getHotelId()));

		verify(hotelTransService, times(1)).findById(1L);
	}

	@Test
	@DisplayName("POST /api/hoteltrans - 호텔 교통 등록 성공")
	void insertTrans_shouldReturnOk() throws Exception {
		when(hotelTransService.insert(any(HotelTransDto.class))).thenReturn(SampleHotelTransDto);

		mockMvc.perform(post("/api/hoteltrans")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(SampleHotelTransDto)))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.responseData.transId").value(SampleHotelTransDto.getTransId()))
				.andExpect(jsonPath("$.responseData.name").value(SampleHotelTransDto.getName()))
				.andExpect(jsonPath("$.responseData.time").value(SampleHotelTransDto.getTime()))
				.andExpect(jsonPath("$.responseData.depart").value(SampleHotelTransDto.getDepart()))
				.andExpect(jsonPath("$.responseData.hotelId").value(SampleHotelTransDto.getHotelId()));

		verify(hotelTransService, times(1)).insert(any(HotelTransDto.class));
	}

	@Test
	@DisplayName("PATCH /api/hoteltrans - 호텔 교통 수정 성공")
	void updateTrans_shouldReturnOk() throws Exception {
		SampleHotelTransDto.setTime("10:00");
		when(hotelTransService.update(any(HotelTransDto.class))).thenReturn(SampleHotelTransDto);

		mockMvc.perform(patch("/api/hoteltrans")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(SampleHotelTransDto)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.responseData.transId").value(SampleHotelTransDto.getTransId()))
				.andExpect(jsonPath("$.responseData.name").value(SampleHotelTransDto.getName()))
				.andExpect(jsonPath("$.responseData.time").value(SampleHotelTransDto.getTime()))
				.andExpect(jsonPath("$.responseData.depart").value(SampleHotelTransDto.getDepart()))
				.andExpect(jsonPath("$.responseData.hotelId").value(SampleHotelTransDto.getHotelId()));

		verify(hotelTransService, times(1)).update(any(HotelTransDto.class));
	}

	@Test
	@DisplayName("DELETE /api/hoteltrans/{transId} - 호텔 교통 삭제 성공")
	void deleteTrans_shouldReturnNoContent() throws Exception {
		when(hotelTransService.deleteById(1L)).thenReturn(SampleHotelTransDto);

		mockMvc.perform(delete("/api/hoteltrans/{transId}", 1L))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.responseData.transId").value(SampleHotelTransDto.getTransId()))
				.andExpect(jsonPath("$.responseData.name").value(SampleHotelTransDto.getName()))
				.andExpect(jsonPath("$.responseData.time").value(SampleHotelTransDto.getTime()))
				.andExpect(jsonPath("$.responseData.depart").value(SampleHotelTransDto.getDepart()))
				.andExpect(jsonPath("$.responseData.hotelId").value(SampleHotelTransDto.getHotelId()));

		verify(hotelTransService, times(1)).deleteById(1L);
	}
}
