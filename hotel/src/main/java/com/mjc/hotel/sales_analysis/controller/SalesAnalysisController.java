package com.mjc.hotel.sales_analysis.controller;

import com.mjc.hotel.sales_analysis.dto.SalesDashboardResponse;
import com.mjc.hotel.sales_analysis.service.SalesAnalysisService;
import com.mjc.hotel.util.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Sales Analysis", description = "호텔 매출 분석 및 점유율 대시보드 API")
@RestController
@RequestMapping("/api/sales")
@RequiredArgsConstructor
public class SalesAnalysisController {

    private final SalesAnalysisService salesAnalysisService;

    @Operation(
            summary = "대시보드 종합 지표 조회", 
            description = "특정 호텔의 선택 월 기준 매출, 객실 점유율, 당일 점유율, 평균 객단가(ADR), 예약 건수, 고객/VIP 재방문율 및 최근 7개월 매출 트렌드를 조회합니다."
    )
    @GetMapping("/dashboard")
    public ResponseEntity<ApiResponse<SalesDashboardResponse>> getDashboard(
            @Parameter(description = "조회 대상 호텔 ID", required = true)
            @RequestParam(name = "hotelId") Long hotelId,
            
            @Parameter(description = "조회 대상 연월 (포맷: YYYY-MM, 미지정 시 현재 월 기본값 적용)", required = false)
            @RequestParam(name = "yearMonth", required = false) String yearMonth
    ) {
        SalesDashboardResponse data = salesAnalysisService.getDashboardData(hotelId, yearMonth);
        ApiResponse<SalesDashboardResponse> response = new ApiResponse<>(true, "대시보드 데이터를 성공적으로 조회했습니다.", data);
        return ResponseEntity.ok(response);
    }
}
