package com.mjc.hotel.room_images.controller;

import com.mjc.hotel.common.ApiResponse;
import com.mjc.hotel.common.ResponseCode;
import com.mjc.hotel.room_images.dto.RoomImageDto;
import com.mjc.hotel.room_images.service.RoomImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/roomimage")
public class RoomImageRestController {
	@Autowired
	private RoomImageService roomImageService;

	@PostMapping
	public ResponseEntity<ApiResponse<RoomImageDto>> insert(@RequestBody RoomImageDto requestDto) {
		RoomImageDto resultDto = this.roomImageService.insert(requestDto);
		return ResponseEntity.status(HttpStatus.CREATED).body(
				ApiResponse.make(ResponseCode.INSERT_OK, "ok", resultDto)
		);
	}

	@PatchMapping
	public ResponseEntity<ApiResponse<RoomImageDto>> update(@RequestBody RoomImageDto requestDto) {
		RoomImageDto resultDto = this.roomImageService.update(requestDto);
		return ResponseEntity.status(HttpStatus.OK).body(
				ApiResponse.make(ResponseCode.UPDATE_OK, "ok", resultDto)
		);
	}

	@GetMapping("/{roomId}")
	public ResponseEntity<ApiResponse<RoomImageDto>> findById(@PathVariable Long roomId) {
		RoomImageDto resultDto = this.roomImageService.findById(roomId);
		return ResponseEntity.status(HttpStatus.OK).body(
				ApiResponse.make(ResponseCode.SELECT_OK, "ok", resultDto)
		);
	}

	@DeleteMapping("/{roomId}")
	public ResponseEntity<ApiResponse<RoomImageDto>> deleteById(@PathVariable Long roomId) {
		RoomImageDto resultDto = this.roomImageService.deleteById(roomId);
		return ResponseEntity.status(HttpStatus.OK).body(
				ApiResponse.make(ResponseCode.DELETE_OK, "ok", resultDto)
		);
	}

	@GetMapping("/hotel/{roomId}")
	public ResponseEntity<ApiResponse<Page<RoomImageDto>>> page(@PathVariable Long roomId
			, @PageableDefault(size=10, page=0, sort="id", direction= Sort.Direction.DESC) Pageable pageable) {
		Page<RoomImageDto> page = this.roomImageService.findAllByRoomIdEquals(roomId, pageable);
		return ResponseEntity.status(HttpStatus.OK).body(
				ApiResponse.make(ResponseCode.SELECT_OK, "ok", page)
		);
	}
}
