package com.mjc.hotel.hotelsimage;

import com.mjc.hotel.common.dto.BaseEntity;
import com.mjc.hotel.hotels.HotelEntity;
import com.mjc.hotel.hotels.IHotel;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "hotel_images")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HotelImageEntity extends BaseEntity implements IHotelImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "hotel_image_id")
    private Long hotelImageId;

    @Column(nullable = false, length = 100)
    private String fileName;

    @Column(nullable = false)
    private Integer size;

    @Column(name = "ext")
    private String ext;

    @Column(name = "store_name")
    private String storeName;

    @Column(name = "path")
    private String path;

    @Transient
    private Long hotelId;

    @JoinColumn(name = "hotel_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private HotelEntity hotel;

    @Override
    public Long getHotelId() {

        if(hotel != null){
            return hotel.getHotelId();
        }

        return hotelId;
    }

    @Override
    public void setHotelId(Long hotelId) {

        this.hotelId = hotelId;

        if (this.hotel == null) {
            this.hotel = new HotelEntity();
        }

        this.hotel.setHotelId(hotelId);
        this.hotelId = hotelId;
    }
    @Override
    public HotelEntity getHotel() {
        return hotel;
    }
    @Override
    public void setHotel(IHotel hotel) {

        if (hotel == null) {
            this.hotel = null;
            return;
        }

        if (this.hotel == null) {
            this.hotel = new HotelEntity();
        }

        this.hotel.copyMembers(hotel, true);
        this.hotelId = hotel.getHotelId();
    }
}
