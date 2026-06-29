package com.mjc.hotel.sales_analysis.mapper;

import com.mjc.hotel.sales_analysis.dto.MonthlyRevenueDto;
import com.mjc.hotel.sales_analysis.dto.RoomTypeRevenueDto;
import com.mjc.hotel.sales_analysis.dto.SalesDashboardQueryDto;
import com.mjc.hotel.sales_analysis.dto.ChannelShareDto;
import com.mjc.hotel.sales_analysis.dto.TopBookingDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;

@Mapper
public interface SalesAnalysisMapper {

    SalesDashboardQueryDto getBasicMetrics(
            @Param("hotelId") Long hotelId, 
            @Param("startDate") LocalDate startDate, 
            @Param("endDate") LocalDate endDate
    );

    Long getTodayOccupiedRooms(
            @Param("hotelId") Long hotelId, 
            @Param("today") LocalDate today
    );

    Double getReturningGuestRate(
            @Param("hotelId") Long hotelId, 
            @Param("startDate") LocalDate startDate, 
            @Param("endDate") LocalDate endDate
    );

    Double getVipReturningGuestRate(
            @Param("hotelId") Long hotelId, 
            @Param("startDate") LocalDate startDate, 
            @Param("endDate") LocalDate endDate
    );

    List<MonthlyRevenueDto> getMonthlyRevenueTrend(
            @Param("hotelId") Long hotelId, 
            @Param("startDate") LocalDate startDate
    );

    List<RoomTypeRevenueDto> getRoomTypeRevenue(
            @Param("hotelId") Long hotelId, 
            @Param("startDate") LocalDate startDate, 
            @Param("endDate") LocalDate endDate
    );

    List<ChannelShareDto> getChannelShares(
            @Param("hotelId") Long hotelId, 
            @Param("startDate") LocalDate startDate, 
            @Param("endDate") LocalDate endDate
    );

    List<TopBookingDto> getTopBookings(
            @Param("hotelId") Long hotelId, 
            @Param("startDate") LocalDate startDate, 
            @Param("endDate") LocalDate endDate
    );
}
