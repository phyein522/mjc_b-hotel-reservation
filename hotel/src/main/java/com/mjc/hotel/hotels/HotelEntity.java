package com.mjc.hotel.hotels;

import com.mjc.hotel.common.dto.BaseEntity;
import com.mjc.hotel.user.dto.IUser;
import com.mjc.hotel.user.entity.UserEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalTime;

@Entity
@Table(name = "hotels")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class HotelEntity extends BaseEntity implements IHotel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "hotel_id", comment = "기본키")
    private Long hotelId;

    @Column(nullable = false, length = 200, comment = "호텔 이름")
    private String name;

    @Column(columnDefinition = "TEXT", comment = "설명")
    private String description;

    @Column(nullable = false, length = 500, comment = "주소")
    private String address;

    @Column(nullable = false, length = 100, comment = "도시")
    private String city;

    @Column(nullable = false, length = 30, comment = "우편번호")
    private String zipCode;

    @Column(nullable = false, length = 30, comment = "전화번호")
    private String phone;

    @Column(nullable = false, length = 255, comment = "이메일")
    private String email;

    @Column(nullable = false, comment = "체크인 시간")
    private LocalTime checkIn;

    @Column(nullable = false, comment = "체크아웃 시간")
    private LocalTime checkOut;

    @Column(comment = "별 등급")
    private int starRate;

    @Column(nullable = false, comment = "운영 여부")
    private Boolean isActive;

    @Column(nullable = false, comment = "위도")
    private BigDecimal latitude;

    @Column(nullable = false, comment = "경도")
    private BigDecimal longitude;

    @Column(nullable = false, comment = "호텔 유형")
    private HotelTypeEnum type;

    @Transient
    private Long userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @Override
    public Long getUserId() {

        if (user != null) {
            return user.getUserId();
        }

        return userId;
    }

    @Override
    public void setUserId(Long userId) {

        this.userId = userId;

        if (user == null) {
            user = new UserEntity();
        }

        user.setUserId(userId);
    }
    @Override
    public void setUser(IUser user) {

        if (user == null) {
            return;
        }

        if (this.user == null) {
            this.user = new UserEntity();
        }

        this.user.copyMembers(user, true);
        this.userId = user.getUserId();
    }
}