package com.mjc.hotel.sales_analysis.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "hotels")
@EntityListeners(AuditingEntityListener.class)
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@ToString(exclude = "rooms")
public class Hotel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "`호텔아이디`")
    private Long id;

    @Column(name = "`이름`", nullable = false, length = 200)
    private String name;

    @Column(name = "`설명`", columnDefinition = "TEXT")
    private String description;

    @Column(name = "`주소`", nullable = false, length = 500)
    private String address;

    @Column(name = "`도시`", nullable = false, length = 100)
    private String city;

    @Column(name = "`우편번호`", length = 30)
    private String zipCode;

    @Column(name = "`전화번호`", length = 30)
    private String phoneNumber;

    @Column(name = "`이메일`", length = 255)
    private String email;

    @Column(name = "`체크인 시간`", nullable = false)
    private LocalTime checkInTime;

    @Column(name = "`체크아웃 시간`", nullable = false)
    private LocalTime checkOutTime;

    @Column(name = "`별_등급`")
    private Integer starRating;

    @Column(name = "`운영여부`", nullable = false)
    private Boolean isActive;

    @CreatedDate
    @Column(name = "`생성 시간`", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "`업데이트 시간`", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "`위도`", length = 30)
    private String latitude;

    @Column(name = "`경도`", length = 30)
    private String longitude;

    @Column(name = "`호텔 유형`", length = 20)
    private String hotelType;

    @OneToMany(mappedBy = "hotel", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Room> rooms = new ArrayList<>();
}
