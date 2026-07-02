package com.mjc.hotel.review.dto;

import com.mjc.hotel.review.enums.ReviewTagType;

public record ReviewTagResponse(
        ReviewTagType tag
) {
    public static ReviewTagResponse from(ReviewTagType tag) {
        return new ReviewTagResponse(tag);
    }
}
