package com.mjc.hotel.sales_analysis.service;

import com.mjc.hotel.sales_analysis.dto.*;
import java.util.List;

public interface SalesAnalysisService {
    SalesDashboardResponse getDashboardData(Long hotelId, String targetMonth);
    List<MonthlyRevenueDto> getMonthlyRevenueTrend(Long hotelId, String startDate);
    List<ChannelShareDto> getChannelShares(Long hotelId, String targetMonth);
    List<TopBookingDto> getTopBookings(Long hotelId, String targetMonth);
    List<RoomTypeRevenueDto> getRoomTypeRevenue(Long hotelId, String targetMonth);
}
