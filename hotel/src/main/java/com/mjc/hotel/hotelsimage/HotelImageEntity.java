package com.mjc.hotel.hotelsimage;

import com.mjc.hotel.common.dto.BaseEntity;
import com.mjc.hotel.hotels.HotelEntity;
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
    @Column(name = "image_id")
    private Long imageId;

    @Column(name = "image_url", nullable = false, columnDefinition = "TEXT")
    private String url;

    @Column(name = "sort_order")
    private Integer sortOrder;

    @Column(name = "is_thumbnail")
    private Boolean isThumbnail;

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

        if(hotel == null){
            hotel = new HotelEntity();
        }

        hotel.setHotelId(hotelId);
    }
}
