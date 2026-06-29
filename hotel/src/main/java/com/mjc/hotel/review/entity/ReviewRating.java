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

@Entity
@Table(name = "reivews_rating")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class ReviewRating {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "review_id")
    private Long reviewId;

    @Column(name = "category_id", length = 200)
    private String categoryId;

    @Column(name = "score")
    private Long score;
}
