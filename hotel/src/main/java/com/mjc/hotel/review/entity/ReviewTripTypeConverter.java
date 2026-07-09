package com.mjc.hotel.review.entity;

import com.mjc.hotel.review.enums.ReviewTripType;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class ReviewTripTypeConverter implements AttributeConverter<ReviewTripType, Integer> {

    @Override
    public Integer convertToDatabaseColumn(ReviewTripType attribute) {
        return attribute == null ? null : attribute.getCode();
    }

    @Override
    public ReviewTripType convertToEntityAttribute(Integer dbData) {
        return ReviewTripType.fromCode(dbData);
    }
}
