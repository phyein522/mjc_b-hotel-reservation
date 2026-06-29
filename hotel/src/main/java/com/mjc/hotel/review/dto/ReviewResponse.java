package com.mjc.hotel.review.dto;

import com.mjc.hotel.review.entity.Review;
import com.mjc.hotel.review.entity.ReviewPhoto;
import com.mjc.hotel.review.entity.ReviewRating;
import com.mjc.hotel.review.entity.ReviewTag;

import java.time.LocalDateTime;
import java.util.List;

public record ReviewResponse(
        Long reviewId,
        Long reservationId,
        Long userId,
        Long hotelId,
        Long tripTypeSelectionId,
        Short viewCount,
        String title,
        String content,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        Long overallRating,
        Long likeCount,
        Long dislikeCount,
        List<ReviewPhotoResponse> photos,
        List<ReviewTagResponse> tags,
        List<ReviewRatingResponse> ratings
) {
    public static ReviewResponse from(
            Review review,
            List<ReviewPhoto> photos,
            List<ReviewTag> tags,
            List<ReviewRating> ratings
    ) {
        return new ReviewResponse(
                review.getReviewId(),
                review.getReservationId(),
                review.getUserId(),
                review.getHotelId(),
                review.getTripTypeSelectionId(),
                review.getViewCount(),
                review.getTitle(),
                review.getContent(),
                review.getCreatedAt(),
                review.getUpdatedAt(),
                review.getOverallRating(),
                review.getLikeCount(),
                review.getDislikeCount(),
                photos.stream().map(ReviewPhotoResponse::from).toList(),
                tags.stream().map(ReviewTagResponse::from).toList(),
                ratings.stream().map(ReviewRatingResponse::from).toList()
        );
    }
}
