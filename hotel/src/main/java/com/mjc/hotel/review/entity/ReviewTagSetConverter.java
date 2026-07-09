package com.mjc.hotel.review.entity;

import com.mjc.hotel.review.enums.ReviewTagType;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Converter
public class ReviewTagSetConverter implements AttributeConverter<Set<ReviewTagType>, String> {

    @Override
    public String convertToDatabaseColumn(Set<ReviewTagType> attribute) {
        if (attribute == null || attribute.isEmpty()) {
            return "";
        }

        return attribute.stream()
                .map(ReviewTagType::getCode)
                .map(String::valueOf)
                .collect(Collectors.joining(","));
    }

    @Override
    public Set<ReviewTagType> convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isBlank()) {
            return new LinkedHashSet<>();
        }

        return Arrays.stream(dbData.split(","))
                .map(String::trim)
                .filter(value -> !value.isBlank())
                .map(Integer::valueOf)
                .map(ReviewTagType::fromCode)
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }
}
