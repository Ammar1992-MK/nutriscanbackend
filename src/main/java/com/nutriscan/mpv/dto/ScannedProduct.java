package com.nutriscan.mpv.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ScannedProduct(
        String barcode,
        @JsonProperty("product_name") String productName,
        @JsonProperty("nutrition_grades") String nutritionGrades,
        Nutriments nutriments,
        @JsonProperty("nutriscore_data") NutriscoreData nutriscoreData,
        @JsonProperty("categories_tags") List<String> categoryTags,
        @JsonProperty("image_front_thumb_url") String imageUrl
) {}
