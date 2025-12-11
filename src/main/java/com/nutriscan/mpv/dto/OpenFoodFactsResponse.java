package com.nutriscan.mpv.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record OpenFoodFactsResponse(
        String code,
        Product product,
        Integer status,
        @JsonProperty("status_verbose") String statusVerbose
) {}
