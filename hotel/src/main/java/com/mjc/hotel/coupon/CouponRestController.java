package com.mjc.hotel.coupon;

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

@RestController
@Slf4j
@RequestMapping("/api/coupons")
public class CouponRestController {

    @Autowired
    private CouponService couponService;

    @GetMapping
    public ResponseEntity<ApiResponse<Page<CouponDto>>> page(
            @PageableDefault(size = 10, page = 0,
                    sort = "couponId",
                    direction = Sort.Direction.DESC)
            Pageable pageable) {

        Page<CouponDto> page = this.couponService.findAll(pageable);

        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.make(ResponseCode.SELECT_OK, "ok", page)
        );
    }

    @GetMapping("/{couponId}")
    public ResponseEntity<ApiResponse<CouponDto>> findById(
            @PathVariable Long couponId) {

        CouponDto resultDto = this.couponService.findById(couponId);

        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.make(ResponseCode.SELECT_OK, "ok", resultDto)
        );
    }

    @PostMapping
    public ResponseEntity<ApiResponse<CouponDto>> insert(
            @RequestBody CouponDto requestDto) {

        CouponDto resultDto = this.couponService.insert(requestDto);

        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.make(ResponseCode.INSERT_OK, "ok", resultDto)
        );
    }

    @PatchMapping
    public ResponseEntity<ApiResponse<CouponDto>> update(
            @RequestBody CouponDto requestDto) {

        CouponDto resultDto = this.couponService.update(requestDto);

        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.make(ResponseCode.UPDATE_OK, "ok", resultDto)
        );
    }

    @DeleteMapping("/{couponId}/{userId}")
    public ResponseEntity<ApiResponse<CouponDto>> deleteById(
            @PathVariable Long couponId,
            @PathVariable Long userId) {

        CouponDto resultDto = this.couponService.deleteById(couponId, userId);

        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.make(ResponseCode.DELETE_OK, "ok", resultDto)
        );
    }
}