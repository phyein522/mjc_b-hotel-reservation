package com.mjc.hotel.rates.controller;

import com.mjc.hotel.common.ApiResponse;
import com.mjc.hotel.common.ResponseCode;
import com.mjc.hotel.rates.dto.RatePolicyDto;
import com.mjc.hotel.rates.service.RatePolicyService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/rates/policies")
public class RatePolicyController {

    @Autowired
    private RatePolicyService ratePolicyService;

    @GetMapping("/hotels/{hotelId}")
    public ResponseEntity<ApiResponse<RatePolicyDto>> getPolicy(@PathVariable Long hotelId) {
        RatePolicyDto result = ratePolicyService.findByHotelId(hotelId);
        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.make(ResponseCode.SELECT_OK, "ok", result)
        );
    }

    @PutMapping("/hotels/{hotelId}/min-stay")
    public ResponseEntity<ApiResponse<RatePolicyDto>> updateMinStay(
            @PathVariable Long hotelId,
            @RequestBody RatePolicyDto requestDto) {
        
        if (requestDto.getMinStayNights() == null) {
            throw new IllegalArgumentException("최소 투숙 박수(minStayNights)는 필수입니다.");
        }

        RatePolicyDto result = ratePolicyService.updateMinStay(hotelId, requestDto.getMinStayNights());
        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.make(ResponseCode.UPDATE_OK, "ok", result)
        );
    }

    @PutMapping("/hotels/{hotelId}/times")
    public ResponseEntity<ApiResponse<RatePolicyDto>> updateTimes(
            @PathVariable Long hotelId,
            @RequestBody RatePolicyDto requestDto) {

        if (requestDto.getCheckInTime() == null || requestDto.getCheckOutTime() == null) {
            throw new IllegalArgumentException("체크인 시간 및 체크아웃 시간은 필수입니다.");
        }

        RatePolicyDto result = ratePolicyService.updateTimes(hotelId, requestDto.getCheckInTime(), requestDto.getCheckOutTime());
        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.make(ResponseCode.UPDATE_OK, "ok", result)
        );
    }

    @PutMapping("/hotels/{hotelId}/cancellation")
    public ResponseEntity<ApiResponse<RatePolicyDto>> updateCancellation(
            @PathVariable Long hotelId,
            @RequestBody RatePolicyDto requestDto) {

        if (requestDto.getCancelDeadlineDays() == null || requestDto.getCancelFeeRate() == null) {
            throw new IllegalArgumentException("취소 기한 및 취소 수수료율은 필수입니다.");
        }

        RatePolicyDto result = ratePolicyService.updateCancellation(hotelId, requestDto.getCancelDeadlineDays(), requestDto.getCancelFeeRate());
        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.make(ResponseCode.UPDATE_OK, "ok", result)
        );
    }

    @PutMapping("/hotels/{hotelId}/child-rates")
    public ResponseEntity<ApiResponse<RatePolicyDto>> updateChildRates(
            @PathVariable Long hotelId,
            @RequestBody RatePolicyDto requestDto) {

        if (requestDto.getFreeChildAge() == null || requestDto.getChildRateType() == null) {
            throw new IllegalArgumentException("무료 아동 나이 및 아동 요금 유형은 필수입니다.");
        }

        RatePolicyDto result = ratePolicyService.updateChildRates(
                hotelId,
                requestDto.getFreeChildAge(),
                requestDto.getChildRateType(),
                requestDto.getChildDiscountRate()
        );
        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.make(ResponseCode.UPDATE_OK, "ok", result)
        );
    }
}
