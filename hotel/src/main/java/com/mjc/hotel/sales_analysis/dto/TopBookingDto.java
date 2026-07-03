package com.mjc.hotel.sales_analysis.dto;

import lombok.*;
import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class TopBookingDto {
    private Integer rank;
    private String guestName;
    private String roomName;
    private BigDecimal amount;
    private Integer nights;
}
