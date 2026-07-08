package com.mjc.hotel.sales_analysis.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class MetricValueDto<T> {
    private T value;
    private Double changeRate;
    private Boolean isIncreased;
}
