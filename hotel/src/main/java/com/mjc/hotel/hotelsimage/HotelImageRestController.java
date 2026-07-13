package com.mjc.hotel.hotelsimage;

import com.mjc.hotel.common.ApiResponse;
import com.mjc.hotel.common.ResponseCode;
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
@RequestMapping("/api/hotelimage")
public class HotelImageRestController {
    @Autowired
    private HotelImageService hotelImageService;

    @PostMapping
    public ResponseEntity<ApiResponse<HotelImageResponseDto>> insert(@RequestPart HotelImageRequestDto requestDto, @RequestPart MultipartFile file) {
        HotelImageResponseDto resultDto = this.hotelImageService.insert(requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.make(ResponseCode.INSERT_OK, "ok", resultDto)
        );
    }

    @PostMapping("/hotel/{hotelId}")
    public ResponseEntity<ApiResponse<HotelImageResponseDto>> uploadAndInsert(@PathVariable Long hotelId, @RequestPart MultipartFile file) throws RuntimeException {
        HotelImageResponseDto resultDto = this.hotelImageService.uploadAndInsert(hotelId, file);
        HotelImageResponseDto result = HotelImageResponseDto.builder().build();
        result.copyMembers(resultDto, true);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.make(ResponseCode.INSERT_OK, "ok", result)
        );
    }

    @PatchMapping
    public ResponseEntity<ApiResponse<HotelImageResponseDto>> update(@RequestBody HotelImageRequestDto requestDto) {
        HotelImageResponseDto resultDto = this.hotelImageService.update(requestDto);
        HotelImageResponseDto result = HotelImageResponseDto.builder().build();
        result.copyMembers(resultDto, true);
        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.make(ResponseCode.UPDATE_OK, "ok", result)
        );
    }

    @PatchMapping("/image/{hotelImageId}")
    public ResponseEntity<ApiResponse<HotelImageResponseDto>> uploadAndUpdate(@PathVariable Long hotelImageId, @RequestPart MultipartFile file) throws RuntimeException {
        HotelImageResponseDto resultDto = this.hotelImageService.uploadAndUpdate(hotelImageId, file);
        HotelImageResponseDto result = HotelImageResponseDto.builder().build();
        result.copyMembers(resultDto, true);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.make(ResponseCode.UPDATE_OK, "ok", result)
        );
    }

    @GetMapping("/{hotelImageId}")
    public ResponseEntity<ApiResponse<HotelImageResponseDto>> findById(@PathVariable Long hotelImageId) {
        HotelImageResponseDto resultDto = this.hotelImageService.findById(hotelImageId);
        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.make(ResponseCode.SELECT_OK, "ok", resultDto)
        );
    }

    @GetMapping("/download/{hotelImageId}")
    public ResponseEntity<ByteArrayResource> downloadImageById(@PathVariable Long hotelImageId) throws IOException {
        HotelImageDto findDto = this.hotelImageService.findById(hotelImageId);
        byte[] result = this.hotelImageService.getImageBytesById(findDto);
        ByteArrayResource resource = new ByteArrayResource(result);
        return ResponseEntity.ok()
                .contentLength(result.length)
                .header("Content-type", "application/octet-stream")
                .header("Content-disposition", "attachment; filename=\"" + URLEncoder.encode(findDto.getFileName(), StandardCharsets.UTF_8) + "\"")
                .body(resource);
    }

    @GetMapping("/image/{hotelImageId}")
    public ResponseEntity<Resource> resourceImageById(@PathVariable Long hotelImageId) throws IOException {
        HotelImageDto findDto = this.hotelImageService.findById(hotelImageId);
        Resource result = this.hotelImageService.getImageResourceById(findDto);
        String contentType = "image/" + findDto.getExt();
        return ResponseEntity
                .ok()
                .header("Content-type", contentType)
                .body(result);
    }

    @DeleteMapping("/{hotelImageId}")
    public ResponseEntity<ApiResponse<HotelImageDto>> deleteById(@PathVariable Long hotelImageId) {
        HotelImageDto resultDto = this.hotelImageService.deleteById(hotelImageId);
        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.make(ResponseCode.DELETE_OK, "ok", resultDto)
        );
    }

    @GetMapping("/hotel/{hotelId}")
    public ResponseEntity<ApiResponse<Page<HotelImageResponseDto>>> page(
            @PathVariable Long hotelId, @PageableDefault(size = 10, page = 0, sort = "hotelImageId",
                    direction = Sort.Direction.DESC) Pageable pageable) {
        Page<HotelImageResponseDto> page = this.hotelImageService.findAllByHotelIdEquals(hotelId, pageable);
        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.make(ResponseCode.SELECT_OK, "ok", page)
        );
    }
}
