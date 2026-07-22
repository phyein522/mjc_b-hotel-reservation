package com.mjc.hotel.usercoupon;

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
@RequestMapping("/api/usercoupons")
public class UserCouponRestController {

    @Autowired
    private UserCouponService userCouponService;

    @GetMapping
    public ResponseEntity<ApiResponse<Page<UserCouponDto>>> page(
            @PageableDefault(size = 10, page = 0,
                    sort = "userCouponId",
                    direction = Sort.Direction.DESC)
            Pageable pageable) {

        Page<UserCouponDto> page = this.userCouponService.findAll(pageable);

        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.make(ResponseCode.SELECT_OK, "ok", page)
        );
    }
    @GetMapping("/{userCouponId}")
    public ResponseEntity<ApiResponse<UserCouponDto>> findById(
            @PathVariable Long userCouponId) {

        UserCouponDto resultDto = this.userCouponService.findById(userCouponId);

        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.make(ResponseCode.SELECT_OK, "ok", resultDto)
        );
    }

    @PostMapping
    public ResponseEntity<ApiResponse<UserCouponDto>> insert(
            @RequestBody UserCouponDto requestDto) {

        UserCouponDto resultDto = this.userCouponService.insert(requestDto);

        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.make(ResponseCode.INSERT_OK, "ok", resultDto)
        );
    }
    @PatchMapping
    public ResponseEntity<ApiResponse<UserCouponDto>> update(
            @RequestBody UserCouponDto requestDto) {

        UserCouponDto resultDto = this.userCouponService.update(requestDto);

        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.make(ResponseCode.UPDATE_OK, "ok", resultDto)
        );
    }
    @DeleteMapping("/{userCouponId}/{userId}")
    public ResponseEntity<ApiResponse<UserCouponDto>> deleteById(
            @PathVariable Long userCouponId,
            @PathVariable Long userId) {

        UserCouponDto resultDto = this.userCouponService.deleteById(userCouponId, userId);

        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.make(ResponseCode.DELETE_OK, "ok", resultDto)
        );
    }
}
