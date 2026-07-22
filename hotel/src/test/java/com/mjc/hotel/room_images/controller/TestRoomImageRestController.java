package com.mjc.hotel.room_images.controller;

import com.mjc.hotel.room_images.dto.RoomImageDto;
import com.mjc.hotel.room_images.dto.RoomImageListDto;
import com.mjc.hotel.room_images.dto.RoomImageRequestDto;
import com.mjc.hotel.room_images.dto.RoomImageResponseDto;
import com.mjc.hotel.room_images.service.RoomImageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.http.HttpMethod;
import tools.jackson.databind.ObjectMapper;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * RoomImageRestController 의 모든 URL Mapping 메소드에 대한 단위 테스트
 *
 * @WebMvcTest 를 사용하여 웹 계층만 슬라이스 테스트하고,
 * RoomImageService 는 @MockitoBean 으로 목(mock) 처리한다.
 *
 * ApiResponse 의 실제 페이로드 필드명은 "responseData" 이다.
 * (예: {"responseCode":"SELECT_OK","message":"ok","responseData":{...}})
 */
@WebMvcTest(RoomImageRestController.class)
public class TestRoomImageRestController {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private RoomImageService roomImageService;

	@Autowired
	private ObjectMapper objectMapper;

	private static RoomImageResponseDto SampleRoomImageResponseDto;
	private static RoomImageListDto SampleRoomImageListDto;
	private static RoomImageDto SampleRoomImageDto;

	@BeforeEach
	void setUp() {
		SampleRoomImageResponseDto = RoomImageResponseDto.builder()
				.roomImageId(1L)
				.fileName("room1.jpg")
				.size(102400)
				.ext("jpg")
				.storeName("uuid-store-name.jpg")
				.path("/upload/room/1")
				.roomId(10L)
				.build();

		SampleRoomImageDto = RoomImageDto.builder()
				.roomImageId(1L)
				.fileName("room1.jpg")
				.size(102400)
				.ext("jpg")
				.storeName("uuid-store-name.jpg")
				.path("/upload/room/1")
				.roomId(10L)
				.build();

		SampleRoomImageListDto = RoomImageListDto.builder()
				.roomImageId(1L)
				.fileName("room1.jpg")
				.size(102400)
				.ext("jpg")
				.storeName("uuid-store-name.jpg")
				.path("/upload/room/1")
				.roomId(10L)
				.build();
	}

	@Test
	@DisplayName("POST /api/roomimage - 객실 이미지 등록 성공")
	void insert_shouldReturnCreated() throws Exception {
		// given
		when(roomImageService.insert(any(RoomImageRequestDto.class))).thenReturn(SampleRoomImageResponseDto);

		MockMultipartFile requestDtoPart = new MockMultipartFile(
				"requestDto",
				"requestDto",
				"application/json",
				objectMapper.writeValueAsBytes(SampleRoomImageResponseDto)
		);

		MockMultipartFile filePart = new MockMultipartFile(
				"file",
				"room1.jpg",
				"image/jpeg",
				"dummy-image-content".getBytes()
		);

		// when & then
		mockMvc.perform(multipart("/api/roomimage")
						.file(requestDtoPart)
						.file(filePart))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.responseData.roomImageId").value(SampleRoomImageResponseDto.getRoomImageId()))
				.andExpect(jsonPath("$.responseData.fileName").value(SampleRoomImageResponseDto.getFileName()))
				.andExpect(jsonPath("$.responseData.size").value(SampleRoomImageResponseDto.getSize()))
				.andExpect(jsonPath("$.responseData.ext").value(SampleRoomImageResponseDto.getExt()))
				.andExpect(jsonPath("$.responseData.storeName").value(SampleRoomImageResponseDto.getStoreName()))
				.andExpect(jsonPath("$.responseData.path").value(SampleRoomImageResponseDto.getPath()))
				.andExpect(jsonPath("$.responseData.roomId").value(SampleRoomImageResponseDto.getRoomId()))
		;

		verify(roomImageService, times(1)).insert(any(RoomImageRequestDto.class));
	}

	@Test
	@DisplayName("POST /api/roomimage/room/{roomId} - 업로드 후 등록 성공")
	void uploadAndInsert_shouldReturnCreated() throws Exception {
		// given
		when(roomImageService.uploadAndInsert(eq(10L), any())).thenReturn(SampleRoomImageResponseDto);

		MockMultipartFile filePart = new MockMultipartFile(
				"file",
				"room1.jpg",
				"image/jpeg",
				"dummy-image-content".getBytes()
		);

		// when & then
		mockMvc.perform(multipart("/api/roomimage/room/{roomId}", 10L)
						.file(filePart))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.responseData.roomImageId").value(SampleRoomImageDto.getRoomImageId()))
				.andExpect(jsonPath("$.responseData.fileName").value(SampleRoomImageDto.getFileName()))
				.andExpect(jsonPath("$.responseData.size").value(SampleRoomImageDto.getSize()))
				.andExpect(jsonPath("$.responseData.ext").value(SampleRoomImageDto.getExt()))
				.andExpect(jsonPath("$.responseData.storeName").value(SampleRoomImageDto.getStoreName()))
				.andExpect(jsonPath("$.responseData.path").value(SampleRoomImageDto.getPath()))
				.andExpect(jsonPath("$.responseData.roomId").value(SampleRoomImageDto.getRoomId()))
		;

		verify(roomImageService, times(1)).uploadAndInsert(eq(10L), any());
	}

	@Test
	@DisplayName("PATCH /api/roomimage - 객실 이미지 수정 성공")
	void update_shouldReturnOk() throws Exception {
		// given
		SampleRoomImageDto.setFileName("수정된파일명.jpg");
		SampleRoomImageResponseDto.setFileName(SampleRoomImageDto.getFileName());
		when(roomImageService.update(any(RoomImageRequestDto.class))).thenReturn(SampleRoomImageResponseDto);

		RoomImageRequestDto requestDto = RoomImageRequestDto.builder()
				.roomImageId(1L)
				.fileName(SampleRoomImageDto.getFileName())
				.size(102400)
				.ext("jpg")
				.storeName("uuid-store-name.jpg")
				.path("/upload/room/1")
				.roomId(10L)
				.build();

		// when & then
		mockMvc.perform(patch("/api/roomimage")
						.contentType("application/json")
						.content(objectMapper.writeValueAsString(requestDto)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.responseData.roomImageId").value(SampleRoomImageDto.getRoomImageId()))
				.andExpect(jsonPath("$.responseData.fileName").value(SampleRoomImageDto.getFileName()))
				.andExpect(jsonPath("$.responseData.size").value(SampleRoomImageDto.getSize()))
				.andExpect(jsonPath("$.responseData.ext").value(SampleRoomImageDto.getExt()))
				.andExpect(jsonPath("$.responseData.storeName").value(SampleRoomImageDto.getStoreName()))
				.andExpect(jsonPath("$.responseData.path").value(SampleRoomImageDto.getPath()))
				.andExpect(jsonPath("$.responseData.roomId").value(SampleRoomImageDto.getRoomId()))
		;

		verify(roomImageService, times(1)).update(any(RoomImageRequestDto.class));
	}

	@Test
	@DisplayName("PATCH /api/roomimage/image/{roomImageId} - 업로드 후 수정 성공")
	void uploadAndUpdate_shouldReturnCreated() throws Exception {
		// given
		when(roomImageService.uploadAndUpdate(eq(1L), any())).thenReturn(SampleRoomImageResponseDto);

		MockMultipartFile filePart = new MockMultipartFile(
				"file",
				"room1_new.jpg",
				"image/jpeg",
				"dummy-image-content".getBytes()
		);

		// when & then
		mockMvc.perform(multipart(HttpMethod.PATCH, "/api/roomimage/image/{roomImageId}", 1L)
						.file(filePart))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.responseData.roomImageId").value(SampleRoomImageDto.getRoomImageId()))
				.andExpect(jsonPath("$.responseData.fileName").value(SampleRoomImageDto.getFileName()))
				.andExpect(jsonPath("$.responseData.size").value(SampleRoomImageDto.getSize()))
				.andExpect(jsonPath("$.responseData.ext").value(SampleRoomImageDto.getExt()))
				.andExpect(jsonPath("$.responseData.storeName").value(SampleRoomImageDto.getStoreName()))
				.andExpect(jsonPath("$.responseData.path").value(SampleRoomImageDto.getPath()))
				.andExpect(jsonPath("$.responseData.roomId").value(SampleRoomImageDto.getRoomId()))
		;

		verify(roomImageService, times(1)).uploadAndUpdate(eq(1L), any());
	}

	@Test
	@DisplayName("GET /api/roomimage/{roomImageId} - 단건 조회 성공")
	void findById_shouldReturnOk() throws Exception {
		// given
		when(roomImageService.findById(1L)).thenReturn(SampleRoomImageResponseDto);

		// when & then
		mockMvc.perform(get("/api/roomimage/{roomImageId}", 1L))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.responseData.roomImageId").value(SampleRoomImageResponseDto.getRoomImageId()))
				.andExpect(jsonPath("$.responseData.fileName").value(SampleRoomImageResponseDto.getFileName()))
				.andExpect(jsonPath("$.responseData.size").value(SampleRoomImageResponseDto.getSize()))
				.andExpect(jsonPath("$.responseData.ext").value(SampleRoomImageResponseDto.getExt()))
				.andExpect(jsonPath("$.responseData.storeName").value(SampleRoomImageResponseDto.getStoreName()))
				.andExpect(jsonPath("$.responseData.path").value(SampleRoomImageResponseDto.getPath()))
				.andExpect(jsonPath("$.responseData.roomId").value(SampleRoomImageResponseDto.getRoomId()))
				.andExpect(jsonPath("$.responseData.imageUrl").value(SampleRoomImageResponseDto.getImageUrl()))
		;

		verify(roomImageService, times(1)).findById(1L);
	}

	@Test
	@DisplayName("GET /api/roomimage/download/{roomImageId} - 이미지 다운로드 성공")
	void downloadImageById_shouldReturnOk() throws Exception {
		// given
		byte[] imageBytes = "dummy-image-content".getBytes(StandardCharsets.UTF_8);
		when(roomImageService.findById(1L)).thenReturn(SampleRoomImageResponseDto);
		when(roomImageService.getImageBytesById(any(RoomImageResponseDto.class))).thenReturn(imageBytes);

		String expectedFileName = URLEncoder.encode(SampleRoomImageResponseDto.getFileName(), StandardCharsets.UTF_8);

		// when & then
		mockMvc.perform(get("/api/roomimage/download/{roomImageId}", 1L))
				.andExpect(status().isOk())
				.andExpect(header().string("Content-type", "application/octet-stream"))
				.andExpect(header().string("Content-disposition", "attachment; filename=\"" + expectedFileName + "\""))
		;

		verify(roomImageService, times(1)).findById(1L);
		verify(roomImageService, times(1)).getImageBytesById(any(RoomImageResponseDto.class));
	}

	@Test
	@DisplayName("GET /api/roomimage/image/{roomImageId} - 이미지 리소스 조회 성공")
	void resourceImageById_shouldReturnOk() throws Exception {
		// given
		Resource imageResource = new ByteArrayResource("dummy-image-content".getBytes(StandardCharsets.UTF_8));
		when(roomImageService.findById(1L)).thenReturn(SampleRoomImageResponseDto);
		when(roomImageService.getImageResourceById(any(RoomImageResponseDto.class))).thenReturn(imageResource);

		// when & then
		mockMvc.perform(get("/api/roomimage/image/{roomImageId}", 1L))
				.andExpect(status().isOk())
				.andExpect(header().string("Content-type", "image/" + SampleRoomImageResponseDto.getExt()))
		;

		verify(roomImageService, times(1)).findById(1L);
		verify(roomImageService, times(1)).getImageResourceById(any(RoomImageResponseDto.class));
	}

	@Test
	@DisplayName("DELETE /api/roomimage/{roomImageId} - 삭제 성공")
	void deleteById_shouldReturnOk() throws Exception {
		// given
		when(roomImageService.deleteById(1L)).thenReturn(SampleRoomImageResponseDto);

		// when & then
		mockMvc.perform(delete("/api/roomimage/{roomImageId}", 1L))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.responseData.roomImageId").value(SampleRoomImageResponseDto.getRoomImageId()))
				.andExpect(jsonPath("$.responseData.fileName").value(SampleRoomImageResponseDto.getFileName()))
				.andExpect(jsonPath("$.responseData.size").value(SampleRoomImageResponseDto.getSize()))
				.andExpect(jsonPath("$.responseData.ext").value(SampleRoomImageResponseDto.getExt()))
				.andExpect(jsonPath("$.responseData.storeName").value(SampleRoomImageResponseDto.getStoreName()))
				.andExpect(jsonPath("$.responseData.path").value(SampleRoomImageResponseDto.getPath()))
				.andExpect(jsonPath("$.responseData.roomId").value(SampleRoomImageResponseDto.getRoomId()))
		;

		verify(roomImageService, times(1)).deleteById(1L);
	}

	@Test
	@DisplayName("GET /api/roomimage/hotel/{roomId} - 객실별 이미지 페이징 조회 성공")
	void page_shouldReturnOk() throws Exception {
		// given
		Page<RoomImageResponseDto> pageResult = new PageImpl<>(
				List.of(SampleRoomImageResponseDto),
				PageRequest.of(0, 10),
				1
		);
		when(roomImageService.findAllByRoomIdEquals(eq(10L), any())).thenReturn(pageResult);

		// when & then
		mockMvc.perform(get("/api/roomimage/hotel/{roomId}", 10L)
						.param("page", "0")
						.param("size", "10"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.responseData.content[0].roomImageId").value(SampleRoomImageResponseDto.getRoomImageId()))
				.andExpect(jsonPath("$.responseData.content[0].fileName").value(SampleRoomImageResponseDto.getFileName()))
				.andExpect(jsonPath("$.responseData.content[0].size").value(SampleRoomImageResponseDto.getSize()))
				.andExpect(jsonPath("$.responseData.content[0].ext").value(SampleRoomImageResponseDto.getExt()))
				.andExpect(jsonPath("$.responseData.content[0].storeName").value(SampleRoomImageResponseDto.getStoreName()))
				.andExpect(jsonPath("$.responseData.content[0].path").value(SampleRoomImageResponseDto.getPath()))
				.andExpect(jsonPath("$.responseData.content[0].roomId").value(SampleRoomImageResponseDto.getRoomId()))
				.andExpect(jsonPath("$.responseData.totalElements").value(1));

		verify(roomImageService, times(1)).findAllByRoomIdEquals(eq(10L), any());
	}
}