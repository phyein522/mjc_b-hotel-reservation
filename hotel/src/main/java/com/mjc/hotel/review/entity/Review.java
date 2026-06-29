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
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "reviews")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "review_id")
    private Long reviewId;

    @Column(name = "reservation_id")
    private Long reservationId;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "hotel_id")
    private Long hotelId;

    @Column(name = "trip_type_selection_id")
    private Long tripTypeSelectionId;

    @Column(name = "view_count", nullable = false)
    private Short viewCount;

    @Column(name = "title", length = 200)
    private String title;

    @Column(name = "content", columnDefinition = "TEXT")
    private String content;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "overall_rating")
    private Long overallRating;

    @Column(name = "like_count")
    private Long likeCount;

    @Column(name = "dislike_count")
    private Long dislikeCount;
}
