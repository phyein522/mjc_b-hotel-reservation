package com.mjc.hotel.hotelsamen;

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

@WebMvcTest(HotelAmenRestController.class)
public class TestHotelAmenRestController {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private HotelAmenService hotelAmenService;

	@Autowired
	private ObjectMapper objectMapper;

	private static HotelAmenDto SampleHotelAmenDto;

	@BeforeEach
	void setUp() {
		SampleHotelAmenDto = HotelAmenDto.builder()
				.amenId(1L)
				.wifi(true)
				.pool(true)
				.fitnessCenter(true)
				.spa(false)
				.restaurant(true)
				.valetParking(false)
				.freeParking(true)
				.concierge(true)
				.bar(true)
				.breakfast(true)
				.airportShuttle(false)
				.roomService(true)
				.laundry(true)
				.lounge(false)
				.sauna(true)
				.freeCancel(true)
				.petFriendly(false)
				.hotelId(10L)
				.build();
	}

	@Test
	@DisplayName("GET /api/hotelamenities - 호텔 편의시설 목록 조회 성공")
	void getHotelAmen_shouldReturnOk() throws Exception {
		when(hotelAmenService.findAll()).thenReturn(List.of(SampleHotelAmenDto));

		mockMvc.perform(get("/api/hotelamenities"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[0].amenId").value(SampleHotelAmenDto.getAmenId()))
				.andExpect(jsonPath("$[0].wifi").value(SampleHotelAmenDto.getWifi()))
				.andExpect(jsonPath("$[0].pool").value(SampleHotelAmenDto.getPool()))
				.andExpect(jsonPath("$[0].fitnessCenter").value(SampleHotelAmenDto.getFitnessCenter()))
				.andExpect(jsonPath("$[0].spa").value(SampleHotelAmenDto.getSpa()))
				.andExpect(jsonPath("$[0].restaurant").value(SampleHotelAmenDto.getRestaurant()))
				.andExpect(jsonPath("$[0].hotelId").value(SampleHotelAmenDto.getHotelId()));

		verify(hotelAmenService, times(1)).findAll();
	}

	@Test
	@DisplayName("GET /api/hotelamenities/{hotelAmenId} - 호텔 편의시설 단건 조회 성공")
	void getHotelAmenById_shouldReturnOk() throws Exception {
		when(hotelAmenService.findById(1L)).thenReturn(SampleHotelAmenDto);

		mockMvc.perform(get("/api/hotelamenities/{hotelAmenId}", 1L))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.amenId").value(SampleHotelAmenDto.getAmenId()))
				.andExpect(jsonPath("$.wifi").value(SampleHotelAmenDto.getWifi()))
				.andExpect(jsonPath("$.pool").value(SampleHotelAmenDto.getPool()))
				.andExpect(jsonPath("$.fitnessCenter").value(SampleHotelAmenDto.getFitnessCenter()))
				.andExpect(jsonPath("$.spa").value(SampleHotelAmenDto.getSpa()))
				.andExpect(jsonPath("$.restaurant").value(SampleHotelAmenDto.getRestaurant()))
				.andExpect(jsonPath("$.hotelId").value(SampleHotelAmenDto.getHotelId()));

		verify(hotelAmenService, times(1)).findById(1L);
	}

	@Test
	@DisplayName("POST /api/hotelamenities - 호텔 편의시설 등록 성공")
	void insertHotelAmen_shouldReturnOk() throws Exception {
		when(hotelAmenService.insert(any(HotelAmenDto.class))).thenReturn(SampleHotelAmenDto);

		mockMvc.perform(post("/api/hotelamenities")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(SampleHotelAmenDto)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.amenId").value(SampleHotelAmenDto.getAmenId()))
				.andExpect(jsonPath("$.wifi").value(SampleHotelAmenDto.getWifi()))
				.andExpect(jsonPath("$.pool").value(SampleHotelAmenDto.getPool()))
				.andExpect(jsonPath("$.fitnessCenter").value(SampleHotelAmenDto.getFitnessCenter()))
				.andExpect(jsonPath("$.spa").value(SampleHotelAmenDto.getSpa()))
				.andExpect(jsonPath("$.restaurant").value(SampleHotelAmenDto.getRestaurant()))
				.andExpect(jsonPath("$.hotelId").value(SampleHotelAmenDto.getHotelId()));

		verify(hotelAmenService, times(1)).insert(any(HotelAmenDto.class));
	}

	@Test
	@DisplayName("PUT /api/hotelamenities/{hotelAmenId} - 호텔 편의시설 수정 성공")
	void updateHotelAmen_shouldReturnOk() throws Exception {
		SampleHotelAmenDto.setSpa(true);
		when(hotelAmenService.update(eq(1L), any(HotelAmenDto.class))).thenReturn(SampleHotelAmenDto);

		mockMvc.perform(put("/api/hotelamenities/{hotelAmenId}", 1L)
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(SampleHotelAmenDto)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.amenId").value(SampleHotelAmenDto.getAmenId()))
				.andExpect(jsonPath("$.wifi").value(SampleHotelAmenDto.getWifi()))
				.andExpect(jsonPath("$.pool").value(SampleHotelAmenDto.getPool()))
				.andExpect(jsonPath("$.fitnessCenter").value(SampleHotelAmenDto.getFitnessCenter()))
				.andExpect(jsonPath("$.spa").value(SampleHotelAmenDto.getSpa()))
				.andExpect(jsonPath("$.restaurant").value(SampleHotelAmenDto.getRestaurant()))
				.andExpect(jsonPath("$.hotelId").value(SampleHotelAmenDto.getHotelId()));

		verify(hotelAmenService, times(1)).update(eq(1L), any(HotelAmenDto.class));
	}

	@Test
	@DisplayName("DELETE /api/hotelamenities/{hotelAmenId} - 호텔 편의시설 삭제 성공")
	void deleteAmenImage_shouldReturnNoContent() throws Exception {
		doNothing().when(hotelAmenService).deleteById(1L);

		mockMvc.perform(delete("/api/hotelamenities/{hotelAmenId}", 1L))
				.andExpect(status().isNoContent());

		verify(hotelAmenService, times(1)).deleteById(1L);
	}
}
