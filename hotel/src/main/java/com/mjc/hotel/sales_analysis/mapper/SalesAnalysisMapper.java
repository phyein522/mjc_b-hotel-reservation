package com.mjc.hotel.sales_analysis.mapper;

import com.mjc.hotel.sales_analysis.dto.*;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface SalesAnalysisMapper {

    // 1. 기본 매출, 예약건수, 총 판매 박수 집계
    SalesDashboardQueryDto getBasicMetrics(
            @Param("hotelId") Long hotelId,
            @Param("startDate") String startDate,
            @Param("endDate") String endDate);

    // 2. 오늘 점유된 객실 수 조회
    long getTodayOccupiedRooms(
            @Param("hotelId") Long hotelId,
            @Param("today") String today);

    // 3. 고객 재방문율 산출
    double getReturningGuestRate(
            @Param("hotelId") Long hotelId,
            @Param("startDate") String startDate,
            @Param("endDate") String endDate);

    // 4. VIP 고객 재방문율 산출
    double getVipReturningGuestRate(
            @Param("hotelId") Long hotelId,
            @Param("startDate") String startDate,
            @Param("endDate") String endDate);

    // 5. 최근 7개월 월 매출액 추이 조회
    List<MonthlyRevenueDto> getMonthlyRevenueTrend(
            @Param("hotelId") Long hotelId,
            @Param("startDate") String startDate);

    // 6. 객실 유형별 보유 개수 및 매출액 조회
    List<RoomTypeRevenueDto> getRoomTypeRevenue(
            @Param("hotelId") Long hotelId,
            @Param("startDate") String startDate,
            @Param("endDate") String endDate);

    // 7. 채널별 예약 및 매출 비중 조회
    List<ChannelShareDto> getChannelShares(
            @Param("hotelId") Long hotelId,
            @Param("startDate") String startDate,
            @Param("endDate") String endDate);

    // 8. 매출 상위 1~5위 예약 조회
    List<TopBookingDto> getTopBookings(
            @Param("hotelId") Long hotelId,
            @Param("startDate") String startDate,
            @Param("endDate") String endDate);
}
