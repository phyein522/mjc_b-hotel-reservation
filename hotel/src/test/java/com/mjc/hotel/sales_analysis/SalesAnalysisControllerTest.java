package com.mjc.hotel.sales_analysis;

import com.mjc.hotel.common.ApiResponse;
import com.mjc.hotel.sales_analysis.controller.SalesAnalysisController;
import com.mjc.hotel.sales_analysis.dto.*;
import com.mjc.hotel.sales_analysis.service.SalesAnalysisService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class SalesAnalysisControllerTest {

    private SalesAnalysisController controller;
    private SalesAnalysisService service;

    @BeforeEach
    public void setUp() {
        service = mock(SalesAnalysisService.class);
        controller = new SalesAnalysisController(service);
    }

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
}
