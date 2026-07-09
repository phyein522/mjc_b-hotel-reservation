package com.mjc.hotel.sales_analysis;

import com.mjc.hotel.common.ApiResponse;
import com.mjc.hotel.sales_analysis.controller.SalesAnalysisController;
import com.mjc.hotel.sales_analysis.dto.*;
import com.mjc.hotel.sales_analysis.service.SalesAnalysisService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class SalesAnalysisControllerTest {

    private SalesAnalysisController controller;
    private SalesAnalysisService service;

    @BeforeEach
    public void setUp() {
        // 매 테스트마다 서비스는 mock으로, controller는 새로 생성 (테스트 간 상태 공유 방지)
        service = mock(SalesAnalysisService.class);
        controller = new SalesAnalysisController(service);
    }

    // ---------------------------------------------------------
    // /api/sales/dashboard 관련 테스트 (기존)
    // ---------------------------------------------------------

    @Test
    public void testGetDashboardData_Success() {
        // Given
        SalesDashboardResponse mockResponse = SalesDashboardResponse.builder()
                .hotelId(9999L)
                .hotelName("MJC 가상 테스트 호텔")
                .year(2026)
                .month(6)
                .metrics(DashboardMetricsDto.builder()
                        .totalRevenue(new MetricValueDto<>(new BigDecimal("1150000.00"), 64.29, true))
                        .build())
                .roomTypeRevenue(List.of(new RoomTypeRevenueDto(), new RoomTypeRevenueDto(), new RoomTypeRevenueDto()))
                .topBookings(List.of(new TopBookingDto(), new TopBookingDto(), new TopBookingDto()))
                .build();
        when(service.getDashboardData(9999L, "2026-06")).thenReturn(mockResponse);

        // When
        ResponseEntity<ApiResponse<SalesDashboardResponse>> responseEntity = controller.getDashboard(9999L, "2026-06");

        // Then
        assertThat(responseEntity.getStatusCode().is2xxSuccessful()).isTrue();

        ApiResponse<SalesDashboardResponse> apiResponse = responseEntity.getBody();
        assertThat(apiResponse).isNotNull();
        assertThat(apiResponse.getResponseCode().name()).isEqualTo("SELECT_OK");

        SalesDashboardResponse responseData = apiResponse.getResponseData();
        assertThat(responseData.getHotelId()).isEqualTo(9999L);
        assertThat(responseData.getHotelName()).isEqualTo("MJC 가상 테스트 호텔");
        assertThat(responseData.getYear()).isEqualTo(2026);
        assertThat(responseData.getMonth()).isEqualTo(6);
        assertThat(responseData.getMetrics().getTotalRevenue().getValue().doubleValue()).isEqualTo(1150000.00);
        assertThat(responseData.getRoomTypeRevenue()).hasSize(3);
        assertThat(responseData.getTopBookings()).hasSize(3);
    }

    @Test
    public void testGetDashboardData_InvalidMonthFormat() {
        // Given
        when(service.getDashboardData(9999L, "2026-06-15")).thenThrow(new IllegalArgumentException("유효하지 않은 대상월 날짜 형식입니다."));

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            controller.getDashboard(9999L, "2026-06-15");
        });
    }

    // ---------------------------------------------------------
    // /api/sales/monthly 관련 테스트 (기존 + 빈 목록 케이스 추가)
    // ---------------------------------------------------------

    @Test
    public void testGetMonthlyTrend_Success() {
        // Given
        List<MonthlyRevenueDto> mockTrend = List.of(
                new MonthlyRevenueDto("2025-11", new BigDecimal("500000.00")),
                new MonthlyRevenueDto("2025-12", new BigDecimal("620000.00")),
                new MonthlyRevenueDto("2026-01", new BigDecimal("700000.00")),
                new MonthlyRevenueDto("2026-02", new BigDecimal("750000.00")),
                new MonthlyRevenueDto("2026-03", new BigDecimal("800000.00")),
                new MonthlyRevenueDto("2026-04", new BigDecimal("950000.00")),
                new MonthlyRevenueDto("2026-05", new BigDecimal("1000000.00")),
                new MonthlyRevenueDto("2026-06", new BigDecimal("1150000.00"))
        );
        when(service.getMonthlyRevenueTrend(9999L, "2025-11-01")).thenReturn(mockTrend);

        // When
        ResponseEntity<ApiResponse<List<MonthlyRevenueDto>>> responseEntity = controller.getMonthlyTrend(9999L, "2025-11-01");

        // Then
        assertThat(responseEntity.getStatusCode().is2xxSuccessful()).isTrue();

        ApiResponse<List<MonthlyRevenueDto>> apiResponse = responseEntity.getBody();
        assertThat(apiResponse).isNotNull();
        assertThat(apiResponse.getResponseCode().name()).isEqualTo("SELECT_OK");

        List<MonthlyRevenueDto> responseData = apiResponse.getResponseData();
        assertThat(responseData).hasSize(8);
        assertThat(responseData.get(0).getYearMonth()).isEqualTo("2025-11");
        assertThat(responseData.get(0).getRevenue().doubleValue()).isEqualTo(500000.00);
    }

    @Test
    public void testGetMonthlyTrend_EmptyResult() {
        // Given: 예약 데이터가 없는 신규 호텔이라 결과가 빈 리스트인 상황 가정
        when(service.getMonthlyRevenueTrend(8888L, "2026-06-01")).thenReturn(Collections.emptyList());

        // When
        ResponseEntity<ApiResponse<List<MonthlyRevenueDto>>> responseEntity = controller.getMonthlyTrend(8888L, "2026-06-01");

        // Then: 에러가 아니라 빈 리스트로 정상 응답되어야 함
        assertThat(responseEntity.getStatusCode().is2xxSuccessful()).isTrue();
        List<MonthlyRevenueDto> responseData = responseEntity.getBody().getResponseData();
        assertThat(responseData).isEmpty();
    }

    // ---------------------------------------------------------
    // /api/sales/top-bookings 관련 테스트 (신규 추가)
    // ---------------------------------------------------------

    @Test
    public void testGetTopBookings_Success() {
        // Given: TOP 5 예약 내역 mock 데이터
        List<TopBookingDto> mockTopBookings = List.of(
                new TopBookingDto(), new TopBookingDto(), new TopBookingDto(),
                new TopBookingDto(), new TopBookingDto()
        );
        when(service.getTopBookings(9999L, "2026-06")).thenReturn(mockTopBookings);

        // When
        ResponseEntity<ApiResponse<List<TopBookingDto>>> responseEntity = controller.getTopBookings(9999L, "2026-06");

        // Then
        assertThat(responseEntity.getStatusCode().is2xxSuccessful()).isTrue();

        ApiResponse<List<TopBookingDto>> apiResponse = responseEntity.getBody();
        assertThat(apiResponse).isNotNull();
        assertThat(apiResponse.getResponseCode().name()).isEqualTo("SELECT_OK");
        assertThat(apiResponse.getResponseData()).hasSize(5);

        // service가 정확한 파라미터로 한 번 호출됐는지도 함께 검증
        verify(service, times(1)).getTopBookings(9999L, "2026-06");
    }

    @Test
    public void testGetTopBookings_EmptyResult() {
        // Given: 해당 월에 예약이 하나도 없는 상황 가정
        when(service.getTopBookings(9999L, "2020-01")).thenReturn(Collections.emptyList());

        // When
        ResponseEntity<ApiResponse<List<TopBookingDto>>> responseEntity = controller.getTopBookings(9999L, "2020-01");

        // Then
        assertThat(responseEntity.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(responseEntity.getBody().getResponseData()).isEmpty();
    }

    @Test
    public void testGetTopBookings_InvalidMonthFormat() {
        // Given: 잘못된 targetMonth 형식이 들어오면 서비스에서 예외를 던진다고 가정
        when(service.getTopBookings(9999L, "26-06")).thenThrow(new IllegalArgumentException("유효하지 않은 대상월 날짜 형식입니다."));

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            controller.getTopBookings(9999L, "26-06");
        });
    }

    // ---------------------------------------------------------
    // /api/sales/rooms 관련 테스트 (신규 추가)
    // ---------------------------------------------------------

    @Test
    public void testGetRoomTypeRevenue_Success() {
        // Given: 객실 타입별 매출 통계 mock 데이터
        List<RoomTypeRevenueDto> mockRoomRevenue = List.of(
                new RoomTypeRevenueDto(), new RoomTypeRevenueDto(), new RoomTypeRevenueDto()
        );
        when(service.getRoomTypeRevenue(9999L, "2026-06")).thenReturn(mockRoomRevenue);

        // When
        ResponseEntity<ApiResponse<List<RoomTypeRevenueDto>>> responseEntity = controller.getRoomTypeRevenue(9999L, "2026-06");

        // Then
        assertThat(responseEntity.getStatusCode().is2xxSuccessful()).isTrue();

        ApiResponse<List<RoomTypeRevenueDto>> apiResponse = responseEntity.getBody();
        assertThat(apiResponse).isNotNull();
        assertThat(apiResponse.getResponseCode().name()).isEqualTo("SELECT_OK");
        assertThat(apiResponse.getResponseData()).hasSize(3);
    }

    @Test
    public void testGetRoomTypeRevenue_EmptyResult() {
        // Given: 객실 타입 데이터 자체가 아직 등록 안 된 호텔이라고 가정
        when(service.getRoomTypeRevenue(7777L, "2026-06")).thenReturn(Collections.emptyList());

        // When
        ResponseEntity<ApiResponse<List<RoomTypeRevenueDto>>> responseEntity = controller.getRoomTypeRevenue(7777L, "2026-06");

        // Then
        assertThat(responseEntity.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(responseEntity.getBody().getResponseData()).isEmpty();
    }

    @Test
    public void testGetRoomTypeRevenue_ServiceThrowsException() {
        // Given: 존재하지 않는 hotelId로 조회 시 서비스가 예외를 던진다고 가정
        when(service.getRoomTypeRevenue(-1L, "2026-06")).thenThrow(new IllegalArgumentException("존재하지 않는 호텔입니다."));

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            controller.getRoomTypeRevenue(-1L, "2026-06");
        });
    }
}