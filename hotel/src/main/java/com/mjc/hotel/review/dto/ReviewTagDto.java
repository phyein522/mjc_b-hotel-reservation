package com.mjc.hotel.review.dto;

import com.mjc.hotel.review.enums.ReviewTagType;
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
public class ReviewTagDto {
    private ReviewTagType tag;
}
