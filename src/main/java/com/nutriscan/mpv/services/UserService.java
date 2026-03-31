package com.nutriscan.mpv.services;

import com.nutriscan.mpv.NutritionProfile;
import com.nutriscan.mpv.Product;
import com.nutriscan.mpv.User;
import com.nutriscan.mpv.UserProduct;
import com.nutriscan.mpv.calculations.CalculateScore;
import com.nutriscan.mpv.dto.NutritionProfileDto;
import com.nutriscan.mpv.dto.ProductScoreDto;
import com.nutriscan.mpv.dto.ScannedProduct;
import com.nutriscan.mpv.repository.NutritionProfileRepository;
import com.nutriscan.mpv.repository.ProductRepository;
import com.nutriscan.mpv.repository.UserProductRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private static final int SUGGESTION_THRESHOLD = 85;

    private final NutritionProfileRepository nutritionProfileRepository;
    private final ProductRepository productRepository;
    private final UserProductRepository userProductRepository;
    private final OpenFoodFactsService openFoodFactsService;
    private final SuggestionService suggestionService;

    public UserService(NutritionProfileRepository nutritionProfileRepository,
                       OpenFoodFactsService openFoodFactsService,
                       ProductRepository productRepository,
                       UserProductRepository userProductRepository,
                       SuggestionService suggestionService) {
        this.nutritionProfileRepository = nutritionProfileRepository;
        this.openFoodFactsService = openFoodFactsService;
        this.productRepository = productRepository;
        this.userProductRepository = userProductRepository;
        this.suggestionService = suggestionService;
    }

    public void saveNutritionProfile(NutritionProfileDto nutritionProfileDto) {
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        NutritionProfile nutritionProfile = new NutritionProfile();
        nutritionProfile.setGoal(nutritionProfileDto.goal());
        nutritionProfile.setDiet(nutritionProfileDto.diet());
        nutritionProfile.setAllergy(nutritionProfileDto.allergy());
        nutritionProfile.setOtherPreferences(nutritionProfileDto.otherPreferences());
        nutritionProfile.setUser(currentUser);
        nutritionProfileRepository.save(nutritionProfile);
    }

    public ProductScoreDto getBarCodeMetadata(String barCode) {
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        //check product exist
        Product product = productRepository.getByBarcode(barCode);

        if (product == null) {
            //presist a new product
            ScannedProduct scannedProduct = openFoodFactsService.getProductByBarcode(barCode);
            Product newProduct = saveScannedProduct(scannedProduct, barCode);
            return calculateAndSaveScore(currentUser, newProduct);
        } else {
            //fetch existing product from db
            UserProduct userProduct = userProductRepository.findByUserAndProduct(currentUser, product);
            if(userProduct.getScore() < SUGGESTION_THRESHOLD && product.getCategoryTag() != null) {
                suggestionService.findAndPushSuggestions(product.getCategoryTag(), currentUser, userProduct.getScore());
            }
            return new ProductScoreDto(product.getBarcode(), product.getName(), userProduct.getScore());
        }
    }

    private ProductScoreDto calculateAndSaveScore(User user, Product product) {
        int score = CalculateScore.INSTANCE.calculate(product, user);
        userProductRepository.save(new UserProduct(user, product, score));
        if (score < SUGGESTION_THRESHOLD && product.getCategoryTag() != null) {
            suggestionService.findAndPushSuggestions(product.getCategoryTag(), user, score);
        }

        return new ProductScoreDto(product.getBarcode(), product.getName(), score);
    }

    private Product saveScannedProduct(ScannedProduct scannedProduct, String barcode) {
        String categoryTag = scannedProduct.categoryTags() == null ? null :
                scannedProduct.categoryTags().stream()
                        .filter(tag -> tag.startsWith("en:"))
                        .reduce((first, second) -> second)
                        .orElse(null);

        Product product = new Product();
        product.setBarcode(barcode);
        product.setCarbs(scannedProduct.nutriments().carbohydratesPer100g());
        product.setEnergy(scannedProduct.nutriments().energyKcalPer100g());
        product.setFat(scannedProduct.nutriments().fatPer100g());
        product.setProtein(scannedProduct.nutriments().proteinsPer100g());
        product.setSugar(scannedProduct.nutriments().sugarsPer100g());
        product.setNovaGroup(scannedProduct.nutriments().novaGroup());
        product.setName(scannedProduct.productName());
        product.setCategoryTag(categoryTag);
        return productRepository.save(product);
    }
}
