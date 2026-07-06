package com.mjc.hotel.review.dto;

import com.mjc.hotel.review.entity.Review;
import com.mjc.hotel.review.entity.ReviewPhoto;
import com.mjc.hotel.review.enums.ReviewRatingCategory;
import com.mjc.hotel.review.enums.ReviewTagType;
import com.mjc.hotel.review.enums.ReviewTripType;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

public record ReviewResponse(
        Long reviewId,
        Long reservationId,
        Long userId,
        String userName,
        Long hotelId,
        String hotelName,
        Long roomId,
        String roomName,
        ReviewTripType tripType,
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
            String userName,
            String hotelName,
            String roomName
    ) {
        return new ReviewResponse(
                review.getReviewId(),
                review.getReservationId(),
                review.getUserId(),
                userName,
                review.getHotelId(),
                hotelName,
                review.getRoomId(),
                roomName,
                review.getTripType(),
                review.getViewCount(),
                review.getTitle(),
                review.getContent(),
                review.getCreatedAt(),
                review.getUpdatedAt(),
                review.getOverallRating(),
                review.getLikeCount(),
                review.getDislikeCount(),
                photos.stream().map(ReviewPhotoResponse::from).toList(),
                safeTags(review.getTags()).stream().map(ReviewTagResponse::from).toList(),
                safeRatings(review.getCategoryRatings()).entrySet().stream()
                        .map(entry -> ReviewRatingResponse.from(entry.getKey(), entry.getValue()))
                        .toList()
        );
    }

    private static Set<ReviewTagType> safeTags(Set<ReviewTagType> tags) {
        return tags == null ? Collections.emptySet() : tags;
    }

    private static Map<ReviewRatingCategory, Long> safeRatings(Map<ReviewRatingCategory, Long> ratings) {
        return ratings == null ? Collections.emptyMap() : ratings;
    }
}
