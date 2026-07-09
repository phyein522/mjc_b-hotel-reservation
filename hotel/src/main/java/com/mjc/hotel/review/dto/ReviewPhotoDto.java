package com.mjc.hotel.review.dto;

import com.mjc.hotel.review.entity.ReviewPhoto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class ReviewPhotoDto {
    private Long id;
    private Long reviewId;
    private String photoPath;
    private Integer photoOrder;

    public static ReviewPhotoDto from(ReviewPhoto photo) {
        if (photo == null) {
            return null;
        }
        return ReviewPhotoDto.builder()
                .id(photo.getId())
                .reviewId(photo.getReviewId())
                .photoPath(photo.getPhotoPath())
                .photoOrder(photo.getPhotoOrder())
                .build();
    }
}
