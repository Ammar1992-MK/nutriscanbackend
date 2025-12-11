package com.nutriscan.mpv.dto;

import java.util.List;

public record Components(
        List<NutrientComponent> negative,
        List<NutrientComponent> positive
) {}
