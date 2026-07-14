package com.mjc.hotel.hotels;

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

import java.math.BigDecimal;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
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
				.latitude(BigDecimal.valueOf(36.3504119))
				.longitude(BigDecimal.valueOf(127.3845475))
				.type(HotelTypeEnum.HOTEL)
				.build();
	}

	@Test
	@DisplayName("GET /api/hotels - 호텔 목록 조회 성공")
	void getHotels_shouldReturnOk() throws Exception {
		// given
		Page<HotelDto> pageResult = new PageImpl<>(
				List.of(SampleHotelDto),
				PageRequest.of(0, 10),
				1
		);
		when(hotelService.findAll(any())).thenReturn(pageResult);

		// when & then
		mockMvc.perform(get("/api/hotels")
						.param("page", "0")
						.param("size", "10"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.responseData.content[0].hotelId").value(SampleHotelDto.getHotelId()))
				.andExpect(jsonPath("$.responseData.content[0].name").value(SampleHotelDto.getName()))
				.andExpect(jsonPath("$.responseData.content[0].description").value(SampleHotelDto.getDescription()))
				.andExpect(jsonPath("$.responseData.content[0].address").value(SampleHotelDto.getAddress()))
				.andExpect(jsonPath("$.responseData.content[0].city").value(SampleHotelDto.getCity()))
				.andExpect(jsonPath("$.responseData.content[0].zipCode").value(SampleHotelDto.getZipCode()))
				.andExpect(jsonPath("$.responseData.content[0].phone").value(SampleHotelDto.getPhone()))
				.andExpect(jsonPath("$.responseData.content[0].email").value(SampleHotelDto.getEmail()))
				.andExpect(jsonPath("$.responseData.content[0].checkIn").value(SampleHotelDto.getCheckIn().format(TIME_FORMATTER)))
				.andExpect(jsonPath("$.responseData.content[0].checkOut").value(SampleHotelDto.getCheckOut().format(TIME_FORMATTER)))
				.andExpect(jsonPath("$.responseData.content[0].starRate").value(SampleHotelDto.getStarRate()))
				.andExpect(jsonPath("$.responseData.content[0].isActive").value(SampleHotelDto.getIsActive()))
				.andExpect(jsonPath("$.responseData.content[0].latitude").value(SampleHotelDto.getLatitude()))
				.andExpect(jsonPath("$.responseData.content[0].longitude").value(SampleHotelDto.getLongitude()))
				.andExpect(jsonPath("$.responseData.content[0].type").value(SampleHotelDto.getType().toString()))
				.andExpect(jsonPath("$.responseData.totalElements").value(1));

		verify(hotelService, times(1)).findAll(any());
	}

	@Test
	@DisplayName("GET /api/hotels/{hotelId} - 호텔 단건 조회 성공")
	void getHotel_shouldReturnOk() throws Exception {
		// given
		when(hotelService.findById(1L)).thenReturn(SampleHotelDto);

		// when & then
		mockMvc.perform(get("/api/hotels/{hotelId}", 1L))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.responseData.hotelId").value(SampleHotelDto.getHotelId()))
				.andExpect(jsonPath("$.responseData.name").value(SampleHotelDto.getName()))
				.andExpect(jsonPath("$.responseData.description").value(SampleHotelDto.getDescription()))
				.andExpect(jsonPath("$.responseData.address").value(SampleHotelDto.getAddress()))
				.andExpect(jsonPath("$.responseData.city").value(SampleHotelDto.getCity()))
				.andExpect(jsonPath("$.responseData.zipCode").value(SampleHotelDto.getZipCode()))
				.andExpect(jsonPath("$.responseData.phone").value(SampleHotelDto.getPhone()))
				.andExpect(jsonPath("$.responseData.email").value(SampleHotelDto.getEmail()))
				.andExpect(jsonPath("$.responseData.checkIn").value(SampleHotelDto.getCheckIn().format(TIME_FORMATTER)))
				.andExpect(jsonPath("$.responseData.checkOut").value(SampleHotelDto.getCheckOut().format(TIME_FORMATTER)))
				.andExpect(jsonPath("$.responseData.starRate").value(SampleHotelDto.getStarRate()))
				.andExpect(jsonPath("$.responseData.isActive").value(SampleHotelDto.getIsActive()))
				.andExpect(jsonPath("$.responseData.latitude").value(SampleHotelDto.getLatitude()))
				.andExpect(jsonPath("$.responseData.longitude").value(SampleHotelDto.getLongitude()))
				.andExpect(jsonPath("$.responseData.type").value(SampleHotelDto.getType().toString()));

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
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.responseData.hotelId").value(SampleHotelDto.getHotelId()))
				.andExpect(jsonPath("$.responseData.name").value(SampleHotelDto.getName()))
				.andExpect(jsonPath("$.responseData.description").value(SampleHotelDto.getDescription()))
				.andExpect(jsonPath("$.responseData.address").value(SampleHotelDto.getAddress()))
				.andExpect(jsonPath("$.responseData.city").value(SampleHotelDto.getCity()))
				.andExpect(jsonPath("$.responseData.zipCode").value(SampleHotelDto.getZipCode()))
				.andExpect(jsonPath("$.responseData.phone").value(SampleHotelDto.getPhone()))
				.andExpect(jsonPath("$.responseData.email").value(SampleHotelDto.getEmail()))
				.andExpect(jsonPath("$.responseData.checkIn").value(SampleHotelDto.getCheckIn().format(TIME_FORMATTER)))
				.andExpect(jsonPath("$.responseData.checkOut").value(SampleHotelDto.getCheckOut().format(TIME_FORMATTER)))
				.andExpect(jsonPath("$.responseData.starRate").value(SampleHotelDto.getStarRate()))
				.andExpect(jsonPath("$.responseData.isActive").value(SampleHotelDto.getIsActive()))
				.andExpect(jsonPath("$.responseData.latitude").value(SampleHotelDto.getLatitude()))
				.andExpect(jsonPath("$.responseData.longitude").value(SampleHotelDto.getLongitude()))
				.andExpect(jsonPath("$.responseData.type").value(SampleHotelDto.getType().toString()));

		verify(hotelService, times(1)).insert(any(HotelDto.class));
	}

	@Test
	@DisplayName("PATCH /api/hotels - 호텔 수정 성공")
	void updateHotel_shouldReturnOk() throws Exception {
		// given
		SampleHotelDto.setName("수정된 엠제이씨 호텔");
		when(hotelService.update(any(HotelDto.class))).thenReturn(SampleHotelDto);

		// when & then
		mockMvc.perform(patch("/api/hotels")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(SampleHotelDto)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.responseData.hotelId").value(SampleHotelDto.getHotelId()))
				.andExpect(jsonPath("$.responseData.name").value(SampleHotelDto.getName()))
				.andExpect(jsonPath("$.responseData.description").value(SampleHotelDto.getDescription()))
				.andExpect(jsonPath("$.responseData.address").value(SampleHotelDto.getAddress()))
				.andExpect(jsonPath("$.responseData.city").value(SampleHotelDto.getCity()))
				.andExpect(jsonPath("$.responseData.zipCode").value(SampleHotelDto.getZipCode()))
				.andExpect(jsonPath("$.responseData.phone").value(SampleHotelDto.getPhone()))
				.andExpect(jsonPath("$.responseData.email").value(SampleHotelDto.getEmail()))
				.andExpect(jsonPath("$.responseData.checkIn").value(SampleHotelDto.getCheckIn().format(TIME_FORMATTER)))
				.andExpect(jsonPath("$.responseData.checkOut").value(SampleHotelDto.getCheckOut().format(TIME_FORMATTER)))
				.andExpect(jsonPath("$.responseData.starRate").value(SampleHotelDto.getStarRate()))
				.andExpect(jsonPath("$.responseData.isActive").value(SampleHotelDto.getIsActive()))
				.andExpect(jsonPath("$.responseData.latitude").value(SampleHotelDto.getLatitude()))
				.andExpect(jsonPath("$.responseData.longitude").value(SampleHotelDto.getLongitude()))
				.andExpect(jsonPath("$.responseData.type").value(SampleHotelDto.getType().toString()));

		verify(hotelService, times(1)).update(any(HotelDto.class));
	}

	@Test
	@DisplayName("DELETE /api/hotels/{hotelId} - 호텔 삭제 성공")
	void deleteHotel_shouldReturnOk() throws Exception {
		// given
		when(hotelService.deleteById(1L)).thenReturn(SampleHotelDto);

		// when & then
		mockMvc.perform(delete("/api/hotels/{hotelId}", 1L))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.responseData.hotelId").value(SampleHotelDto.getHotelId()))
				.andExpect(jsonPath("$.responseData.name").value(SampleHotelDto.getName()))
				.andExpect(jsonPath("$.responseData.description").value(SampleHotelDto.getDescription()))
				.andExpect(jsonPath("$.responseData.address").value(SampleHotelDto.getAddress()))
				.andExpect(jsonPath("$.responseData.city").value(SampleHotelDto.getCity()))
				.andExpect(jsonPath("$.responseData.zipCode").value(SampleHotelDto.getZipCode()))
				.andExpect(jsonPath("$.responseData.phone").value(SampleHotelDto.getPhone()))
				.andExpect(jsonPath("$.responseData.email").value(SampleHotelDto.getEmail()))
				.andExpect(jsonPath("$.responseData.checkIn").value(SampleHotelDto.getCheckIn().format(TIME_FORMATTER)))
				.andExpect(jsonPath("$.responseData.checkOut").value(SampleHotelDto.getCheckOut().format(TIME_FORMATTER)))
				.andExpect(jsonPath("$.responseData.starRate").value(SampleHotelDto.getStarRate()))
				.andExpect(jsonPath("$.responseData.isActive").value(SampleHotelDto.getIsActive()))
				.andExpect(jsonPath("$.responseData.latitude").value(SampleHotelDto.getLatitude()))
				.andExpect(jsonPath("$.responseData.longitude").value(SampleHotelDto.getLongitude()))
				.andExpect(jsonPath("$.responseData.type").value(SampleHotelDto.getType().toString()));

		verify(hotelService, times(1)).deleteById(1L);
	}
}
