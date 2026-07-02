package com.mjc.hotel.rooms.controller;

import com.mjc.hotel.common.ApiResponse;
import com.mjc.hotel.common.ResponseCode;
import com.mjc.hotel.rooms.dto.RoomDto;
import com.mjc.hotel.rooms.dto.RoomResponseWithImagesDto;
import com.mjc.hotel.rooms.service.RoomService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/room")
public class RoomRestController {
	@Autowired
	private RoomService roomService;

	@PostMapping
	public ResponseEntity<ApiResponse<RoomDto>> insert(@RequestBody RoomDto requestDto) {
		RoomDto resultDto = this.roomService.insert(requestDto);
		return ResponseEntity.status(HttpStatus.CREATED).body(
				ApiResponse.make(ResponseCode.INSERT_OK, "ok", resultDto)
		);
	}

	@PostMapping("/image")
	public ResponseEntity<ApiResponse<RoomResponseWithImagesDto>> insertWithImages(@RequestPart(name = "requestDto", required = true) RoomDto requestDto
			, @RequestPart(name = "files", required = false) List<MultipartFile> files) throws IOException {
		RoomResponseWithImagesDto resultDto = this.roomService.insertWithImages(requestDto, files);
		return ResponseEntity.status(HttpStatus.CREATED).body(
				ApiResponse.make(ResponseCode.INSERT_OK, "ok", resultDto)
		);
	}

	@PatchMapping
	public ResponseEntity<ApiResponse<RoomDto>> update(@RequestBody RoomDto requestDto) {
		RoomDto resultDto = this.roomService.update(requestDto);
		return ResponseEntity.status(HttpStatus.OK).body(
				ApiResponse.make(ResponseCode.UPDATE_OK, "ok", resultDto)
		);
	}

	@GetMapping("/{roomId}")
	public ResponseEntity<ApiResponse<RoomDto>> findById(@PathVariable Long roomId) {
		RoomDto resultDto = this.roomService.findById(roomId);
		return ResponseEntity.status(HttpStatus.OK).body(
				ApiResponse.make(ResponseCode.SELECT_OK, "ok", resultDto)
		);
	}

	@GetMapping("/images/{roomId}")
	public ResponseEntity<ApiResponse<RoomResponseWithImagesDto>> findByIdWithImages(@PathVariable Long roomId, @PageableDefault(size=10, page=0, sort="roomImageId", direction= Sort.Direction.DESC) Pageable pageable) {
		RoomResponseWithImagesDto resultDto = this.roomService.findByIdWithImages(roomId, pageable);
		return ResponseEntity.status(HttpStatus.OK).body(
				ApiResponse.make(ResponseCode.SELECT_OK, "ok", resultDto)
		);
	}

	@DeleteMapping("/{roomId}")
	public ResponseEntity<ApiResponse<RoomDto>> deleteById(@PathVariable Long roomId) {
		RoomDto resultDto = this.roomService.deleteById(roomId);
		return ResponseEntity.status(HttpStatus.OK).body(
				ApiResponse.make(ResponseCode.DELETE_OK, "ok", resultDto)
		);
	}

	@GetMapping("/hotel/{hotelId}")
	public ResponseEntity<ApiResponse<Page<RoomDto>>> page(@PathVariable Long hotelId
			, @PageableDefault(size=10, page=0, sort="id", direction= Sort.Direction.DESC) Pageable pageable) {
		Page<RoomDto> page = this.roomService.findAllByHotelIdEquals(hotelId, pageable);
		return ResponseEntity.status(HttpStatus.OK).body(
				ApiResponse.make(ResponseCode.SELECT_OK, "ok", page)
		);
	}
}
