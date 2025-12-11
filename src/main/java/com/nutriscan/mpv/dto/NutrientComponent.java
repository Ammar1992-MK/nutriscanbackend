package com.nutriscan.mpv.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record NutrientComponent(
        String id,
        Integer points,
        @JsonProperty("points_max") Integer pointsMax,
        String unit,
        Double value
) {}
