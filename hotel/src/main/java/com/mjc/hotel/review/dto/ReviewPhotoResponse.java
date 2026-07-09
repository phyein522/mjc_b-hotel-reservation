package com.mjc.hotel.review.dto;

import com.mjc.hotel.review.entity.ReviewPhoto;

public record ReviewPhotoResponse(
        Long id,
        Long reviewId,
        String photoPath,
        Integer photoOrder
) {
    public static ReviewPhotoResponse from(ReviewPhoto photo) {
        return new ReviewPhotoResponse(
                photo.getId(),
                photo.getReviewId(),
                photo.getPhotoPath(),
                photo.getPhotoOrder()
        );
    }
}
