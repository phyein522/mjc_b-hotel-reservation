package com.mjc.hotel.hotels;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
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

/**
 * HotelRestController 의 모든 URL Mapping 메소드에 대한 단위 테스트
 *
 * @WebMvcTest 를 사용하여 웹 계층만 슬라이스 테스트하고,
 * HotelService 는 @MockitoBean 으로 목(mock) 처리한다.
 */
@WebMvcTest(HotelRestController.class)
public class TestHotelRestController {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private HotelService hotelService;

	@Autowired
	private ObjectMapper objectMapper;

	private static HotelDto SampleHotelDto;
	private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss");

	@BeforeEach
	void setUp() {
		SampleHotelDto = HotelDto.builder()
				.hotelId(1L)
				.name("엠제이씨 호텔")
				.description("대전 중심가에 위치한 비즈니스 호텔")
				.address("대전광역시 중구 중앙로 123")
				.city("대전")
				.zipCode("34838")
				.phone("042-123-4567")
				.email("hotel@mjc.com")
				.checkIn(LocalTime.of(15, 0))
				.checkOut(LocalTime.of(11, 0))
				.starRate(5)
				.isActive(true)
				.latitude("36.3504119")
				.longitude("127.3845475")
				.type(HotelTypeEnum.HOTEL)
				.build();
	}

	@Test
	@DisplayName("GET /api/hotels - 호텔 목록 조회 성공")
	void getHotels_shouldReturnOk() throws Exception {
		// given
		when(hotelService.findAll()).thenReturn(List.of(SampleHotelDto));

		// when & then
		mockMvc.perform(get("/api/hotels"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[0].hotelId").value(SampleHotelDto.getHotelId()))
				.andExpect(jsonPath("$[0].name").value(SampleHotelDto.getName()))
				.andExpect(jsonPath("$[0].description").value(SampleHotelDto.getDescription()))
				.andExpect(jsonPath("$[0].address").value(SampleHotelDto.getAddress()))
				.andExpect(jsonPath("$[0].city").value(SampleHotelDto.getCity()))
				.andExpect(jsonPath("$[0].zipCode").value(SampleHotelDto.getZipCode()))
				.andExpect(jsonPath("$[0].phone").value(SampleHotelDto.getPhone()))
				.andExpect(jsonPath("$[0].email").value(SampleHotelDto.getEmail()))
				.andExpect(jsonPath("$[0].checkIn").value(SampleHotelDto.getCheckIn().format(TIME_FORMATTER)))
				.andExpect(jsonPath("$[0].checkOut").value(SampleHotelDto.getCheckOut().format(TIME_FORMATTER)))
				.andExpect(jsonPath("$[0].starRate").value(SampleHotelDto.getStarRate()))
				.andExpect(jsonPath("$[0].isActive").value(SampleHotelDto.getIsActive()))
				.andExpect(jsonPath("$[0].latitude").value(SampleHotelDto.getLatitude()))
				.andExpect(jsonPath("$[0].longitude").value(SampleHotelDto.getLongitude()))
				.andExpect(jsonPath("$[0].type").value(SampleHotelDto.getType().toString()));

		verify(hotelService, times(1)).findAll();
	}

	@Test
	@DisplayName("GET /api/hotels/{hotelId} - 호텔 단건 조회 성공")
	void getHotel_shouldReturnOk() throws Exception {
		// given
		when(hotelService.findById(1L)).thenReturn(SampleHotelDto);

		// when & then
		mockMvc.perform(get("/api/hotels/{hotelId}", 1L))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.hotelId").value(SampleHotelDto.getHotelId()))
				.andExpect(jsonPath("$.name").value(SampleHotelDto.getName()))
				.andExpect(jsonPath("$.description").value(SampleHotelDto.getDescription()))
				.andExpect(jsonPath("$.address").value(SampleHotelDto.getAddress()))
				.andExpect(jsonPath("$.city").value(SampleHotelDto.getCity()))
				.andExpect(jsonPath("$.zipCode").value(SampleHotelDto.getZipCode()))
				.andExpect(jsonPath("$.phone").value(SampleHotelDto.getPhone()))
				.andExpect(jsonPath("$.email").value(SampleHotelDto.getEmail()))
				.andExpect(jsonPath("$.checkIn").value(SampleHotelDto.getCheckIn().format(TIME_FORMATTER)))
				.andExpect(jsonPath("$.checkOut").value(SampleHotelDto.getCheckOut().format(TIME_FORMATTER)))
				.andExpect(jsonPath("$.starRate").value(SampleHotelDto.getStarRate()))
				.andExpect(jsonPath("$.isActive").value(SampleHotelDto.getIsActive()))
				.andExpect(jsonPath("$.latitude").value(SampleHotelDto.getLatitude()))
				.andExpect(jsonPath("$.longitude").value(SampleHotelDto.getLongitude()))
				.andExpect(jsonPath("$.type").value(SampleHotelDto.getType().toString()));

		verify(hotelService, times(1)).findById(1L);
	}

	@Test
	@DisplayName("POST /api/hotels - 호텔 등록 성공")
	void insertHotel_shouldReturnOk() throws Exception {
		// given
		when(hotelService.insert(any(HotelDto.class))).thenReturn(SampleHotelDto);

		// when & then
		mockMvc.perform(post("/api/hotels")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(SampleHotelDto)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.hotelId").value(SampleHotelDto.getHotelId()))
				.andExpect(jsonPath("$.name").value(SampleHotelDto.getName()))
				.andExpect(jsonPath("$.description").value(SampleHotelDto.getDescription()))
				.andExpect(jsonPath("$.address").value(SampleHotelDto.getAddress()))
				.andExpect(jsonPath("$.city").value(SampleHotelDto.getCity()))
				.andExpect(jsonPath("$.zipCode").value(SampleHotelDto.getZipCode()))
				.andExpect(jsonPath("$.phone").value(SampleHotelDto.getPhone()))
				.andExpect(jsonPath("$.email").value(SampleHotelDto.getEmail()))
				.andExpect(jsonPath("$.checkIn").value(SampleHotelDto.getCheckIn().format(TIME_FORMATTER)))
				.andExpect(jsonPath("$.checkOut").value(SampleHotelDto.getCheckOut().format(TIME_FORMATTER)))
				.andExpect(jsonPath("$.starRate").value(SampleHotelDto.getStarRate()))
				.andExpect(jsonPath("$.isActive").value(SampleHotelDto.getIsActive()))
				.andExpect(jsonPath("$.latitude").value(SampleHotelDto.getLatitude()))
				.andExpect(jsonPath("$.longitude").value(SampleHotelDto.getLongitude()))
				.andExpect(jsonPath("$.type").value(SampleHotelDto.getType().toString()));

		verify(hotelService, times(1)).insert(any(HotelDto.class));
	}

	@Test
	@DisplayName("PUT /api/hotels/{hotelId} - 호텔 수정 성공")
	void updateHotel_shouldReturnOk() throws Exception {
		// given
		SampleHotelDto.setName("수정된 엠제이씨 호텔");
		when(hotelService.update(eq(1L), any(HotelDto.class))).thenReturn(SampleHotelDto);

		// when & then
		mockMvc.perform(put("/api/hotels/{hotelId}", 1L)
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(SampleHotelDto)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.hotelId").value(SampleHotelDto.getHotelId()))
				.andExpect(jsonPath("$.name").value(SampleHotelDto.getName()))
				.andExpect(jsonPath("$.description").value(SampleHotelDto.getDescription()))
				.andExpect(jsonPath("$.address").value(SampleHotelDto.getAddress()))
				.andExpect(jsonPath("$.city").value(SampleHotelDto.getCity()))
				.andExpect(jsonPath("$.zipCode").value(SampleHotelDto.getZipCode()))
				.andExpect(jsonPath("$.phone").value(SampleHotelDto.getPhone()))
				.andExpect(jsonPath("$.email").value(SampleHotelDto.getEmail()))
				.andExpect(jsonPath("$.checkIn").value(SampleHotelDto.getCheckIn().format(TIME_FORMATTER)))
				.andExpect(jsonPath("$.checkOut").value(SampleHotelDto.getCheckOut().format(TIME_FORMATTER)))
				.andExpect(jsonPath("$.starRate").value(SampleHotelDto.getStarRate()))
				.andExpect(jsonPath("$.isActive").value(SampleHotelDto.getIsActive()))
				.andExpect(jsonPath("$.latitude").value(SampleHotelDto.getLatitude()))
				.andExpect(jsonPath("$.longitude").value(SampleHotelDto.getLongitude()))
				.andExpect(jsonPath("$.type").value(SampleHotelDto.getType().toString()));

		verify(hotelService, times(1)).update(eq(1L), any(HotelDto.class));
	}

	@Test
	@DisplayName("DELETE /api/hotels/{hotelId} - 호텔 삭제 성공")
	void deleteHotel_shouldReturnNoContent() throws Exception {
		// given
		doNothing().when(hotelService).deleteByID(1L);

		// when & then
		mockMvc.perform(delete("/api/hotels/{hotelId}", 1L))
				.andExpect(status().isNoContent());

		verify(hotelService, times(1)).deleteByID(1L);
	}
}
