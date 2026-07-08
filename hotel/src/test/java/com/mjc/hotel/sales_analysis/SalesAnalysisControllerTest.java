package com.mjc.hotel.sales_analysis;

import com.mjc.hotel.common.ApiResponse;
import com.mjc.hotel.sales_analysis.controller.SalesAnalysisController;
import com.mjc.hotel.sales_analysis.dto.SalesDashboardResponse;
import com.mjc.hotel.sales_analysis.dto.MonthlyRevenueDto;
import com.mjc.hotel.sales_analysis.service.SalesAnalysisService;
import com.mjc.hotel.sales_analysis.service.SalesAnalysisServiceStub;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class SalesAnalysisControllerTest {

    private SalesAnalysisController controller;
    private SalesAnalysisService service;

    @BeforeEach
    public void setUp() {
        service = new SalesAnalysisServiceStub();
        controller = new SalesAnalysisController(service);
    }

    @Test
    public void testGetDashboardData_Success() {
        ResponseEntity<ApiResponse<SalesDashboardResponse>> responseEntity = controller.getDashboard(9999L, "2026-06");
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
        assertThat(responseData.getChannelShares()).hasSize(3);
        assertThat(responseData.getTopBookings()).hasSize(3);
    }

    @Test
    public void testGetDashboardData_InvalidMonthFormat() {
        assertThrows(IllegalArgumentException.class, () -> {
            controller.getDashboard(9999L, "2026-06-15");
        });
    }

    @Test
    public void testGetMonthlyTrend_Success() {
        ResponseEntity<ApiResponse<List<MonthlyRevenueDto>>> responseEntity = controller.getMonthlyTrend(9999L, "2025-11-01");
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
