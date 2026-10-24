package com.example.backend_pj4.infrastructure.database.converter;

import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;
import java.util.stream.Collectors;

import com.example.backend_pj4.common.constants.enums.VideoQuality;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class VideoQualitySetConverter implements AttributeConverter<Set<VideoQuality>, String> {

    private static final String DELIMITER = ",";
    private static final Set<VideoQuality> DEFAULT_SET = Collections.unmodifiableSet(EnumSet.of(VideoQuality.HD));

    @Override
    public String convertToDatabaseColumn(Set<VideoQuality> attribute) {
        if (attribute == null || attribute.isEmpty()) {
            return VideoQuality.HD.name();
        }
        return attribute.stream()
                .map(Enum::name)
                .collect(Collectors.joining(DELIMITER));
    }

    @Override
    public Set<VideoQuality> convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.trim().isEmpty()) {
            return DEFAULT_SET;
        }
        try {
            Set<VideoQuality> set = Arrays.stream(dbData.split(DELIMITER))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .map(VideoQuality::valueOf)
                    .collect(Collectors.toCollection(() -> EnumSet.noneOf(VideoQuality.class)));
            return set.isEmpty() ? DEFAULT_SET : set;
        } catch (IllegalArgumentException e) {
            return DEFAULT_SET;
        }
    }
}
