package com.nutriscan.mpv.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record NutriscoreData(
        Components components,
        @JsonProperty("count_proteins") Integer countProteins,
        @JsonProperty("count_proteins_reason") String countProteinsReason,
        String grade,
        @JsonProperty("is_beverage") Integer isBeverage,
        @JsonProperty("is_cheese") Integer isCheese,
        @JsonProperty("is_fat_oil_nuts_seeds") Integer isFatOilNutsSeeds,
        @JsonProperty("is_red_meat_product") Integer isRedMeatProduct,
        @JsonProperty("is_water") Integer isWater,
        @JsonProperty("negative_points") Integer negativePoints,
        @JsonProperty("negative_points_max") Integer negativePointsMax,
        @JsonProperty("positive_nutrients") List<String> positiveNutrients,
        @JsonProperty("positive_points") Integer positivePoints,
        @JsonProperty("positive_points_max") Integer positivePointsMax,
        Integer score
) {}
