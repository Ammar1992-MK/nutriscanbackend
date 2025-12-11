package com.nutriscan.mpv.controllers;

import com.nutriscan.mpv.dto.NutritionProfileDto;
import com.nutriscan.mpv.services.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/user")
@RestController
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/saveProfile")
    public ResponseEntity<NutritionProfileDto> safeProfile(@RequestBody NutritionProfileDto nutritionProfileDto){
        userService.saveNutritionProfile(nutritionProfileDto);
        return ResponseEntity.ok(nutritionProfileDto);
    }

    @PostMapping("/barcode")
    public ResponseEntity<?> getBarcode(@RequestBody String barcode){
        userService.getBarCodeMetadata(barcode);
        return ResponseEntity.ok(barcode);
    }
}
