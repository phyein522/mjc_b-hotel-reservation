package com.mjc.hotel.sales_analysis.service;

import com.mjc.hotel.sales_analysis.dto.MonthlyRevenueDto;
import com.mjc.hotel.sales_analysis.dto.RoomTypeRevenueDto;
import com.mjc.hotel.sales_analysis.dto.SalesDashboardQueryDto;
import com.mjc.hotel.sales_analysis.dto.SalesDashboardResponse;
import com.mjc.hotel.sales_analysis.entity.Hotel;
import com.mjc.hotel.sales_analysis.repository.HotelRepository;
import com.mjc.hotel.sales_analysis.repository.SalesAnalysisMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeParseException;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SalesAnalysisService {

    private final HotelRepository hotelRepository;
    private final SalesAnalysisMapper salesAnalysisMapper;

    // 매출 및 점유율 목표치 상수 설정 (테이블 부재로 인한 임시 설정값)
    private static final BigDecimal TARGET_REVENUE = new BigDecimal("50000000.00"); // 5천만원

    public SalesDashboardResponse getDashboardData(Long hotelId, String yearMonthParam) {
        // 1. 호텔 정보 조회 및 객실 수 체크
        Hotel hotel = hotelRepository.findById(hotelId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 호텔 ID입니다: " + hotelId));
        
        long totalRoomsCount = hotel.getRooms().stream()
                .filter(room -> Boolean.TRUE.equals(room.getIsActive()))
                .count();
        if (totalRoomsCount == 0) {
            totalRoomsCount = 1; // 0 나누기 방지용 기본값 보정
        }

        // 2. 날짜 파싱 및 범위 계산 (기본값: 이번달)
        LocalDate targetDate = LocalDate.now();
        if (yearMonthParam != null && !yearMonthParam.trim().isEmpty()) {
            try {
                YearMonth parsed = YearMonth.parse(yearMonthParam.trim());
                targetDate = parsed.atDay(1);
            } catch (DateTimeParseException e) {
                // 잘못된 포맷 유입 시 현재 날짜 사용
                targetDate = LocalDate.now();
            }
        }

        LocalDate startDate = targetDate.withDayOfMonth(1);
        LocalDate endDate = targetDate.withDayOfMonth(targetDate.lengthOfMonth());

        // 전월(T-1) 날짜 계산
        LocalDate prevStartDate = startDate.minusMonths(1);
        LocalDate prevEndDate = prevStartDate.withDayOfMonth(prevStartDate.lengthOfMonth());

        // 오늘(당일) 날짜 기준 설정 (조회월이 현재월이면 오늘, 과거월이면 해당 월 말일 기준)
        LocalDate today = LocalDate.now();
        if (targetDate.getYear() != today.getYear() || targetDate.getMonthValue() != today.getMonthValue()) {
            today = endDate;
        }

        // 3. 데이터베이스 쿼리 (MyBatis Mapper 호출)
        // 이번달 기본 통계
        SalesDashboardQueryDto currentMetrics = salesAnalysisMapper.getBasicMetrics(hotelId, startDate, endDate);
        // 전월 기본 통계
        SalesDashboardQueryDto prevMetrics = salesAnalysisMapper.getBasicMetrics(hotelId, prevStartDate, prevEndDate);

        // 당일 점유 객실수
        Long todayOccupiedRooms = salesAnalysisMapper.getTodayOccupiedRooms(hotelId, today);

        // 재방문율 및 전월 재방문율
        Double currentReturningRate = salesAnalysisMapper.getReturningGuestRate(hotelId, startDate, endDate);
        Double prevReturningRate = salesAnalysisMapper.getReturningGuestRate(hotelId, prevStartDate, prevEndDate);

        // VIP 재방문율
        Double vipReturningRate = salesAnalysisMapper.getVipReturningGuestRate(hotelId, startDate, endDate);

        // 최근 7개월 매출 트렌드 (조회월 포함 6개월 전부터 조회)
        LocalDate trendStartDate = startDate.minusMonths(6);
        List<MonthlyRevenueDto> monthlyTrend = salesAnalysisMapper.getMonthlyRevenueTrend(hotelId, trendStartDate);

        // 객실 유형별 매출 현황
        List<RoomTypeRevenueDto> roomTypeRevenue = salesAnalysisMapper.getRoomTypeRevenue(hotelId, startDate, endDate);

        // 4. 지표 및 전월비 연산
        // 4.1. 매출 지표
        double currentRev = currentMetrics.getTotalRevenue().doubleValue();
        double prevRev = prevMetrics.getTotalRevenue().doubleValue();
        double revenueChangeRate = calculateChangeRate(currentRev, prevRev);

        // 4.2. 점유율 지표
        double currentOcc = ((double) currentMetrics.getTotalNights() / (totalRoomsCount * targetDate.lengthOfMonth())) * 100.0;
        double prevOcc = ((double) prevMetrics.getTotalNights() / (totalRoomsCount * prevStartDate.lengthOfMonth())) * 100.0;
        double occChangeRate = currentOcc - prevOcc; // 점유율 변동은 단순 차이(%p)로 계산

        // 4.3. 당일 점유율 지표
        double todayOccRate = ((double) todayOccupiedRooms / totalRoomsCount) * 100.0;

        // 4.4. 평균 객단가 (ADR)
        double adr = currentMetrics.getTotalNights() > 0 
                ? currentRev / currentMetrics.getTotalNights() 
                : 0.0;

        // 4.5. 예약 건수 지표
        double currentBookings = currentMetrics.getBookingCount().doubleValue();
        double prevBookings = prevMetrics.getBookingCount().doubleValue();
        double bookingChangeRate = calculateChangeRate(currentBookings, prevBookings);

        // 4.6. 재방문율 전월비
        double currentRetRate = currentReturningRate != null ? currentReturningRate : 0.0;
        double prevRetRate = prevReturningRate != null ? prevReturningRate : 0.0;
        double retChangeRate = currentRetRate - prevRetRate; // 재방문율 변동 역시 단순 차이(%p)로 계산

        double vipRetRate = vipReturningRate != null ? vipReturningRate : 0.0;

        // 5. Response DTO 조립
        SalesDashboardResponse.DashboardMetrics metrics = SalesDashboardResponse.DashboardMetrics.builder()
                .totalRevenue(SalesDashboardResponse.MetricValue.builder()
                        .value(currentRev)
                        .changeRate(round(revenueChangeRate))
                        .isIncreased(revenueChangeRate >= 0)
                        .build())
                .occupancyRate(SalesDashboardResponse.MetricValue.builder()
                        .value(round(currentOcc))
                        .changeRate(round(occChangeRate))
                        .isIncreased(occChangeRate >= 0)
                        .build())
                .todayOccupancyRate(new SalesDashboardResponse.SimpleValue(round(todayOccRate)))
                .averageDailyRate(new SalesDashboardResponse.SimpleValue(round(adr)))
                .bookingCount(SalesDashboardResponse.MetricValue.builder()
                        .value(currentBookings)
                        .changeRate(round(bookingChangeRate))
                        .isIncreased(bookingChangeRate >= 0)
                        .build())
                .returningGuestRate(SalesDashboardResponse.MetricValue.builder()
                        .value(round(currentRetRate))
                        .changeRate(round(retChangeRate))
                        .isIncreased(retChangeRate >= 0)
                        .build())
                .vipReturningGuestRate(new SalesDashboardResponse.SimpleValue(round(vipRetRate)))
                .build();

        return SalesDashboardResponse.builder()
                .hotelId(hotelId)
                .hotelName(hotel.getName())
                .year(startDate.getYear())
                .month(startDate.getMonthValue())
                .targetRevenue(TARGET_REVENUE)
                .metrics(metrics)
                .monthlyTrend(monthlyTrend)
                .roomTypeRevenue(roomTypeRevenue)
                .build();
    }

    // 전월 대비 성장률 계산 유틸 메서드
    private double calculateChangeRate(double current, double prev) {
        if (prev == 0) {
            return current > 0 ? 100.0 : 0.0;
        }
        return ((current - prev) / prev) * 100.0;
    }

    // 소수점 둘째자리 반올림 유틸 메서드
    private double round(double val) {
        return BigDecimal.valueOf(val)
                .setScale(2, RoundingMode.HALF_UP)
                .doubleValue();
    }
}
