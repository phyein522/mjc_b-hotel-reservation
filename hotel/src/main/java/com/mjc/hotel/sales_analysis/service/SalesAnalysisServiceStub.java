package com.mjc.hotel.sales_analysis.service;

import com.mjc.hotel.sales_analysis.dto.*;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@Profile("stub")
public class SalesAnalysisServiceStub implements SalesAnalysisService {

    @Override
    public SalesDashboardResponse getDashboardData(Long hotelId, String targetMonth) {
        validateTargetMonth(targetMonth);

        String[] parts = targetMonth.split("-");
        int year = Integer.parseInt(parts[0]);
        int month = Integer.parseInt(parts[1]);

        return SalesDashboardResponse.builder()
                .hotelId(hotelId)
                .hotelName("MJC 가상 테스트 호텔")
                .year(year)
                .month(month)
                .metrics(buildStubMetrics())
                .roomTypeRevenue(buildStubRoomTypeRevenue())
                .channelShares(buildStubChannelShares())
                .topBookings(buildStubTopBookings())
                .build();
    }

    @Override
    public List<MonthlyRevenueDto> getMonthlyRevenueTrend(Long hotelId, String startDate) {
        if (startDate == null || !startDate.matches("^\\d{4}-\\d{2}-\\d{2}$")) {
            throw new IllegalArgumentException("유효하지 않은 시작일 날짜 형식입니다. (YYYY-MM-DD 형식 필요)");
        }

        List<MonthlyRevenueDto> trend = new ArrayList<>();
        trend.add(new MonthlyRevenueDto("2025-11", new BigDecimal("500000.00")));
        trend.add(new MonthlyRevenueDto("2025-12", new BigDecimal("620000.00")));
        trend.add(new MonthlyRevenueDto("2026-01", new BigDecimal("700000.00")));
        trend.add(new MonthlyRevenueDto("2026-02", new BigDecimal("750000.00")));
        trend.add(new MonthlyRevenueDto("2026-03", new BigDecimal("800000.00")));
        trend.add(new MonthlyRevenueDto("2026-04", new BigDecimal("950000.00")));
        trend.add(new MonthlyRevenueDto("2026-05", new BigDecimal("1000000.00")));
        trend.add(new MonthlyRevenueDto("2026-06", new BigDecimal("1150000.00")));
        return trend;
    }

    @Override
    public List<ChannelShareDto> getChannelShares(Long hotelId, String targetMonth) {
        validateTargetMonth(targetMonth);
        return buildStubChannelShares();
    }

    @Override
    public List<TopBookingDto> getTopBookings(Long hotelId, String targetMonth) {
        validateTargetMonth(targetMonth);
        return buildStubTopBookings();
    }

    @Override
    public List<RoomTypeRevenueDto> getRoomTypeRevenue(Long hotelId, String targetMonth) {
        validateTargetMonth(targetMonth);
        return buildStubRoomTypeRevenue();
    }

    private void validateTargetMonth(String targetMonth) {
        if (targetMonth == null || !targetMonth.matches("^\\d{4}-\\d{2}$")) {
            throw new IllegalArgumentException("유효하지 않은 대상월 날짜 형식입니다. (YYYY-MM 형식 필요)");
        }
    }

    private DashboardMetricsDto buildStubMetrics() {
        return DashboardMetricsDto.builder()
                .totalRevenue(new MetricValueDto<>(new BigDecimal("1150000.00"), 64.29, true))
                .bookingCount(new MetricValueDto<>(3L, 50.00, true))
                .occupancyRate(new MetricValueDto<>(45.50, 2.10, true))
                .adr(new MetricValueDto<>(164285.71, -1.25, false))
                .returningGuestRate(new MetricValueDto<>(66.67, 5.00, true))
                .vipReturningGuestRate(new MetricValueDto<>(100.00, 0.00, false))
                .build();
    }

    private List<RoomTypeRevenueDto> buildStubRoomTypeRevenue() {
        List<RoomTypeRevenueDto> list = new ArrayList<>();
        list.add(new RoomTypeRevenueDto("STANDARD", "스탠다드 룸", 1L, new BigDecimal("100000.00")));
        list.add(new RoomTypeRevenueDto("DELUXE", "디럭스 룸", 1L, new BigDecimal("150000.00")));
        list.add(new RoomTypeRevenueDto("SUITE", "스위트 룸", 1L, new BigDecimal("900000.00")));
        return list;
    }

    private List<ChannelShareDto> buildStubChannelShares() {
        List<ChannelShareDto> list = new ArrayList<>();
        list.add(new ChannelShareDto(2L, "아고다", 1L, 33.33, new BigDecimal("900000.00"), 78.26));
        list.add(new ChannelShareDto(1L, "야놀자", 1L, 33.33, new BigDecimal("150000.00"), 13.04));
        list.add(new ChannelShareDto(3L, "직접예약", 1L, 33.33, new BigDecimal("100000.00"), 8.70));
        return list;
    }

    private List<TopBookingDto> buildStubTopBookings() {
        List<TopBookingDto> list = new ArrayList<>();
        list.add(new TopBookingDto(1, "박대표", "T103호 (스위트 룸)", new BigDecimal("900000.00"), 3));
        list.add(new TopBookingDto(2, "김철수", "T102호 (디럭스 룸)", new BigDecimal("150000.00"), 1));
        list.add(new TopBookingDto(3, "이영희", "T101호 (스탠다드 룸)", new BigDecimal("100000.00"), 1));
        return list;
    }
}
