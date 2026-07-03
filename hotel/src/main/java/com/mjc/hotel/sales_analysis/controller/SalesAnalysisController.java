package com.mjc.hotel.sales_analysis.controller;

import com.mjc.hotel.common.ApiResponse;
import com.mjc.hotel.common.ResponseCode;
import com.mjc.hotel.sales_analysis.dto.*;
import com.mjc.hotel.sales_analysis.service.SalesAnalysisService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/sales")
@RequiredArgsConstructor
public class SalesAnalysisController {

    private final SalesAnalysisService salesAnalysisService;

    @GetMapping("/dashboard")
    public ResponseEntity<ApiResponse<SalesDashboardResponse>> getDashboard(
            @RequestParam Long hotelId,
            @RequestParam String targetMonth) {
        log.info("[매출 분석 API] 대시보드 조회 - hotelId: {}, targetMonth: {}", hotelId, targetMonth);
        SalesDashboardResponse response = salesAnalysisService.getDashboardData(hotelId, targetMonth);
        return ResponseEntity.ok(ApiResponse.make(ResponseCode.SELECT_OK, "ok", response));
    }

    @GetMapping("/monthly")
    public ResponseEntity<ApiResponse<List<MonthlyRevenueDto>>> getMonthlyTrend(
            @RequestParam Long hotelId,
            @RequestParam String startDate) {
        log.info("[매출 분석 API] 월별 매출 추이 조회 - hotelId: {}, startDate: {}", hotelId, startDate);
        List<MonthlyRevenueDto> response = salesAnalysisService.getMonthlyRevenueTrend(hotelId, startDate);
        return ResponseEntity.ok(ApiResponse.make(ResponseCode.SELECT_OK, "ok", response));
    }

    @GetMapping("/channels")
    public ResponseEntity<ApiResponse<List<ChannelShareDto>>> getChannelShares(
            @RequestParam Long hotelId,
            @RequestParam String targetMonth) {
        log.info("[매출 분석 API] 채널별 매출 비중 조회 - hotelId: {}, targetMonth: {}", hotelId, targetMonth);
        List<ChannelShareDto> response = salesAnalysisService.getChannelShares(hotelId, targetMonth);
        return ResponseEntity.ok(ApiResponse.make(ResponseCode.SELECT_OK, "ok", response));
    }

    @GetMapping("/top-bookings")
    public ResponseEntity<ApiResponse<List<TopBookingDto>>> getTopBookings(
            @RequestParam Long hotelId,
            @RequestParam String targetMonth) {
        log.info("[매출 분석 API] 매출 TOP 5 예약 내역 조회 - hotelId: {}, targetMonth: {}", hotelId, targetMonth);
        List<TopBookingDto> response = salesAnalysisService.getTopBookings(hotelId, targetMonth);
        return ResponseEntity.ok(ApiResponse.make(ResponseCode.SELECT_OK, "ok", response));
    }

    @GetMapping("/rooms")
    public ResponseEntity<ApiResponse<List<RoomTypeRevenueDto>>> getRoomTypeRevenue(
            @RequestParam Long hotelId,
            @RequestParam String targetMonth) {
        log.info("[매출 분석 API] 객실 유형별 통계 조회 - hotelId: {}, targetMonth: {}", hotelId, targetMonth);
        List<RoomTypeRevenueDto> response = salesAnalysisService.getRoomTypeRevenue(hotelId, targetMonth);
        return ResponseEntity.ok(ApiResponse.make(ResponseCode.SELECT_OK, "ok", response));
    }
}
