package com.mjc.hotel.hotelsimage;

import com.mjc.hotel.hotels.HotelEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "hotel_images")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HotelImageEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "hotel_image_id")
    private Long hotelImageId;

    @Column(name = "image_url", nullable = false, columnDefinition = "TEXT")
    private String imageUrl;

    @Column(name = "sort_order")
    private Integer sortOrder;

    @Column(name = "is_thumbnail")
    private Boolean isThumbnail;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Transient
    private Long hotelId;

    @JoinColumn(name = "hotel_id", nullable = true)
    @ManyToOne(fetch = FetchType.LAZY)
    private HotelEntity hotel;

}
