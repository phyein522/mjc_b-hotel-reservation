package com.mjc.hotel.review.dto;

import com.mjc.hotel.common.dto.BaseDto;
import com.mjc.hotel.review.enums.ReviewTripType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class ReviewDto extends BaseDto implements IReview {
    private Long reviewId;
    private Long reservationId;
    private Long userId;
    private String userName;
    private Long hotelId;
    private String hotelName;
    private Long roomId;
    private String roomName;
    private ReviewTripType tripType;
    private Short viewCount;
    private String title;
    private String content;
    private LocalDateTime updatedAt;
    private Long overallRating;
    private Long likeCount;
    private Long dislikeCount;
    private List<ReviewPhotoDto> photos;
    private List<ReviewTagDto> tags;
    private List<ReviewRatingDto> ratings;
}
