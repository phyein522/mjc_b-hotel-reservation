package com.mjc.hotel.review.dto;

public record ReviewPhotoRequest(
        String photoPath,
        Long photoOrder
) {
}
