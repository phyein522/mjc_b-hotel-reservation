package com.mjc.hotel.review.dto;

import com.mjc.hotel.review.enums.ReviewRatingCategory;
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
public class ReviewRatingDto {
    private ReviewRatingCategory category;
    private Long score;
}
