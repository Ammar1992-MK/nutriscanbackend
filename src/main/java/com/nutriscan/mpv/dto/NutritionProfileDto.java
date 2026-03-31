package com.nutriscan.mpv.dto;

import com.nutriscan.mpv.GoalType;

public record NutritionProfileDto(GoalType goal, String allergy, String diet, String otherPreferences){}
