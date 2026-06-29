package com.mjc.hotel.review.dto;

import com.mjc.hotel.review.entity.ReviewRating;

public record ReviewRatingResponse(
        Long id,
        Long reviewId,
        String categoryId,
        Long score
) {
    public static ReviewRatingResponse from(ReviewRating rating) {
        return new ReviewRatingResponse(
                rating.getId(),
                rating.getReviewId(),
                rating.getCategoryId(),
                rating.getScore()
        );
    }
}
