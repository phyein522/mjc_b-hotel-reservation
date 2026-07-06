package com.mjc.hotel.review.dto;

import com.mjc.hotel.review.enums.ReviewRatingCategory;

public record ReviewRatingResponse(
        ReviewRatingCategory category,
        Long score
) {
    public static ReviewRatingResponse from(ReviewRatingCategory category, Long score) {
        return new ReviewRatingResponse(category, score);
    }
}
