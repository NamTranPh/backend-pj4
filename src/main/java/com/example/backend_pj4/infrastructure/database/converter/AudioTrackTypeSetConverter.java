package com.example.backend_pj4.infrastructure.database.converter;

import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;
import java.util.stream.Collectors;

import com.example.backend_pj4.common.constants.enums.AudioTrackType;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class AudioTrackTypeSetConverter implements AttributeConverter<Set<AudioTrackType>, String> {

    private static final String DELIMITER = ",";
    private static final Set<AudioTrackType> DEFAULT_SET = Collections.unmodifiableSet(EnumSet.of(AudioTrackType.VIETSUB));

    @Override
    public String convertToDatabaseColumn(Set<AudioTrackType> attribute) {
        if (attribute == null || attribute.isEmpty()) {
            return AudioTrackType.VIETSUB.name();
        }
        return attribute.stream()
                .map(Enum::name)
                .collect(Collectors.joining(DELIMITER));
    }

    @Override
    public Set<AudioTrackType> convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.trim().isEmpty()) {
            return DEFAULT_SET;
        }
        try {
            Set<AudioTrackType> set = Arrays.stream(dbData.split(DELIMITER))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .map(AudioTrackType::valueOf)
                    .collect(Collectors.toCollection(() -> EnumSet.noneOf(AudioTrackType.class)));
            return set.isEmpty() ? DEFAULT_SET : set;
        } catch (IllegalArgumentException e) {
            return DEFAULT_SET;
        }
    }
}
