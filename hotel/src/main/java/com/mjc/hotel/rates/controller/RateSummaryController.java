package com.mjc.hotel.rates.controller;

import com.mjc.hotel.common.ApiResponse;
import com.mjc.hotel.common.ResponseCode;
import com.mjc.hotel.rates.dto.RateSummaryResponseDto;
import com.mjc.hotel.rates.service.RateSummaryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/rates")
public class RateSummaryController {

    @Autowired
    private RateSummaryService rateSummaryService;

    @GetMapping("/hotels/{hotelId}/summary")
    public ResponseEntity<ApiResponse<RateSummaryResponseDto>> getSummary(@PathVariable Long hotelId) {
        RateSummaryResponseDto result = rateSummaryService.getRateSummary(hotelId);
        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.make(ResponseCode.SELECT_OK, "ok", result)
        );
    }
}
