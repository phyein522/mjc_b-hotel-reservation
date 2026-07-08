package com.mjc.hotel.sales_analysis.dto;

import lombok.*;
import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class RoomTypeRevenueDto {
    private String roomTypeId;
    private String roomTypeName;
    private Long roomCount;
    private BigDecimal revenue;
}
