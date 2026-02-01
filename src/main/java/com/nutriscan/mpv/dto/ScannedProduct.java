package com.nutriscan.mpv.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ScannedProduct(
        String barcode,
        @JsonProperty("product_name") String productName,
        @JsonProperty("nutrition_grades") String nutritionGrades,
        Nutriments nutriments,
        @JsonProperty("nutriscore_data") NutriscoreData nutriscoreData
) {}
