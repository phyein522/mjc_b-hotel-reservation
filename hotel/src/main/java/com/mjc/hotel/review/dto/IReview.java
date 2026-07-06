package com.mjc.hotel.review.dto;

import com.mjc.hotel.common.dto.IBase;
import com.mjc.hotel.review.enums.ReviewTripType;

import java.time.LocalDateTime;
import java.util.List;

@tools.jackson.databind.annotation.JsonDeserialize(as = ReviewDto.class)
public interface IReview extends IBase {
    Long getReviewId();
    void setReviewId(Long reviewId);

    Long getReservationId();
    void setReservationId(Long reservationId);

    Long getUserId();
    void setUserId(Long userId);

    String getUserName();
    void setUserName(String userName);

    Long getHotelId();
    void setHotelId(Long hotelId);

    String getHotelName();
    void setHotelName(String hotelName);

    Long getRoomId();
    void setRoomId(Long roomId);

    String getRoomName();
    void setRoomName(String roomName);

    ReviewTripType getTripType();
    void setTripType(ReviewTripType tripType);

    Short getViewCount();
    void setViewCount(Short viewCount);

    String getTitle();
    void setTitle(String title);

    String getContent();
    void setContent(String content);

    LocalDateTime getUpdatedAt();
    void setUpdatedAt(LocalDateTime updatedAt);

    Long getOverallRating();
    void setOverallRating(Long overallRating);

    Long getLikeCount();
    void setLikeCount(Long likeCount);

    Long getDislikeCount();
    void setDislikeCount(Long dislikeCount);

    List<ReviewPhotoDto> getPhotos();
    void setPhotos(List<ReviewPhotoDto> photos);

    List<ReviewTagDto> getTags();
    void setTags(List<ReviewTagDto> tags);

    List<ReviewRatingDto> getRatings();
    void setRatings(List<ReviewRatingDto> ratings);

    default IReview copyMembers(IReview source, boolean forced) {
        if (source == null) {
            return this;
        }
        IBase.super.copyMembers(source, forced);
        if (forced || source.getReviewId() != null) {
            this.setReviewId(source.getReviewId());
        }
        if (forced || source.getReservationId() != null) {
            this.setReservationId(source.getReservationId());
        }
        if (forced || source.getUserId() != null) {
            this.setUserId(source.getUserId());
        }
        if (forced || source.getUserName() != null) {
            this.setUserName(source.getUserName());
        }
        if (forced || source.getHotelId() != null) {
            this.setHotelId(source.getHotelId());
        }
        if (forced || source.getHotelName() != null) {
            this.setHotelName(source.getHotelName());
        }
        if (forced || source.getRoomId() != null) {
            this.setRoomId(source.getRoomId());
        }
        if (forced || source.getRoomName() != null) {
            this.setRoomName(source.getRoomName());
        }
        if (forced || source.getTripType() != null) {
            this.setTripType(source.getTripType());
        }
        if (forced || source.getViewCount() != null) {
            this.setViewCount(source.getViewCount());
        }
        if (forced || source.getTitle() != null) {
            this.setTitle(source.getTitle());
        }
        if (forced || source.getContent() != null) {
            this.setContent(source.getContent());
        }
        if (forced || source.getUpdatedAt() != null) {
            this.setUpdatedAt(source.getUpdatedAt());
        }
        if (forced || source.getOverallRating() != null) {
            this.setOverallRating(source.getOverallRating());
        }
        if (forced || source.getLikeCount() != null) {
            this.setLikeCount(source.getLikeCount());
        }
        if (forced || source.getDislikeCount() != null) {
            this.setDislikeCount(source.getDislikeCount());
        }
        if (forced || source.getPhotos() != null) {
            this.setPhotos(source.getPhotos());
        }
        if (forced || source.getTags() != null) {
            this.setTags(source.getTags());
        }
        if (forced || source.getRatings() != null) {
            this.setRatings(source.getRatings());
        }
        return this;
    }
}
