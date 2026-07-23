package com.mjc.hotel.rates.controller;

import com.mjc.hotel.common.ApiResponse;
import com.mjc.hotel.common.ResponseCode;
import com.mjc.hotel.room_images.dto.RoomImageResponseDto;
import com.mjc.hotel.rates.repository.RoomRatesRepository;
import com.mjc.hotel.rates.service.RoomRatesImageService;
import com.mjc.hotel.rooms.dto.RoomEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.NoSuchElementException;

/**
 * 객실 이미지 추가 업로드 및 개별 삭제 전담 REST Controller
 */
@RestController
@RequestMapping("/api/rates/rooms")
@RequiredArgsConstructor
public class RoomRatesImageController {

    private final RoomRatesImageService roomRatesImageService;
    private final RoomRatesRepository roomRatesRepository;

    /**
     * [POST] /api/rates/rooms/{roomId}/images (객실 이미지 추가 업로드)
     */
    @PostMapping("/{roomId}/images")
    public ResponseEntity<ApiResponse<List<RoomImageResponseDto>>> uploadImages(
            @PathVariable Long roomId,
            @RequestParam("files") List<MultipartFile> files) {

        RoomEntity room = roomRatesRepository.findByRoomIdAndIsActiveTrue(roomId)
                .orElseThrow(() -> new NoSuchElementException("해당 호실을 찾을 수 없습니다. ID: " + roomId));

        List<RoomImageResponseDto> responseData = roomRatesImageService.uploadAndInsertMultiple(room, files);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.make(ResponseCode.INSERT_OK, "이미지가 성공적으로 업로드되었습니다.", responseData));
    }

    /**
     * [DELETE] /api/rates/rooms/{roomId}/images/{imageId} (객실 이미지 개별 삭제)
     */
    @DeleteMapping("/{roomId}/images/{imageId}")
    public ResponseEntity<ApiResponse<Void>> deleteImage(
            @PathVariable Long roomId,
            @PathVariable Long imageId) {

        boolean isDeleted = roomRatesImageService.deleteImage(roomId, imageId);
        if (!isDeleted) {
            throw new NoSuchElementException("삭제할 이미지를 찾을 수 없습니다. Image ID: " + imageId);
        }
        return ResponseEntity.ok(ApiResponse.make(ResponseCode.DELETE_OK, "이미지가 성공적으로 삭제되었습니다.", null));
    }
}
