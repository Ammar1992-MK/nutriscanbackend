package com.nutriscan.mpv.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

 public record Nutriments(
        @JsonProperty("carbohydrates_100g") Double carbohydratesPer100g,
        @JsonProperty("energy-kcal_100g") Double energyKcalPer100g,
        @JsonProperty("fat_100g") Double fatPer100g,
        @JsonProperty("nova-group") Integer novaGroup,
        @JsonProperty("proteins_100g") Double proteinsPer100g,
        @JsonProperty("salt_100g") Double saltPer100g,
        @JsonProperty("sugars_100g") Double sugarsPer100g
) {}
