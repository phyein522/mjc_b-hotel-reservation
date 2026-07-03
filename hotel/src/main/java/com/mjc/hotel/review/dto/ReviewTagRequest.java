package com.mjc.hotel.review.dto;

import com.mjc.hotel.review.enums.ReviewTagType;

public record ReviewTagRequest(
        ReviewTagType tag
) {
}
