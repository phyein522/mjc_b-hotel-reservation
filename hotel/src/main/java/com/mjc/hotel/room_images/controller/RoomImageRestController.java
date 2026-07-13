package com.mjc.hotel.room_images.controller;

import com.mjc.hotel.common.ApiResponse;
import com.mjc.hotel.common.ResponseCode;
import com.mjc.hotel.room_images.dto.RoomImageDto;
import com.mjc.hotel.room_images.dto.RoomImageRequestDto;
import com.mjc.hotel.room_images.dto.RoomImageResponseDto;
import com.mjc.hotel.room_images.service.RoomImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping("/api/roomimage")
public class RoomImageRestController {
	@Autowired
	private RoomImageService roomImageService;

	@PostMapping
	public ResponseEntity<ApiResponse<RoomImageResponseDto>> insert(@RequestPart RoomImageRequestDto requestDto, @RequestPart MultipartFile file) {
		RoomImageResponseDto resultDto = this.roomImageService.insert(requestDto);
		return ResponseEntity.status(HttpStatus.CREATED).body(
				ApiResponse.make(ResponseCode.INSERT_OK, "ok", resultDto)
		);
	}

	@PostMapping("/room/{roomId}")
	public ResponseEntity<ApiResponse<RoomImageResponseDto>> uploadAndInsert(@PathVariable Long roomId, @RequestPart MultipartFile file) throws RuntimeException {
		RoomImageResponseDto resultDto = this.roomImageService.uploadAndInsert(roomId, file);
//		result.copyMembers(resultDto, true);
		return ResponseEntity.status(HttpStatus.CREATED).body(
				ApiResponse.make(ResponseCode.INSERT_OK, "ok", resultDto)
		);
	}

	@PatchMapping
	public ResponseEntity<ApiResponse<RoomImageResponseDto>> update(@RequestBody RoomImageRequestDto requestDto) {
		RoomImageResponseDto resultDto = this.roomImageService.update(requestDto);
//		RoomImageResponseDto result = RoomImageResponseDto.builder().build();
//		result.copyMembers(resultDto, true);
//		result.getImageUrl();
		return ResponseEntity.status(HttpStatus.OK).body(
				ApiResponse.make(ResponseCode.UPDATE_OK, "ok", resultDto)
		);
	}

	@PatchMapping("/image/{roomImageId}")
	public ResponseEntity<ApiResponse<RoomImageResponseDto>> uploadAndUpdate(@PathVariable Long roomImageId, @RequestPart MultipartFile file) throws RuntimeException {
		RoomImageResponseDto resultDto = this.roomImageService.uploadAndUpdate(roomImageId, file);
//		RoomImageResponseDto result = RoomImageResponseDto.builder().build();
//		result.copyMembers(resultDto, true);
		return ResponseEntity.status(HttpStatus.CREATED).body(
				ApiResponse.make(ResponseCode.INSERT_OK, "ok", resultDto)
		);
	}

	@GetMapping("/{roomImageId}")
	public ResponseEntity<ApiResponse<RoomImageResponseDto>> findById(@PathVariable Long roomImageId) {
		RoomImageResponseDto resultDto = this.roomImageService.findById(roomImageId);
		return ResponseEntity.status(HttpStatus.OK).body(
				ApiResponse.make(ResponseCode.SELECT_OK, "ok", resultDto)
		);
	}

	@GetMapping("/download/{roomImageId}")
	public ResponseEntity<ByteArrayResource> downloadImageById(@PathVariable Long roomImageId) throws IOException {
		RoomImageResponseDto findDto = this.roomImageService.findById(roomImageId);
		byte[] result = this.roomImageService.getImageBytesById(findDto);
		ByteArrayResource resource = new ByteArrayResource(result);
		return ResponseEntity
				.ok()
				.contentLength(result.length)
				.header("Content-type", "application/octet-stream")
				.header("Content-disposition", "attachment; filename=\"" + URLEncoder.encode(findDto.getFileName(), StandardCharsets.UTF_8) + "\"")
				.body(resource);
	}

	@GetMapping("/image/{roomImageId}")
	public ResponseEntity<Resource> resourceImageById(@PathVariable Long roomImageId) throws IOException {
		RoomImageResponseDto findDto = this.roomImageService.findById(roomImageId);
		Resource result = this.roomImageService.getImageResourceById(findDto);
		String contentType = "image/" + findDto.getExt();
		return ResponseEntity
				.ok()
				.header("Content-type", contentType)
				.body(result);
	}

	@DeleteMapping("/{roomImageId}")
	public ResponseEntity<ApiResponse<RoomImageResponseDto>> deleteById(@PathVariable Long roomImageId) {
		RoomImageResponseDto resultDto = this.roomImageService.deleteById(roomImageId);
		return ResponseEntity.status(HttpStatus.OK).body(
				ApiResponse.make(ResponseCode.DELETE_OK, "ok", resultDto)
		);
	}

	@GetMapping("/hotel/{roomId}")
	public ResponseEntity<ApiResponse<Page<RoomImageResponseDto>>> page(@PathVariable Long roomId
			, @PageableDefault(size=10, page=0, sort="id", direction= Sort.Direction.DESC) Pageable pageable) {
		Page<RoomImageResponseDto> page = this.roomImageService.findAllByRoomIdEquals(roomId, pageable);
		return ResponseEntity.status(HttpStatus.OK).body(
				ApiResponse.make(ResponseCode.SELECT_OK, "ok", page)
		);
	}
}
