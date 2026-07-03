package com.mjc.hotel.sales_analysis.entity;

import lombok.*;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "channels")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FakeChannel {
    @Id
    @Column(name = "채널ID")
    private Long id;

    @Column(name = "채널명", nullable = false, length = 100)
    private String name;

    @Column(name = "채널유형", nullable = false, length = 20)
    private String type;

    @Column(name = "수수료율", precision = 5, scale = 2)
    private BigDecimal feeRate;

    @Column(name = "활성여부", nullable = false)
    private Boolean isActive;

    @Column(name = "생성시간")
    private LocalDateTime createdAt;
}
