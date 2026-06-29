package com.mjc.hotel.review.dto;

public record ReviewRatingRequest(
        String categoryId,
        Long score
) {
}
