package com.mjc.hotel.review.dto;

import com.mjc.hotel.review.entity.ReviewTagType;

public record ReviewTagRequest(
        ReviewTagType tag
) {
}
