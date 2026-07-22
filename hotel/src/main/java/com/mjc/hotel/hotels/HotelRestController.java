package com.mjc.hotel.hotels;

import com.mjc.hotel.common.ApiResponse;
import com.mjc.hotel.common.ResponseCode;
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

@RestController
@Slf4j
@RequestMapping("/api/hotels")
public class HotelRestController {
    @Autowired
    private HotelService hotelService;

    @GetMapping
    public ResponseEntity<ApiResponse<Page<HotelDto>>> page(
            @PageableDefault(size = 10, page = 0, sort = "hotelId", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<HotelDto> page = this.hotelService.findAll(pageable);
        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.make(ResponseCode.SELECT_OK, "ok", page)
        );
    }

    @GetMapping("/{hotelId}")
    public ResponseEntity<ApiResponse<HotelDto>> findById(@PathVariable Long hotelId) {
        HotelDto resultDto = this.hotelService.findById(hotelId);
        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.make(ResponseCode.SELECT_OK, "ok", resultDto)
        );
    }

    @GetMapping("/images/{hotelId}")
    public ResponseEntity<ApiResponse<HotelResponseWithImagesDto>> findByIdWithImages(@PathVariable Long hotelId, @PageableDefault(size=10, page=0, sort="hotelImageId", direction= Sort.Direction.DESC) Pageable pageable) {
        HotelResponseWithImagesDto resultDto = this.hotelService.findByIdWithImages(hotelId, pageable);
        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.make(ResponseCode.SELECT_OK, "ok", resultDto)
        );
    }

    @PostMapping
    public ResponseEntity<ApiResponse<HotelDto>> insert(@RequestBody HotelDto requestDto) {
        HotelDto resultDto = this.hotelService.insert(requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.make(ResponseCode.INSERT_OK, "ok", resultDto)
        );
    }
    @PostMapping("/image")
    public ResponseEntity<ApiResponse<HotelResponseWithImagesDto>> insertWithImages(@RequestPart(name = "requestDto", required = true) HotelDto requestDto
            , @RequestPart(name = "files", required = false) List<MultipartFile> files) throws IOException {
        HotelResponseWithImagesDto resultDto = this.hotelService.insertWithImages(requestDto, files);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.make(ResponseCode.INSERT_OK, "ok", resultDto)
        );
    }

    @PatchMapping
    public ResponseEntity<ApiResponse<HotelDto>> update(@RequestBody HotelDto requestDto) {
        HotelDto resultDto = this.hotelService.update(requestDto);
        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.make(ResponseCode.UPDATE_OK, "ok", resultDto)
        );
    }

    @DeleteMapping("/{hotelId}")
    public ResponseEntity<ApiResponse<HotelDto>> deleteById(
            @PathVariable Long hotelId,
            @RequestParam Long userId) {

        HotelDto resultDto = this.hotelService.deleteById(hotelId, userId);

        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.make(ResponseCode.DELETE_OK, "ok", resultDto)
        );
    }
}
