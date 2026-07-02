package com.mjc.hotel.review.dto;

import com.mjc.hotel.review.entity.ReviewRatingCategory;

public record ReviewRatingRequest(
        ReviewRatingCategory category,
        Long score
) {
}
