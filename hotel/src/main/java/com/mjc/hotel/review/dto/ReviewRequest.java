package com.mjc.hotel.review.dto;

import java.util.List;

public record ReviewRequest(
        Long reservationId,
        Long userId,
        Long hotelId,
        Long tripTypeSelectionId,
        Short viewCount,
        String title,
        String content,
        Long overallRating,
        Long likeCount,
        Long dislikeCount,
        List<ReviewPhotoRequest> photos,
        List<ReviewTagRequest> tags,
        List<ReviewRatingRequest> ratings
) {
}
