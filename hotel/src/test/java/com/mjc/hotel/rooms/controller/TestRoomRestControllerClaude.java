package com.mjc.hotel.rooms.controller;

import com.mjc.hotel.rooms.dto.RoomDto;
import com.mjc.hotel.rooms.dto.RoomResponseWithImagesDto;
import com.mjc.hotel.rooms.enums.RoomBedOption;
import com.mjc.hotel.rooms.enums.RoomStatus;
import com.mjc.hotel.rooms.enums.RoomType;
import com.mjc.hotel.rooms.enums.RoomViewOption;
import com.mjc.hotel.rooms.service.RoomService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * RoomRestController 의 모든 URL Mapping 메소드에 대한 단위 테스트
 *
 * @WebMvcTest 를 사용하여 웹 계층만 슬라이스 테스트하고,
 * RoomService 는 @MockitoBean 으로 목(mock) 처리한다.
 *
 * ApiResponse 의 실제 페이로드 필드명은 "responseData" 이다.
 * (예: {"responseCode":"SELECT_OK","message":"ok","responseData":{...}})
 */
@WebMvcTest(RoomRestController.class)
public class TestRoomRestControllerClaude {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private RoomService roomService;

	@Autowired
	private ObjectMapper objectMapper;

	private static RoomDto SampleRoomDto;

	@BeforeEach
	void setUp() {
		SampleRoomDto = RoomDto.builder()
				.roomId(1L)
				.name("디럭스 트윈룸")
				.number("101")
				.floor(1)
				.size(45)
				.basePrice(BigDecimal.valueOf(150000))
				.maxAdult(2)
				.maxChild(1)
				.isActive(true)
				.roomType(RoomType.Standard)
				.roomStatus(RoomStatus.EnableReservation)
				.roomViewOption(RoomViewOption.CityView)
				.roomBedOption(RoomBedOption.QueenBed)
				.hotelId(10L)
				.build();
	}

	@Test
	@DisplayName("POST /api/v1/room - 객실 등록 성공")
	void insert_shouldReturnCreated() throws Exception {
		// given
		when(roomService.insert(any(RoomDto.class))).thenReturn(SampleRoomDto);

		// when & then
		mockMvc.perform(post("/api/v1/room")
						.contentType("application/json")
						.content(objectMapper.writeValueAsString(SampleRoomDto)))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.responseData.roomId").value(SampleRoomDto.getRoomId()))
				.andExpect(jsonPath("$.responseData.name").value(SampleRoomDto.getName()))
				.andExpect(jsonPath("$.responseData.number").value(SampleRoomDto.getNumber()))
				.andExpect(jsonPath("$.responseData.floor").value(SampleRoomDto.getFloor()))
				.andExpect(jsonPath("$.responseData.size").value(SampleRoomDto.getSize()))
				.andExpect(jsonPath("$.responseData.basePrice").value(SampleRoomDto.getBasePrice()))
				.andExpect(jsonPath("$.responseData.maxAdult").value(SampleRoomDto.getMaxAdult()))
				.andExpect(jsonPath("$.responseData.maxChild").value(SampleRoomDto.getMaxChild()))
				.andExpect(jsonPath("$.responseData.isActive").value(SampleRoomDto.getIsActive()))
				.andExpect(jsonPath("$.responseData.roomType").value(SampleRoomDto.getRoomType().toString()))
				.andExpect(jsonPath("$.responseData.roomStatus").value(SampleRoomDto.getRoomStatus().toString()))
				.andExpect(jsonPath("$.responseData.roomViewOption").value(SampleRoomDto.getRoomViewOption().toString()))
				.andExpect(jsonPath("$.responseData.roomBedOption").value(SampleRoomDto.getRoomBedOption().toString()))
				.andExpect(jsonPath("$.responseData.hotelId").value(SampleRoomDto.getHotelId()))
		;

		verify(roomService, times(1)).insert(any(RoomDto.class));
	}

	@Test
	@DisplayName("POST /api/v1/room/image - 이미지 포함 객실 등록 성공")
	void insertWithImages_shouldReturnCreated() throws Exception {
		// given
		RoomResponseWithImagesDto responseDto = RoomResponseWithImagesDto.builder()
				.roomDto(SampleRoomDto)
				.roomImages(new PageImpl<>(List.of(), PageRequest.of(0, 10), 0))
				.build();

		when(roomService.insertWithImages(any(RoomDto.class), anyList()))
				.thenReturn(responseDto);

		MockMultipartFile requestDtoPart = new MockMultipartFile(
				"requestDto",
				"requestDto",
				"application/json",
				objectMapper.writeValueAsBytes(SampleRoomDto)
		);

		MockMultipartFile filePart = new MockMultipartFile(
				"files",
				"room1.jpg",
				"image/jpeg",
				"dummy-image-content".getBytes()
		);

		// when & then
		mockMvc.perform(multipart("/api/v1/room/image")
						.file(requestDtoPart)
						.file(filePart))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.responseData.roomDto.roomId").value(SampleRoomDto.getRoomId()))
				.andExpect(jsonPath("$.responseData.roomDto.name").value(SampleRoomDto.getName()))
				.andExpect(jsonPath("$.responseData.roomDto.number").value(SampleRoomDto.getNumber()))
				.andExpect(jsonPath("$.responseData.roomDto.floor").value(SampleRoomDto.getFloor()))
				.andExpect(jsonPath("$.responseData.roomDto.size").value(SampleRoomDto.getSize()))
				.andExpect(jsonPath("$.responseData.roomDto.basePrice").value(SampleRoomDto.getBasePrice()))
				.andExpect(jsonPath("$.responseData.roomDto.maxAdult").value(SampleRoomDto.getMaxAdult()))
				.andExpect(jsonPath("$.responseData.roomDto.maxChild").value(SampleRoomDto.getMaxChild()))
				.andExpect(jsonPath("$.responseData.roomDto.isActive").value(SampleRoomDto.getIsActive()))
				.andExpect(jsonPath("$.responseData.roomDto.roomType").value(SampleRoomDto.getRoomType().toString()))
				.andExpect(jsonPath("$.responseData.roomDto.roomStatus").value(SampleRoomDto.getRoomStatus().toString()))
				.andExpect(jsonPath("$.responseData.roomDto.roomViewOption").value(SampleRoomDto.getRoomViewOption().toString()))
				.andExpect(jsonPath("$.responseData.roomDto.roomBedOption").value(SampleRoomDto.getRoomBedOption().toString()))
				.andExpect(jsonPath("$.responseData.roomDto.hotelId").value(SampleRoomDto.getHotelId()))
		;

		verify(roomService, times(1)).insertWithImages(any(RoomDto.class), anyList());
	}

	@Test
	@DisplayName("PATCH /api/v1/room - 객실 수정 성공")
	void update_shouldReturnOk() throws Exception {
		// given
		SampleRoomDto.setName("수정된 객실명");
		when(roomService.update(any(RoomDto.class))).thenReturn(SampleRoomDto);

		// when & then
		mockMvc.perform(patch("/api/v1/room")
						.contentType("application/json")
						.content(objectMapper.writeValueAsString(SampleRoomDto)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.responseData.roomId").value(SampleRoomDto.getRoomId()))
				.andExpect(jsonPath("$.responseData.name").value(SampleRoomDto.getName()))
				.andExpect(jsonPath("$.responseData.number").value(SampleRoomDto.getNumber()))
				.andExpect(jsonPath("$.responseData.floor").value(SampleRoomDto.getFloor()))
				.andExpect(jsonPath("$.responseData.size").value(SampleRoomDto.getSize()))
				.andExpect(jsonPath("$.responseData.basePrice").value(SampleRoomDto.getBasePrice()))
				.andExpect(jsonPath("$.responseData.maxAdult").value(SampleRoomDto.getMaxAdult()))
				.andExpect(jsonPath("$.responseData.maxChild").value(SampleRoomDto.getMaxChild()))
				.andExpect(jsonPath("$.responseData.isActive").value(SampleRoomDto.getIsActive()))
				.andExpect(jsonPath("$.responseData.roomType").value(SampleRoomDto.getRoomType().toString()))
				.andExpect(jsonPath("$.responseData.roomStatus").value(SampleRoomDto.getRoomStatus().toString()))
				.andExpect(jsonPath("$.responseData.roomViewOption").value(SampleRoomDto.getRoomViewOption().toString()))
				.andExpect(jsonPath("$.responseData.roomBedOption").value(SampleRoomDto.getRoomBedOption().toString()))
				.andExpect(jsonPath("$.responseData.hotelId").value(SampleRoomDto.getHotelId()))
		;

		verify(roomService, times(1)).update(any(RoomDto.class));
	}

	@Test
	@DisplayName("GET /api/v1/room/{roomId} - 단건 조회 성공")
	void findById_shouldReturnOk() throws Exception {
		// given
		when(roomService.findById(1L)).thenReturn(SampleRoomDto);

		// when & then
		mockMvc.perform(get("/api/v1/room/{roomId}", 1L))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.responseData.roomId").value(SampleRoomDto.getRoomId()))
				.andExpect(jsonPath("$.responseData.name").value(SampleRoomDto.getName()))
				.andExpect(jsonPath("$.responseData.number").value(SampleRoomDto.getNumber()))
				.andExpect(jsonPath("$.responseData.floor").value(SampleRoomDto.getFloor()))
				.andExpect(jsonPath("$.responseData.size").value(SampleRoomDto.getSize()))
				.andExpect(jsonPath("$.responseData.basePrice").value(SampleRoomDto.getBasePrice()))
				.andExpect(jsonPath("$.responseData.maxAdult").value(SampleRoomDto.getMaxAdult()))
				.andExpect(jsonPath("$.responseData.maxChild").value(SampleRoomDto.getMaxChild()))
				.andExpect(jsonPath("$.responseData.isActive").value(SampleRoomDto.getIsActive()))
				.andExpect(jsonPath("$.responseData.roomType").value(SampleRoomDto.getRoomType().toString()))
				.andExpect(jsonPath("$.responseData.roomStatus").value(SampleRoomDto.getRoomStatus().toString()))
				.andExpect(jsonPath("$.responseData.roomViewOption").value(SampleRoomDto.getRoomViewOption().toString()))
				.andExpect(jsonPath("$.responseData.roomBedOption").value(SampleRoomDto.getRoomBedOption().toString()))
				.andExpect(jsonPath("$.responseData.hotelId").value(SampleRoomDto.getHotelId()))
		;

		verify(roomService, times(1)).findById(1L);
	}

	@Test
	@DisplayName("GET /api/v1/room/images/{roomId} - 이미지 포함 단건 조회 성공")
	void findByIdWithImages_shouldReturnOk() throws Exception {
		// given
		RoomResponseWithImagesDto responseDto = RoomResponseWithImagesDto.builder()
				.roomDto(SampleRoomDto)
				.roomImages(new PageImpl<>(List.of(), PageRequest.of(0, 10), 0))
				.build();

		when(roomService.findByIdWithImages(eq(1L), any())).thenReturn(responseDto);

		// when & then
		mockMvc.perform(get("/api/v1/room/images/{roomId}", 1L)
						.param("page", "0")
						.param("size", "10"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.responseData.roomDto.roomId").value(SampleRoomDto.getRoomId()))
				.andExpect(jsonPath("$.responseData.roomDto.name").value(SampleRoomDto.getName()))
				.andExpect(jsonPath("$.responseData.roomDto.number").value(SampleRoomDto.getNumber()))
				.andExpect(jsonPath("$.responseData.roomDto.floor").value(SampleRoomDto.getFloor()))
				.andExpect(jsonPath("$.responseData.roomDto.size").value(SampleRoomDto.getSize()))
				.andExpect(jsonPath("$.responseData.roomDto.basePrice").value(SampleRoomDto.getBasePrice()))
				.andExpect(jsonPath("$.responseData.roomDto.maxAdult").value(SampleRoomDto.getMaxAdult()))
				.andExpect(jsonPath("$.responseData.roomDto.maxChild").value(SampleRoomDto.getMaxChild()))
				.andExpect(jsonPath("$.responseData.roomDto.isActive").value(SampleRoomDto.getIsActive()))
				.andExpect(jsonPath("$.responseData.roomDto.roomType").value(SampleRoomDto.getRoomType().toString()))
				.andExpect(jsonPath("$.responseData.roomDto.roomStatus").value(SampleRoomDto.getRoomStatus().toString()))
				.andExpect(jsonPath("$.responseData.roomDto.roomViewOption").value(SampleRoomDto.getRoomViewOption().toString()))
				.andExpect(jsonPath("$.responseData.roomDto.roomBedOption").value(SampleRoomDto.getRoomBedOption().toString()))
				.andExpect(jsonPath("$.responseData.roomDto.hotelId").value(SampleRoomDto.getHotelId()))
		;

		verify(roomService, times(1)).findByIdWithImages(eq(1L), any());
	}

	@Test
	@DisplayName("DELETE /api/v1/room/{roomId} - 삭제 성공")
	void deleteById_shouldReturnOk() throws Exception {
		// given
		when(roomService.deleteById(1L)).thenReturn(SampleRoomDto);

		// when & then
		mockMvc.perform(delete("/api/v1/room/{roomId}", 1L))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.responseData.roomId").value(SampleRoomDto.getRoomId()))
				.andExpect(jsonPath("$.responseData.name").value(SampleRoomDto.getName()))
				.andExpect(jsonPath("$.responseData.number").value(SampleRoomDto.getNumber()))
				.andExpect(jsonPath("$.responseData.floor").value(SampleRoomDto.getFloor()))
				.andExpect(jsonPath("$.responseData.size").value(SampleRoomDto.getSize()))
				.andExpect(jsonPath("$.responseData.basePrice").value(SampleRoomDto.getBasePrice()))
				.andExpect(jsonPath("$.responseData.maxAdult").value(SampleRoomDto.getMaxAdult()))
				.andExpect(jsonPath("$.responseData.maxChild").value(SampleRoomDto.getMaxChild()))
				.andExpect(jsonPath("$.responseData.isActive").value(SampleRoomDto.getIsActive()))
				.andExpect(jsonPath("$.responseData.roomType").value(SampleRoomDto.getRoomType().toString()))
				.andExpect(jsonPath("$.responseData.roomStatus").value(SampleRoomDto.getRoomStatus().toString()))
				.andExpect(jsonPath("$.responseData.roomViewOption").value(SampleRoomDto.getRoomViewOption().toString()))
				.andExpect(jsonPath("$.responseData.roomBedOption").value(SampleRoomDto.getRoomBedOption().toString()))
				.andExpect(jsonPath("$.responseData.hotelId").value(SampleRoomDto.getHotelId()))
		;

		verify(roomService, times(1)).deleteById(1L);
	}

	@Test
	@DisplayName("GET /api/v1/room/hotel/{hotelId} - 호텔별 객실 페이징 조회 성공")
	void page_shouldReturnOk() throws Exception {
		// given
		Page<RoomDto> pageResult = new PageImpl<>(
				List.of(SampleRoomDto),
				PageRequest.of(0, 10),
				1
		);
		when(roomService.findAllByHotelIdEquals(eq(10L), any())).thenReturn(pageResult);

		// when & then
		mockMvc.perform(get("/api/v1/room/hotel/{hotelId}", 10L)
						.param("page", "0")
						.param("size", "10"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.responseData.content[0].roomId").value(SampleRoomDto.getRoomId()))
				.andExpect(jsonPath("$.responseData.content[0].name").value(SampleRoomDto.getName()))
				.andExpect(jsonPath("$.responseData.content[0].number").value(SampleRoomDto.getNumber()))
				.andExpect(jsonPath("$.responseData.content[0].floor").value(SampleRoomDto.getFloor()))
				.andExpect(jsonPath("$.responseData.content[0].size").value(SampleRoomDto.getSize()))
				.andExpect(jsonPath("$.responseData.content[0].basePrice").value(SampleRoomDto.getBasePrice()))
				.andExpect(jsonPath("$.responseData.content[0].maxAdult").value(SampleRoomDto.getMaxAdult()))
				.andExpect(jsonPath("$.responseData.content[0].maxChild").value(SampleRoomDto.getMaxChild()))
				.andExpect(jsonPath("$.responseData.content[0].isActive").value(SampleRoomDto.getIsActive()))
				.andExpect(jsonPath("$.responseData.content[0].roomType").value(SampleRoomDto.getRoomType().toString()))
				.andExpect(jsonPath("$.responseData.content[0].roomStatus").value(SampleRoomDto.getRoomStatus().toString()))
				.andExpect(jsonPath("$.responseData.content[0].roomViewOption").value(SampleRoomDto.getRoomViewOption().toString()))
				.andExpect(jsonPath("$.responseData.content[0].roomBedOption").value(SampleRoomDto.getRoomBedOption().toString()))
				.andExpect(jsonPath("$.responseData.content[0].hotelId").value(SampleRoomDto.getHotelId()))
				.andExpect(jsonPath("$.responseData.totalElements").value(1));

		verify(roomService, times(1)).findAllByHotelIdEquals(eq(10L), any());
	}
}