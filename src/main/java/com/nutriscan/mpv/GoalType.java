package com.nutriscan.mpv;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum GoalType {
    LOSE_WEIGHT,
    GAIN_WEIGHT,
    MAINTAIN_WEIGHT;

    @JsonCreator
    public static GoalType from(String value) {
        return GoalType.valueOf(value.toUpperCase());
    }

    @JsonValue
    public String toJson() {
        return name().toLowerCase();
    }
}
