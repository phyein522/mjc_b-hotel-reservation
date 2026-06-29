package com.mjc.hotel.review.dto;

import com.mjc.hotel.review.entity.ReviewTag;

public record ReviewTagResponse(
        Long reviewTagId,
        Long reviewId,
        Long tagId,
        Boolean status
) {
    public static ReviewTagResponse from(ReviewTag tag) {
        return new ReviewTagResponse(
                tag.getReviewTagId(),
                tag.getReviewId(),
                tag.getTagId(),
                tag.getStatus()
        );
    }
}
