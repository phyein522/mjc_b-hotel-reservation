package com.mjc.hotel.sales_analysis.dto;

import lombok.*;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class SalesDashboardResponse {
    private Long hotelId;
    private String hotelName;
    private Integer year;
    private Integer month;
    private DashboardMetricsDto metrics;
    private List<RoomTypeRevenueDto> roomTypeRevenue;
    private List<ChannelShareDto> channelShares;
    private List<TopBookingDto> topBookings;
}
