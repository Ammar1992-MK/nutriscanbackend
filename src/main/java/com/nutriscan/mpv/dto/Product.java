package com.nutriscan.mpv.dto;

public record Product(
        @JsonProperty("product_name") String productName,
        @JsonProperty("nutrition_grades") String nutritionGrades,
        Nutriments nutriments,
        @JsonProperty("nutriscore_data") NutriscoreData nutriscoreData
) {}
