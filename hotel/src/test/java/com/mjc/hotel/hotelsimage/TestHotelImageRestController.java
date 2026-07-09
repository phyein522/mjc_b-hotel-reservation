package com.mjc.hotel.hotelsimage;

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
import org.springframework.http.HttpMethod;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
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

@WebMvcTest(HotelImageRestController.class)
public class TestHotelImageRestController {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private HotelImageService hotelImageService;

	@Autowired
	private ObjectMapper objectMapper;

	private static HotelImageResponseDto SampleHotelImageResponseDto;
	private static HotelImageDto SampleHotelImageDto;

	@BeforeEach
	void setUp() {
		SampleHotelImageResponseDto = HotelImageResponseDto.builder()
				.hotelImageId(1L)
				.fileName("hotel1.jpg")
				.size(204800)
				.ext("jpg")
				.storeName("hotel-store-name.jpg")
				.path("/upload/hotel/1")
				.hotelId(10L)
				.build();

		SampleHotelImageDto = HotelImageDto.builder()
				.hotelImageId(1L)
				.fileName("hotel1.jpg")
				.size(204800)
				.ext("jpg")
				.storeName("hotel-store-name.jpg")
				.path("/upload/hotel/1")
				.hotelId(10L)
				.build();
	}

	@Test
	@DisplayName("POST /api/hotelimage - 호텔 이미지 등록 성공")
	void insert_shouldReturnCreated() throws Exception {
		when(hotelImageService.insert(any(HotelImageRequestDto.class))).thenReturn(SampleHotelImageResponseDto);

		MockMultipartFile requestDtoPart = new MockMultipartFile(
				"requestDto",
				"requestDto",
				"application/json",
				objectMapper.writeValueAsBytes(SampleHotelImageResponseDto)
		);
		MockMultipartFile filePart = new MockMultipartFile(
				"file",
				"hotel1.jpg",
				"image/jpeg",
				"dummy-image-content".getBytes(StandardCharsets.UTF_8)
		);

		mockMvc.perform(multipart("/api/hotelimage")
						.file(requestDtoPart)
						.file(filePart))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.responseData.hotelImageId").value(SampleHotelImageResponseDto.getHotelImageId()))
				.andExpect(jsonPath("$.responseData.fileName").value(SampleHotelImageResponseDto.getFileName()))
				.andExpect(jsonPath("$.responseData.size").value(SampleHotelImageResponseDto.getSize()))
				.andExpect(jsonPath("$.responseData.ext").value(SampleHotelImageResponseDto.getExt()))
				.andExpect(jsonPath("$.responseData.storeName").value(SampleHotelImageResponseDto.getStoreName()))
				.andExpect(jsonPath("$.responseData.path").value(SampleHotelImageResponseDto.getPath()))
				.andExpect(jsonPath("$.responseData.hotelId").value(SampleHotelImageResponseDto.getHotelId()));

		verify(hotelImageService, times(1)).insert(any(HotelImageRequestDto.class));
	}

	@Test
	@DisplayName("POST /api/hotelimage/hotel/{hotelId} - 업로드 후 등록 성공")
	void uploadAndInsert_shouldReturnCreated() throws Exception {
		when(hotelImageService.uploadAndInsert(eq(10L), any())).thenReturn(SampleHotelImageResponseDto);

		MockMultipartFile filePart = new MockMultipartFile(
				"file",
				"hotel1.jpg",
				"image/jpeg",
				"dummy-image-content".getBytes(StandardCharsets.UTF_8)
		);

		mockMvc.perform(multipart("/api/hotelimage/hotel/{hotelId}", 10L)
						.file(filePart))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.responseData.hotelImageId").value(SampleHotelImageResponseDto.getHotelImageId()))
				.andExpect(jsonPath("$.responseData.fileName").value(SampleHotelImageResponseDto.getFileName()))
				.andExpect(jsonPath("$.responseData.size").value(SampleHotelImageResponseDto.getSize()))
				.andExpect(jsonPath("$.responseData.ext").value(SampleHotelImageResponseDto.getExt()))
				.andExpect(jsonPath("$.responseData.storeName").value(SampleHotelImageResponseDto.getStoreName()))
				.andExpect(jsonPath("$.responseData.path").value(SampleHotelImageResponseDto.getPath()))
				.andExpect(jsonPath("$.responseData.hotelId").value(SampleHotelImageResponseDto.getHotelId()));

		verify(hotelImageService, times(1)).uploadAndInsert(eq(10L), any());
	}

	@Test
	@DisplayName("PATCH /api/hotelimage - 호텔 이미지 수정 성공")
	void update_shouldReturnOk() throws Exception {
		SampleHotelImageResponseDto.setFileName("updated-hotel.jpg");
		HotelImageRequestDto requestDto = new HotelImageRequestDto();
		requestDto.copyMembers(SampleHotelImageResponseDto, true);

		when(hotelImageService.update(any(HotelImageRequestDto.class))).thenReturn(SampleHotelImageResponseDto);

		mockMvc.perform(patch("/api/hotelimage")
						.contentType("application/json")
						.content(objectMapper.writeValueAsString(requestDto)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.responseData.hotelImageId").value(SampleHotelImageResponseDto.getHotelImageId()))
				.andExpect(jsonPath("$.responseData.fileName").value(SampleHotelImageResponseDto.getFileName()))
				.andExpect(jsonPath("$.responseData.size").value(SampleHotelImageResponseDto.getSize()))
				.andExpect(jsonPath("$.responseData.ext").value(SampleHotelImageResponseDto.getExt()))
				.andExpect(jsonPath("$.responseData.storeName").value(SampleHotelImageResponseDto.getStoreName()))
				.andExpect(jsonPath("$.responseData.path").value(SampleHotelImageResponseDto.getPath()))
				.andExpect(jsonPath("$.responseData.hotelId").value(SampleHotelImageResponseDto.getHotelId()));

		verify(hotelImageService, times(1)).update(any(HotelImageRequestDto.class));
	}

	@Test
	@DisplayName("PATCH /api/hotelimage/image/{hotelImageId} - 업로드 후 수정 성공")
	void uploadAndUpdate_shouldReturnCreated() throws Exception {
		when(hotelImageService.uploadAndUpdate(eq(1L), any())).thenReturn(SampleHotelImageResponseDto);

		MockMultipartFile filePart = new MockMultipartFile(
				"file",
				"hotel1-new.jpg",
				"image/jpeg",
				"dummy-image-content".getBytes(StandardCharsets.UTF_8)
		);

		mockMvc.perform(multipart(HttpMethod.PATCH, "/api/hotelimage/image/{hotelImageId}", 1L)
						.file(filePart))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.responseData.hotelImageId").value(SampleHotelImageResponseDto.getHotelImageId()))
				.andExpect(jsonPath("$.responseData.fileName").value(SampleHotelImageResponseDto.getFileName()))
				.andExpect(jsonPath("$.responseData.size").value(SampleHotelImageResponseDto.getSize()))
				.andExpect(jsonPath("$.responseData.ext").value(SampleHotelImageResponseDto.getExt()))
				.andExpect(jsonPath("$.responseData.storeName").value(SampleHotelImageResponseDto.getStoreName()))
				.andExpect(jsonPath("$.responseData.path").value(SampleHotelImageResponseDto.getPath()))
				.andExpect(jsonPath("$.responseData.hotelId").value(SampleHotelImageResponseDto.getHotelId()));

		verify(hotelImageService, times(1)).uploadAndUpdate(eq(1L), any());
	}

	@Test
	@DisplayName("GET /api/hotelimage/{hotelImageId} - 단건 조회 성공")
	void findById_shouldReturnOk() throws Exception {
		when(hotelImageService.findById(1L)).thenReturn(SampleHotelImageResponseDto);

		mockMvc.perform(get("/api/hotelimage/{hotelImageId}", 1L))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.responseData.hotelImageId").value(SampleHotelImageResponseDto.getHotelImageId()))
				.andExpect(jsonPath("$.responseData.fileName").value(SampleHotelImageResponseDto.getFileName()))
				.andExpect(jsonPath("$.responseData.size").value(SampleHotelImageResponseDto.getSize()))
				.andExpect(jsonPath("$.responseData.ext").value(SampleHotelImageResponseDto.getExt()))
				.andExpect(jsonPath("$.responseData.storeName").value(SampleHotelImageResponseDto.getStoreName()))
				.andExpect(jsonPath("$.responseData.path").value(SampleHotelImageResponseDto.getPath()))
				.andExpect(jsonPath("$.responseData.hotelId").value(SampleHotelImageResponseDto.getHotelId()))
				.andExpect(jsonPath("$.responseData.imageUrl").value(SampleHotelImageResponseDto.getImageUrl()));

		verify(hotelImageService, times(1)).findById(1L);
	}

	@Test
	@DisplayName("GET /api/hotelimage/download/{hotelImageId} - 이미지 다운로드 성공")
	void downloadImageById_shouldReturnOk() throws Exception {
		byte[] imageBytes = "dummy-image-content".getBytes(StandardCharsets.UTF_8);
		when(hotelImageService.findById(1L)).thenReturn(SampleHotelImageResponseDto);
		when(hotelImageService.getImageBytesById(any(HotelImageDto.class))).thenReturn(imageBytes);

		String expectedFileName = URLEncoder.encode(SampleHotelImageResponseDto.getFileName(), StandardCharsets.UTF_8);

		mockMvc.perform(get("/api/hotelimage/download/{hotelImageId}", 1L))
				.andExpect(status().isOk())
				.andExpect(header().string("Content-type", "application/octet-stream"))
				.andExpect(header().string("Content-disposition", "attachment; filename=\"" + expectedFileName + "\""));

		verify(hotelImageService, times(1)).findById(1L);
		verify(hotelImageService, times(1)).getImageBytesById(any(HotelImageDto.class));
	}

	@Test
	@DisplayName("GET /api/hotelimage/image/{hotelImageId} - 이미지 리소스 조회 성공")
	void resourceImageById_shouldReturnOk() throws Exception {
		Resource imageResource = new ByteArrayResource("dummy-image-content".getBytes(StandardCharsets.UTF_8));
		when(hotelImageService.findById(1L)).thenReturn(SampleHotelImageResponseDto);
		when(hotelImageService.getImageResourceById(any(HotelImageDto.class))).thenReturn(imageResource);

		mockMvc.perform(get("/api/hotelimage/image/{hotelImageId}", 1L))
				.andExpect(status().isOk())
				.andExpect(header().string("Content-type", "image/" + SampleHotelImageResponseDto.getExt()));

		verify(hotelImageService, times(1)).findById(1L);
		verify(hotelImageService, times(1)).getImageResourceById(any(HotelImageDto.class));
	}

	@Test
	@DisplayName("DELETE /api/hotelimage/{hotelImageId} - 삭제 성공")
	void deleteById_shouldReturnOk() throws Exception {
		when(hotelImageService.deleteById(1L)).thenReturn(SampleHotelImageDto);

		mockMvc.perform(delete("/api/hotelimage/{hotelImageId}", 1L))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.responseData.hotelImageId").value(SampleHotelImageDto.getHotelImageId()))
				.andExpect(jsonPath("$.responseData.fileName").value(SampleHotelImageDto.getFileName()))
				.andExpect(jsonPath("$.responseData.size").value(SampleHotelImageDto.getSize()))
				.andExpect(jsonPath("$.responseData.ext").value(SampleHotelImageDto.getExt()))
				.andExpect(jsonPath("$.responseData.storeName").value(SampleHotelImageDto.getStoreName()))
				.andExpect(jsonPath("$.responseData.path").value(SampleHotelImageDto.getPath()))
				.andExpect(jsonPath("$.responseData.hotelId").value(SampleHotelImageDto.getHotelId()));

		verify(hotelImageService, times(1)).deleteById(1L);
	}

	@Test
	@DisplayName("GET /api/hotelimage/hotel/{hotelId} - 호텔별 이미지 페이징 조회 성공")
	void page_shouldReturnOk() throws Exception {
		Page<HotelImageResponseDto> pageResult = new PageImpl<>(
				List.of(SampleHotelImageResponseDto),
				PageRequest.of(0, 10),
				1
		);
		when(hotelImageService.findAllByHotelIdEquals(eq(10L), any())).thenReturn(pageResult);

		mockMvc.perform(get("/api/hotelimage/hotel/{hotelId}", 10L)
						.param("page", "0")
						.param("size", "10"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.responseData.content[0].hotelImageId").value(SampleHotelImageResponseDto.getHotelImageId()))
				.andExpect(jsonPath("$.responseData.content[0].fileName").value(SampleHotelImageResponseDto.getFileName()))
				.andExpect(jsonPath("$.responseData.content[0].size").value(SampleHotelImageResponseDto.getSize()))
				.andExpect(jsonPath("$.responseData.content[0].ext").value(SampleHotelImageResponseDto.getExt()))
				.andExpect(jsonPath("$.responseData.content[0].storeName").value(SampleHotelImageResponseDto.getStoreName()))
				.andExpect(jsonPath("$.responseData.content[0].path").value(SampleHotelImageResponseDto.getPath()))
				.andExpect(jsonPath("$.responseData.content[0].hotelId").value(SampleHotelImageResponseDto.getHotelId()))
				.andExpect(jsonPath("$.responseData.totalElements").value(1));

		verify(hotelImageService, times(1)).findAllByHotelIdEquals(eq(10L), any());
	}
}
