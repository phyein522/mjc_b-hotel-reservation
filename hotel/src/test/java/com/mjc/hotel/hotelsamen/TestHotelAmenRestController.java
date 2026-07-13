package com.mjc.hotel.hotelsamen;

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
	@DisplayName("GET /api/hotelamenities/hotel/{hotelId} - 호텔별 편의시설 목록 조회 성공")
	void getHotelAmen_shouldReturnOk() throws Exception {
		Page<HotelAmenDto> pageResult = new PageImpl<>(
				List.of(SampleHotelAmenDto),
				PageRequest.of(0, 10),
				1
		);
		when(hotelAmenService.findAllByHotelIdEquals(eq(10L), any())).thenReturn(pageResult);

		mockMvc.perform(get("/api/hotelamenities/hotel/{hotelId}", 10L)
						.param("page", "0")
						.param("size", "10"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.responseData.content[0].amenId").value(SampleHotelAmenDto.getAmenId()))
				.andExpect(jsonPath("$.responseData.content[0].wifi").value(SampleHotelAmenDto.getWifi()))
				.andExpect(jsonPath("$.responseData.content[0].pool").value(SampleHotelAmenDto.getPool()))
				.andExpect(jsonPath("$.responseData.content[0].fitnessCenter").value(SampleHotelAmenDto.getFitnessCenter()))
				.andExpect(jsonPath("$.responseData.content[0].spa").value(SampleHotelAmenDto.getSpa()))
				.andExpect(jsonPath("$.responseData.content[0].restaurant").value(SampleHotelAmenDto.getRestaurant()))
				.andExpect(jsonPath("$.responseData.content[0].hotelId").value(SampleHotelAmenDto.getHotelId()))
				.andExpect(jsonPath("$.responseData.totalElements").value(1));

		verify(hotelAmenService, times(1)).findAllByHotelIdEquals(eq(10L), any());
	}

	@Test
	@DisplayName("GET /api/hotelamenities/{hotelAmenId} - 호텔 편의시설 단건 조회 성공")
	void getHotelAmenById_shouldReturnOk() throws Exception {
		when(hotelAmenService.findById(1L)).thenReturn(SampleHotelAmenDto);

		mockMvc.perform(get("/api/hotelamenities/{hotelAmenId}", 1L))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.responseData.amenId").value(SampleHotelAmenDto.getAmenId()))
				.andExpect(jsonPath("$.responseData.wifi").value(SampleHotelAmenDto.getWifi()))
				.andExpect(jsonPath("$.responseData.pool").value(SampleHotelAmenDto.getPool()))
				.andExpect(jsonPath("$.responseData.fitnessCenter").value(SampleHotelAmenDto.getFitnessCenter()))
				.andExpect(jsonPath("$.responseData.spa").value(SampleHotelAmenDto.getSpa()))
				.andExpect(jsonPath("$.responseData.restaurant").value(SampleHotelAmenDto.getRestaurant()))
				.andExpect(jsonPath("$.responseData.hotelId").value(SampleHotelAmenDto.getHotelId()));

		verify(hotelAmenService, times(1)).findById(1L);
	}

	@Test
	@DisplayName("POST /api/hotelamenities - 호텔 편의시설 등록 성공")
	void insertHotelAmen_shouldReturnOk() throws Exception {
		when(hotelAmenService.insert(any(HotelAmenDto.class))).thenReturn(SampleHotelAmenDto);

		mockMvc.perform(post("/api/hotelamenities")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(SampleHotelAmenDto)))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.responseData.amenId").value(SampleHotelAmenDto.getAmenId()))
				.andExpect(jsonPath("$.responseData.wifi").value(SampleHotelAmenDto.getWifi()))
				.andExpect(jsonPath("$.responseData.pool").value(SampleHotelAmenDto.getPool()))
				.andExpect(jsonPath("$.responseData.fitnessCenter").value(SampleHotelAmenDto.getFitnessCenter()))
				.andExpect(jsonPath("$.responseData.spa").value(SampleHotelAmenDto.getSpa()))
				.andExpect(jsonPath("$.responseData.restaurant").value(SampleHotelAmenDto.getRestaurant()))
				.andExpect(jsonPath("$.responseData.hotelId").value(SampleHotelAmenDto.getHotelId()));

		verify(hotelAmenService, times(1)).insert(any(HotelAmenDto.class));
	}

	@Test
	@DisplayName("PATCH /api/hotelamenities - 호텔 편의시설 수정 성공")
	void updateHotelAmen_shouldReturnOk() throws Exception {
		SampleHotelAmenDto.setSpa(true);
		when(hotelAmenService.update(any(HotelAmenDto.class))).thenReturn(SampleHotelAmenDto);

		mockMvc.perform(patch("/api/hotelamenities")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(SampleHotelAmenDto)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.responseData.amenId").value(SampleHotelAmenDto.getAmenId()))
				.andExpect(jsonPath("$.responseData.wifi").value(SampleHotelAmenDto.getWifi()))
				.andExpect(jsonPath("$.responseData.pool").value(SampleHotelAmenDto.getPool()))
				.andExpect(jsonPath("$.responseData.fitnessCenter").value(SampleHotelAmenDto.getFitnessCenter()))
				.andExpect(jsonPath("$.responseData.spa").value(SampleHotelAmenDto.getSpa()))
				.andExpect(jsonPath("$.responseData.restaurant").value(SampleHotelAmenDto.getRestaurant()))
				.andExpect(jsonPath("$.responseData.hotelId").value(SampleHotelAmenDto.getHotelId()));

		verify(hotelAmenService, times(1)).update(any(HotelAmenDto.class));
	}

	@Test
	@DisplayName("DELETE /api/hotelamenities/{hotelAmenId} - 호텔 편의시설 삭제 성공")
	void deleteAmenImage_shouldReturnNoContent() throws Exception {
		when(hotelAmenService.deleteById(1L)).thenReturn(SampleHotelAmenDto);

		mockMvc.perform(delete("/api/hotelamenities/{hotelAmenId}", 1L))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.responseData.amenId").value(SampleHotelAmenDto.getAmenId()))
				.andExpect(jsonPath("$.responseData.wifi").value(SampleHotelAmenDto.getWifi()))
				.andExpect(jsonPath("$.responseData.pool").value(SampleHotelAmenDto.getPool()))
				.andExpect(jsonPath("$.responseData.fitnessCenter").value(SampleHotelAmenDto.getFitnessCenter()))
				.andExpect(jsonPath("$.responseData.spa").value(SampleHotelAmenDto.getSpa()))
				.andExpect(jsonPath("$.responseData.restaurant").value(SampleHotelAmenDto.getRestaurant()))
				.andExpect(jsonPath("$.responseData.hotelId").value(SampleHotelAmenDto.getHotelId()));

		verify(hotelAmenService, times(1)).deleteById(1L);
	}
}
