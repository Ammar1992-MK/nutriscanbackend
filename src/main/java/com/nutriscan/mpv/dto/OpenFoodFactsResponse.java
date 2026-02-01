package com.nutriscan.mpv.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record OpenFoodFactsResponse(
        String code,
        ScannedProduct product,
        @JsonProperty("status_verbose") String statusVerbose,
        @JsonProperty("status") Integer status
) {}
