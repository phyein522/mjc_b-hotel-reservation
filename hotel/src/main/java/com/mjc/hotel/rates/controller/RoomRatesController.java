package com.mjc.hotel.rates.controller;

import com.mjc.hotel.common.ApiResponse;
import com.mjc.hotel.common.ResponseCode;
import com.mjc.hotel.rates.dto.request.RoomCreateRequestDto;
import com.mjc.hotel.rates.dto.request.RoomUpdateRequestDto;
import com.mjc.hotel.rates.dto.response.RoomDetailResponseDto;
import com.mjc.hotel.rates.dto.response.RoomListResponseDto;
import com.mjc.hotel.rates.service.RoomRatesService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 호실 CRUD 및 상세설명/편의시설/이미지 통합 관리 REST Controller
 */
@RestController
@RequestMapping("/api/rates")
@RequiredArgsConstructor
public class RoomRatesController {

    private final RoomRatesService roomRatesService;

    /**
     * [POST] /api/rates/hotels/{hotelId}/rooms (JSON 전용 호실 등록)
     */
    @PostMapping(value = "/hotels/{hotelId}/rooms", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<RoomDetailResponseDto>> createRoomJson(
            @PathVariable Long hotelId,
            @Valid @RequestBody RoomCreateRequestDto requestDto) {
        RoomDetailResponseDto responseData = roomRatesService.createRoomWithDetails(hotelId, requestDto, null);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.make(ResponseCode.INSERT_OK, "호실이 성공적으로 등록되었습니다.", responseData));
    }

    /**
     * [POST] /api/rates/hotels/{hotelId}/rooms (Multipart 이미지 첨부 포함 호실 등록)
     */
    @PostMapping(value = "/hotels/{hotelId}/rooms", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<RoomDetailResponseDto>> createRoomMultipart(
            @PathVariable Long hotelId,
            @Valid @RequestPart("room") RoomCreateRequestDto requestDto,
            @RequestPart(value = "files", required = false) List<MultipartFile> files) {
        RoomDetailResponseDto responseData = roomRatesService.createRoomWithDetails(hotelId, requestDto, files);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.make(ResponseCode.INSERT_OK, "호실이 성공적으로 등록되었습니다.", responseData));
    }

    /**
     * [GET] /api/rates/hotels/{hotelId}/rooms (호실 목록 조회 - 페이징)
     */
    @GetMapping("/hotels/{hotelId}/rooms")
    public ResponseEntity<ApiResponse<Page<RoomListResponseDto>>> getRoomList(
            @PathVariable Long hotelId,
            @PageableDefault(size = 10) Pageable pageable) {
        Page<RoomListResponseDto> responseData = roomRatesService.getRoomList(hotelId, pageable);
        return ResponseEntity.ok(ApiResponse.make(ResponseCode.SELECT_OK, "ok", responseData));
    }

    /**
     * [GET] /api/rates/rooms/{roomId} (호실 상세 조회)
     */
    @GetMapping("/rooms/{roomId}")
    public ResponseEntity<ApiResponse<RoomDetailResponseDto>> getRoomDetail(
            @PathVariable Long roomId) {
        RoomDetailResponseDto responseData = roomRatesService.getRoomDetail(roomId);
        return ResponseEntity.ok(ApiResponse.make(ResponseCode.SELECT_OK, "ok", responseData));
    }

    /**
     * [PUT] /api/rates/rooms/{roomId} (호실 정보, 상세설명 및 편의시설 수정)
     */
    @PutMapping("/rooms/{roomId}")
    public ResponseEntity<ApiResponse<RoomDetailResponseDto>> updateRoom(
            @PathVariable Long roomId,
            @Valid @RequestBody RoomUpdateRequestDto requestDto) {
        RoomDetailResponseDto responseData = roomRatesService.updateRoom(roomId, requestDto);
        return ResponseEntity.ok(ApiResponse.make(ResponseCode.UPDATE_OK, "호실 정보가 성공적으로 수정되었습니다.", responseData));
    }

    /**
     * [DELETE] /api/rates/rooms/{roomId} (호실 Soft Delete)
     */
    @DeleteMapping("/rooms/{roomId}")
    public ResponseEntity<ApiResponse<Void>> deleteRoom(
            @PathVariable Long roomId) {
        roomRatesService.deleteRoom(roomId);
        return ResponseEntity.ok(ApiResponse.make(ResponseCode.DELETE_OK, "호실이 성공적으로 삭제되었습니다.", null));
    }
}
