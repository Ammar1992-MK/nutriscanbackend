package com.nutriscan.mpv.dto;

public record ProductScoreDto(
        String barcode,
        String productName,
        int score
) {}
