package com.mjc.hotel.hotelsamen;

import com.mjc.hotel.common.ApiResponse;
import com.mjc.hotel.common.ResponseCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/hotelamenities")
public class HotelAmenRestController {
    @Autowired
    private HotelAmenService hotelAmenService;

    @GetMapping("/{hotelAmenId}")
    public ResponseEntity<ApiResponse<HotelAmenDto>> findById(@PathVariable Long hotelAmenId) {
        HotelAmenDto resultDto = this.hotelAmenService.findById(hotelAmenId);
        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.make(ResponseCode.SELECT_OK, "ok", resultDto)
        );
    }

    @PostMapping
    public ResponseEntity<ApiResponse<HotelAmenDto>> insert(@RequestBody HotelAmenDto requestDto) {
        HotelAmenDto resultDto = this.hotelAmenService.insert(requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.make(ResponseCode.INSERT_OK, "ok", resultDto)
        );
    }

    @PatchMapping
    public ResponseEntity<ApiResponse<HotelAmenDto>> update(@RequestBody HotelAmenDto requestDto) {
        HotelAmenDto resultDto = this.hotelAmenService.update(requestDto);
        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.make(ResponseCode.UPDATE_OK, "ok", resultDto)
        );
    }

    @DeleteMapping("/{hotelAmenId}")
    public ResponseEntity<ApiResponse<HotelAmenDto>> deleteById(@PathVariable Long hotelAmenId) {
        HotelAmenDto resultDto = this.hotelAmenService.deleteById(hotelAmenId);
        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.make(ResponseCode.DELETE_OK, "ok", resultDto)
        );
    }

    @GetMapping("/hotel/{hotelId}")
    public ResponseEntity<ApiResponse<Page<HotelAmenDto>>> page(
            @PathVariable Long hotelId,
            @PageableDefault(size = 10, page = 0, sort = "amenId", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<HotelAmenDto> page = this.hotelAmenService.findAllByHotelIdEquals(hotelId, pageable);
        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.make(ResponseCode.SELECT_OK, "ok", page)
        );
    }
}
