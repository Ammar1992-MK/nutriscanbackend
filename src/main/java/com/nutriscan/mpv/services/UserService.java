package com.nutriscan.mpv.services;

import com.nutriscan.mpv.NutritionProfile;
import com.nutriscan.mpv.User;
import com.nutriscan.mpv.dto.NutritionProfileDto;
import com.nutriscan.mpv.repository.NutritionProfileRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final NutritionProfileRepository nutritionProfileRepository;

    public UserService(NutritionProfileRepository nutritionProfileRepository) {
        this.nutritionProfileRepository = nutritionProfileRepository;
    }

    public void saveNutritionProfile(NutritionProfileDto nutritionProfileDto){
        System.out.println(nutritionProfileDto);
        User currentUser =(User)  SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        NutritionProfile nutritionProfile = new NutritionProfile();
        nutritionProfile.setGoal(nutritionProfileDto.goal());
        nutritionProfile.setDiet(nutritionProfileDto.diet());
        nutritionProfile.setAllergy(nutritionProfileDto.allergy());
        nutritionProfile.setOtherPreferences(nutritionProfileDto.otherPreferences());
        nutritionProfile.setUser(currentUser);
        nutritionProfileRepository.save(nutritionProfile);
    }
}
