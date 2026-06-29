package com.mjc.hotel.sales_analysis.entity;

import jakarta.persistence.*;
import lombok.Getter;
import java.time.LocalTime;
import java.time.LocalDateTime;

@Getter
@Entity
@Table(name = "hotels")
public class Hotel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "호텔아이디")
    private Long hotelId;

    @Column(name = "이름", nullable = false, length = 200)
    private String hotelName;

    @Column(name = "설명", columnDefinition = "TEXT")
    private String description;

    @Column(name = "주소", nullable = false, length = 500)
    private String address;

    @Column(name = "도시", nullable = false, length = 100)
    private String city;

    @Column(name = "우편번호", length = 30)
    private String zipCode;

    @Column(name = "전화번호", length = 30)
    private String phone;

    @Column(name = "이메일", length = 255)
    private String email;

    // 띄어쓰기 있는 컬럼명은 백틱 대신 name으로 정확히 매핑
    @Column(name = "체크인 시간", nullable = false)
    private LocalTime checkInTime;

    @Column(name = "체크아웃 시간", nullable = false)
    private LocalTime checkOutTime;

    @Column(name = "별_등급")
    private Short starGrade;

    // 운영중인 호텔만 드롭다운에 표시
    @Column(name = "운영여부", nullable = false)
    private Boolean isActive;

    @Column(name = "생성 시간", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "업데이트 시간", nullable = false)
    private LocalDateTime updatedAt;
}
