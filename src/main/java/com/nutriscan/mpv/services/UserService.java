package com.nutriscan.mpv.services;

import com.nutriscan.mpv.NutritionProfile;
import com.nutriscan.mpv.Product;
import com.nutriscan.mpv.User;
import com.nutriscan.mpv.dto.NutritionProfileDto;
import com.nutriscan.mpv.dto.ScannedProduct;
import com.nutriscan.mpv.repository.NutritionProfileRepository;
import com.nutriscan.mpv.repository.ProductRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final NutritionProfileRepository nutritionProfileRepository;
    private final ProductRepository productRepository;
    private final OpenFoodFactsService openFoodFactsService;

    public UserService(NutritionProfileRepository nutritionProfileRepository, OpenFoodFactsService openFoodFactsService,  ProductRepository productRepository) {
        this.nutritionProfileRepository = nutritionProfileRepository;
        this.openFoodFactsService = openFoodFactsService;
        this.productRepository = productRepository;
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

    public void getBarCodeMetadata(String barCode){
      ScannedProduct scannedProduct = openFoodFactsService.getProductByBarcode(barCode);
      if(scannedProduct!=null){
          saveScannedProduct(scannedProduct,barCode);
      }
    }

    private void saveScannedProduct(ScannedProduct scannedProduct, String barcode){
        Product product = new Product();
        product.setBarcode(barcode);
        product.setCarbs(scannedProduct.nutriments().carbohydratesPer100g());
        product.setEnergy(scannedProduct.nutriments().energyKcalPer100g());
        product.setFat(scannedProduct.nutriments().fatPer100g());
        product.setProtein(scannedProduct.nutriments().proteinsPer100g());
        product.setSugar(scannedProduct.nutriments().sugarsPer100g());
        product.setNovaGroup(scannedProduct.nutriments().novaGroup());
        product.setName(scannedProduct.productName());

        productRepository.save(product);
    }
}
