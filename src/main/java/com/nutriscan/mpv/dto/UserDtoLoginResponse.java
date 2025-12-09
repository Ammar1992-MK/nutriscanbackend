package com.nutriscan.mpv.dto;

public record UserDtoLoginResponse(String fullName, String email, String phoneNumber, String country, NutritionProfileDto nutritionProfileDto, String token){}
