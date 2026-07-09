package com.mjc.hotel.hotelsamen;

import com.mjc.hotel.common.dto.BaseEntity;
import com.mjc.hotel.hotels.HotelEntity;
import com.mjc.hotel.hotels.IHotel;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "amenities")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HotelAmenEntity extends BaseEntity implements IHotelAmen {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "amen_Id", comment = "기본키")
    private Long amenId;

    @Column(comment = "무료 와이파이")
    private Boolean wifi;

    @Column(comment = "수영장")
    private Boolean pool;

    @Column(comment = "피트니스 센터")
    private Boolean fitnessCenter;

    @Column(comment = "스파")
    private Boolean spa;

    @Column(comment = "레스토랑")
    private Boolean restaurant;

    @Column(comment = "대리주차")
    private Boolean valetParking;

    @Column(comment = "무료주차")
    private Boolean freeParking;

    @Column(comment = "안내인")
    private Boolean concierge;

    @Column(comment = "바")
    private Boolean bar;

    @Column(comment = "조식")
    private Boolean breakfast;

    @Column(comment = "공항 셔틀")
    private Boolean airportShuttle;

    @Column(comment = "룸 서비스")
    private Boolean roomService;

    @Column(comment = "세탁")
    private Boolean laundry;

    @Column(comment = "라운지")
    private Boolean lounge;

    @Column(comment = "사우나")
    private Boolean sauna;

    @Column(comment = "무료 취소")
    private Boolean freeCancel;

    @Column(comment = "반려동물 동반")
    private Boolean petFriendly;

    @Transient
    private Long hotelId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hotel_id", nullable = false)
    private HotelEntity hotel;

    @Override
    public Long getHotelId() {

        if (hotel != null) {
            return hotel.getHotelId();
        }

        return hotelId;
    }

    @Override
    public void setHotelId(Long hotelId) {

        this.hotelId = hotelId;

        if (hotel == null) {
            hotel = new HotelEntity();
        }

        hotel.setHotelId(hotelId);
    }
    @Override
    public void setHotel(IHotel hotel) {

        if (hotel == null) {
            return;
        }

        if (this.hotel == null) {
            this.hotel = new HotelEntity();
        }

        this.hotel.copyMembers(hotel, true);
        this.hotelId = hotel.getHotelId();
    }
}
