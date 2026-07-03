package com.mjc.hotel.review.dto;

import com.mjc.hotel.review.enums.ReviewRatingCategory;

public record ReviewRatingRequest(
        ReviewRatingCategory category,
        Long score
) {
}
