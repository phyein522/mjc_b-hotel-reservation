package com.mjc.hotel.sales_analysis.controller;

import com.mjc.hotel.sales_analysis.dto.HotelResponseDto;
import com.mjc.hotel.sales_analysis.dto.SalesResponseDto;
import com.mjc.hotel.sales_analysis.service.SalesService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "매출 분석", description = "호텔 매출 분석 API")
@RestController
@RequestMapping("/api/sales")
@RequiredArgsConstructor
public class SalesController {

    private final SalesService salesService;

    // 드롭다운용 운영중인 호텔 목록 조회
    @Operation(summary = "호텔 목록 조회", description = "운영중인 호텔 목록 반환")
    @GetMapping("/hotels")
    public ResponseEntity<List<HotelResponseDto>> getHotelList() {
        return ResponseEntity.ok(salesService.getActiveHotelList());
    }

    // 호텔 선택 후 연도별 매출 비교 조회
    @Operation(summary = "매출 분석 조회", description = "선택한 호텔의 올해/작년 매출 비교 및 증감률 반환")
    @GetMapping("/summary")
    public ResponseEntity<SalesResponseDto> getSalesSummary(
            @RequestParam Long hotelId,
            @RequestParam int year) {
        return ResponseEntity.ok(salesService.getSalesSummary(hotelId, year));
    }
}
