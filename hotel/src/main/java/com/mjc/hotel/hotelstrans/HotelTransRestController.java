package com.mjc.hotel.hotelstrans;

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
@RequestMapping("/api/hoteltrans")
public class HotelTransRestController {
    @Autowired
    private HotelTransService hotelTransService;

    @GetMapping("/{transId}")
    public ResponseEntity<ApiResponse<HotelTransDto>> findById(@PathVariable Long transId) {
        HotelTransDto resultDto = this.hotelTransService.findById(transId);
        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.make(ResponseCode.SELECT_OK, "ok", resultDto)
        );
    }

    @PostMapping
    public ResponseEntity<ApiResponse<HotelTransDto>> insert(@RequestBody HotelTransDto requestDto) {
        HotelTransDto resultDto = this.hotelTransService.insert(requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.make(ResponseCode.INSERT_OK, "ok", resultDto)
        );
    }

    @PatchMapping
    public ResponseEntity<ApiResponse<HotelTransDto>> update(@RequestBody HotelTransDto requestDto) {
        HotelTransDto resultDto = this.hotelTransService.update(requestDto);
        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.make(ResponseCode.UPDATE_OK, "ok", resultDto)
        );
    }

    @DeleteMapping("/{transId}")
    public ResponseEntity<ApiResponse<HotelTransDto>> deleteById(@PathVariable Long transId) {
        HotelTransDto resultDto = this.hotelTransService.deleteById(transId);
        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.make(ResponseCode.DELETE_OK, "ok", resultDto)
        );
    }

    @GetMapping("/hotel/{hotelId}")
    public ResponseEntity<ApiResponse<Page<HotelTransDto>>> page(
            @PathVariable Long hotelId,
            @PageableDefault(size = 10, page = 0, sort = "transId", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<HotelTransDto> page = this.hotelTransService.findAllByHotelIdEquals(hotelId, pageable);
        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.make(ResponseCode.SELECT_OK, "ok", page)
        );
    }
}
