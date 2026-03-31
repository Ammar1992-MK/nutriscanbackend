package com.nutriscan.mpv;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class GaolTypeConverter implements AttributeConverter<GoalType, String> {
    @Override
    public String convertToDatabaseColumn(GoalType goalType) {
        return goalType == null ? null : goalType.name().toLowerCase();
    }

    @Override
    public GoalType convertToEntityAttribute(String s) {
        return s == null ? null : GoalType.valueOf(s.toUpperCase());
    }
}
