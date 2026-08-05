package com.mjc.hotel.hotels;

import com.mjc.hotel.common.ApiResponse;
import com.mjc.hotel.common.ResponseCode;
import com.mjc.hotel.user.dto.UserDto;
import com.mjc.hotel.user.entity.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/hotels")
public class HotelManagementRestController {
    @Autowired
    private HotelService hotelService;

    @GetMapping("/managed")
    public ResponseEntity<ApiResponse<Page<HotelDto>>> findManagedHotels(
            @AuthenticationPrincipal UserDto user,
            @PageableDefault(size = 100, page = 0) Pageable pageable) {
        if (user == null || user.getUserId() == null) {
            throw new IllegalArgumentException("관리자 사용자 정보가 필요합니다.");
        }
        if (user.getRole() != Role.HOTEL_MANAGER
                && user.getRole() != Role.ADMIN
                && user.getRole() != Role.SUPER_ADMIN) {
            throw new IllegalArgumentException("관리자만 담당 호텔을 조회할 수 있습니다.");
        }
        Page<HotelDto> page = this.hotelService.findAllByManagerId(user.getUserId(), pageable);
        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.make(ResponseCode.SELECT_OK, "managed hotels select ok", page)
        );
    }
}
