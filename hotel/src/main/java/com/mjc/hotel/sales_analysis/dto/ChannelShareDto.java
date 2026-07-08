package com.mjc.hotel.sales_analysis.dto;

import lombok.*;
import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class ChannelShareDto {
    private Long channelId;
    private String channelName;
    private Long bookingCount;
    private Double bookingShareRate;
    private BigDecimal revenue;
    private Double revenueShareRate;
}
