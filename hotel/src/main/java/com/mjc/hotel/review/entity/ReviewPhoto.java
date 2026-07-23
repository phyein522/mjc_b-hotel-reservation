package com.mjc.hotel.review.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

@Entity
@Table(name = "reviews_photo")
@Comment("리뷰 사진")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class ReviewPhoto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("리뷰 사진 ID")
    private Long id;

    @Column(name = "review_id", nullable = false)
    @Comment("리뷰 ID")
    private Long reviewId;

    @Column(name = "photo_path", nullable = false, length = 500)
    @Comment("리뷰 사진 경로")
    private String photoPath;

    @Column(name = "photo_order")
    @Comment("리뷰 사진 정렬 순서")
    private Integer photoOrder;
}
