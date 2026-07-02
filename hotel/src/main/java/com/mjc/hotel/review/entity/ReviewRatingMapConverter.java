package com.mjc.hotel.review.entity;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.Map;
import java.util.stream.Collectors;

@Converter
public class ReviewRatingMapConverter implements AttributeConverter<Map<ReviewRatingCategory, Long>, String> {

    @Override
    public String convertToDatabaseColumn(Map<ReviewRatingCategory, Long> attribute) {
        if (attribute == null || attribute.isEmpty()) {
            return "";
        }

        return attribute.entrySet()
                .stream()
                .map(entry -> entry.getKey().name() + ":" + entry.getValue())
                .collect(Collectors.joining(","));
    }

    @Override
    public Map<ReviewRatingCategory, Long> convertToEntityAttribute(String dbData) {
        Map<ReviewRatingCategory, Long> ratings = new EnumMap<>(ReviewRatingCategory.class);
        if (dbData == null || dbData.isBlank()) {
            return ratings;
        }

        Arrays.stream(dbData.split(","))
                .map(value -> value.split(":"))
                .filter(parts -> parts.length == 2)
                .forEach(parts -> ratings.put(
                        ReviewRatingCategory.valueOf(parts[0].trim()),
                        Long.valueOf(parts[1].trim())
                ));

        return ratings;
    }
}
