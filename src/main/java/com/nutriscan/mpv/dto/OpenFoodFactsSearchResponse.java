package com.nutriscan.mpv.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record OpenFoodFactsSearchResponse(
        List<ScannedProduct> products,
        Integer count,
        Integer page
) {}

