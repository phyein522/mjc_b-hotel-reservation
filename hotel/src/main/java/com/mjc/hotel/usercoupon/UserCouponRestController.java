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

    // 사용자 쿠폰 목록을 페이지 단위로 조회한다.
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

    // 사용자 쿠폰 ID로 사용자 쿠폰 단건 정보를 조회한다.
    @GetMapping("/{userCouponId}")
    public ResponseEntity<ApiResponse<UserCouponDto>> findById(
            @PathVariable Long userCouponId) {

        UserCouponDto resultDto = this.userCouponService.findById(userCouponId);

        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.make(ResponseCode.SELECT_OK, "ok", resultDto)
        );
    }

    // 관리자 권한을 확인한 뒤 사용자에게 쿠폰을 발급한다.
    @PostMapping
    public ResponseEntity<ApiResponse<UserCouponDto>> insert(
            @RequestBody UserCouponDto requestDto,
            @RequestParam Long userId) {

        UserCouponDto resultDto = this.userCouponService.insert(requestDto, userId);

        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.make(ResponseCode.INSERT_OK, "ok", resultDto)
        );
    }

    // 관리자 권한을 확인한 뒤 기존 사용자 쿠폰 정보를 수정한다.
    @PatchMapping
    public ResponseEntity<ApiResponse<UserCouponDto>> update(
            @RequestBody UserCouponDto requestDto,
            @RequestParam Long userId) {

        UserCouponDto resultDto = this.userCouponService.update(requestDto, userId);

        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.make(ResponseCode.UPDATE_OK, "ok", resultDto)
        );
    }

    // 관리자 권한을 확인한 뒤 사용자 쿠폰을 삭제한다.
    @DeleteMapping("/{userCouponId}")
    public ResponseEntity<ApiResponse<UserCouponDto>> deleteById(
            @PathVariable Long userCouponId,
            @RequestParam Long userId) {

        UserCouponDto resultDto = this.userCouponService.deleteById(userCouponId, userId);

        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.make(ResponseCode.DELETE_OK, "ok", resultDto)
        );
    }
}
