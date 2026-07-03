package com.mjc.hotel.sales_analysis.service;

import com.mjc.hotel.sales_analysis.dto.*;
import com.mjc.hotel.sales_analysis.entity.FakeHotel;
import com.mjc.hotel.sales_analysis.mapper.SalesAnalysisMapper;
import com.mjc.hotel.sales_analysis.repository.FakeHotelRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

@Service
@Profile("!stub")
@RequiredArgsConstructor
public class SalesAnalysisServiceImpl implements SalesAnalysisService {

    private final SalesAnalysisMapper salesAnalysisMapper;
    private final FakeHotelRepository hotelRepository;

    @Override
    public SalesDashboardResponse getDashboardData(Long hotelId, String targetMonth) {
        validateTargetMonth(targetMonth);

        // 1. 호텔 이름 조회
        String hotelName = hotelRepository.findById(hotelId)
                .map(FakeHotel::getName)
                .orElse("알 수 없는 호텔");

        // 2. 날짜 계산
        YearMonth currentYearMonth = YearMonth.parse(targetMonth);
        int year = currentYearMonth.getYear();
        int month = currentYearMonth.getMonthValue();

        String startDate = currentYearMonth.atDay(1).toString();
        String endDate = currentYearMonth.atEndOfMonth().toString();
        int daysInMonth = currentYearMonth.lengthOfMonth();

        YearMonth prevYearMonth = currentYearMonth.minusMonths(1);
        String prevStartDate = prevYearMonth.atDay(1).toString();
        String prevEndDate = prevYearMonth.atEndOfMonth().toString();
        int prevDaysInMonth = prevYearMonth.lengthOfMonth();

        // 3. 당월 및 전월 데이터 조회
        SalesDashboardQueryDto currentBasic = salesAnalysisMapper.getBasicMetrics(hotelId, startDate, endDate);
        SalesDashboardQueryDto prevBasic = salesAnalysisMapper.getBasicMetrics(hotelId, prevStartDate, prevEndDate);

        // null 방어 처리
        if (currentBasic == null) {
            currentBasic = new SalesDashboardQueryDto(BigDecimal.ZERO, 0L, 0L);
        }
        if (prevBasic == null) {
            prevBasic = new SalesDashboardQueryDto(BigDecimal.ZERO, 0L, 0L);
        }

        BigDecimal currentRevenue = currentBasic.getTotalRevenue() != null ? currentBasic.getTotalRevenue() : BigDecimal.ZERO;
        BigDecimal prevRevenue = prevBasic.getTotalRevenue() != null ? prevBasic.getTotalRevenue() : BigDecimal.ZERO;

        Long currentBookingCount = currentBasic.getBookingCount() != null ? currentBasic.getBookingCount() : 0L;
        Long prevBookingCount = prevBasic.getBookingCount() != null ? prevBasic.getBookingCount() : 0L;

        Long currentNights = currentBasic.getTotalNights() != null ? currentBasic.getTotalNights() : 0L;
        Long prevNights = prevBasic.getTotalNights() != null ? prevBasic.getTotalNights() : 0L;

        // 4. 객실 유형 조회 및 총 활성화 객실 수 계산
        List<RoomTypeRevenueDto> roomTypeRevenue = salesAnalysisMapper.getRoomTypeRevenue(hotelId, startDate, endDate);
        long totalActiveRooms = roomTypeRevenue.stream()
                .mapToLong(r -> r.getRoomCount() != null ? r.getRoomCount() : 0L)
                .sum();

        List<RoomTypeRevenueDto> prevRoomTypeRevenue = salesAnalysisMapper.getRoomTypeRevenue(hotelId, prevStartDate, prevEndDate);
        long prevActiveRooms = prevRoomTypeRevenue.stream()
                .mapToLong(r -> r.getRoomCount() != null ? r.getRoomCount() : 0L)
                .sum();
        if (prevActiveRooms == 0) {
            prevActiveRooms = totalActiveRooms;
        }

        // 5. 객실 점유율 계산 (Occupancy Rate)
        double currentOccupancy = 0.0;
        if (totalActiveRooms > 0 && daysInMonth > 0) {
            currentOccupancy = round((currentNights * 100.0) / (totalActiveRooms * daysInMonth));
        }

        double prevOccupancy = 0.0;
        if (prevActiveRooms > 0 && prevDaysInMonth > 0) {
            prevOccupancy = round((prevNights * 100.0) / (prevActiveRooms * prevDaysInMonth));
        }

        // 6. 평균 객단가 계산 (ADR)
        double currentAdr = 0.0;
        if (currentNights > 0) {
            currentAdr = round(currentRevenue.doubleValue() / currentNights);
        }

        double prevAdr = 0.0;
        if (prevNights > 0) {
            prevAdr = round(prevRevenue.doubleValue() / prevNights);
        }

        // 7. 재방문율 및 VIP 재방문율 조회
        double currentReturningRate = round(salesAnalysisMapper.getReturningGuestRate(hotelId, startDate, endDate));
        double prevReturningRate = round(salesAnalysisMapper.getReturningGuestRate(hotelId, prevStartDate, prevEndDate));

        double currentVipReturningRate = round(salesAnalysisMapper.getVipReturningGuestRate(hotelId, startDate, endDate));
        double prevVipReturningRate = round(salesAnalysisMapper.getVipReturningGuestRate(hotelId, prevStartDate, prevEndDate));

        // 8. 메트릭 DTO 조립
        DashboardMetricsDto metrics = DashboardMetricsDto.builder()
                .totalRevenue(calculateBigDecimalMetric(currentRevenue, prevRevenue))
                .bookingCount(calculateLongMetric(currentBookingCount, prevBookingCount))
                .occupancyRate(calculateDoubleMetric(currentOccupancy, prevOccupancy))
                .adr(calculateDoubleMetric(currentAdr, prevAdr))
                .returningGuestRate(calculateDoubleMetric(currentReturningRate, prevReturningRate))
                .vipReturningGuestRate(calculateDoubleMetric(currentVipReturningRate, prevVipReturningRate))
                .build();

        // 9. 채널 및 탑 예약 데이터 조회
        List<ChannelShareDto> channelShares = salesAnalysisMapper.getChannelShares(hotelId, startDate, endDate);
        List<TopBookingDto> topBookings = salesAnalysisMapper.getTopBookings(hotelId, startDate, endDate);

        return SalesDashboardResponse.builder()
                .hotelId(hotelId)
                .hotelName(hotelName)
                .year(year)
                .month(month)
                .metrics(metrics)
                .roomTypeRevenue(roomTypeRevenue)
                .channelShares(channelShares)
                .topBookings(topBookings)
                .build();
    }

    @Override
    public List<MonthlyRevenueDto> getMonthlyRevenueTrend(Long hotelId, String startDate) {
        if (startDate == null || !startDate.matches("^\\d{4}-\\d{2}-\\d{2}$")) {
            throw new IllegalArgumentException("유효하지 않은 시작일 날짜 형식입니다. (YYYY-MM-DD 형식 필요)");
        }
        return salesAnalysisMapper.getMonthlyRevenueTrend(hotelId, startDate);
    }

    @Override
    public List<ChannelShareDto> getChannelShares(Long hotelId, String targetMonth) {
        validateTargetMonth(targetMonth);
        YearMonth currentYearMonth = YearMonth.parse(targetMonth);
        String startDate = currentYearMonth.atDay(1).toString();
        String endDate = currentYearMonth.atEndOfMonth().toString();
        return salesAnalysisMapper.getChannelShares(hotelId, startDate, endDate);
    }

    @Override
    public List<TopBookingDto> getTopBookings(Long hotelId, String targetMonth) {
        validateTargetMonth(targetMonth);
        YearMonth currentYearMonth = YearMonth.parse(targetMonth);
        String startDate = currentYearMonth.atDay(1).toString();
        String endDate = currentYearMonth.atEndOfMonth().toString();
        return salesAnalysisMapper.getTopBookings(hotelId, startDate, endDate);
    }

    @Override
    public List<RoomTypeRevenueDto> getRoomTypeRevenue(Long hotelId, String targetMonth) {
        validateTargetMonth(targetMonth);
        YearMonth currentYearMonth = YearMonth.parse(targetMonth);
        String startDate = currentYearMonth.atDay(1).toString();
        String endDate = currentYearMonth.atEndOfMonth().toString();
        return salesAnalysisMapper.getRoomTypeRevenue(hotelId, startDate, endDate);
    }

    private void validateTargetMonth(String targetMonth) {
        if (targetMonth == null || !targetMonth.matches("^\\d{4}-\\d{2}$")) {
            throw new IllegalArgumentException("유효하지 않은 대상월 날짜 형식입니다. (YYYY-MM 형식 필요)");
        }
    }

    private double round(double val) {
        return Math.round(val * 100.0) / 100.0;
    }

    private MetricValueDto<BigDecimal> calculateBigDecimalMetric(BigDecimal currentVal, BigDecimal prevVal) {
        double current = currentVal.doubleValue();
        double prev = prevVal.doubleValue();

        double changeRate = 0.0;
        boolean isIncreased = false;

        if (prev > 0) {
            changeRate = round(((current - prev) / prev) * 100.0);
            isIncreased = changeRate > 0;
        }

        return new MetricValueDto<>(currentVal, changeRate, isIncreased);
    }

    private MetricValueDto<Long> calculateLongMetric(Long currentVal, Long prevVal) {
        double current = currentVal.doubleValue();
        double prev = prevVal.doubleValue();

        double changeRate = 0.0;
        boolean isIncreased = false;

        if (prev > 0) {
            changeRate = round(((current - prev) / prev) * 100.0);
            isIncreased = changeRate > 0;
        }

        return new MetricValueDto<>(currentVal, changeRate, isIncreased);
    }

    private MetricValueDto<Double> calculateDoubleMetric(Double currentVal, Double prevVal) {
        double changeRate = 0.0;
        boolean isIncreased = false;

        if (prevVal > 0) {
            changeRate = round(((currentVal - prevVal) / prevVal) * 100.0);
            isIncreased = changeRate > 0;
        }

        return new MetricValueDto<>(currentVal, changeRate, isIncreased);
    }
}
