package com.nutriscan.mpv.services;

import com.nutriscan.mpv.Product;
import com.nutriscan.mpv.User;
import com.nutriscan.mpv.calculations.CalculateScore;
import com.nutriscan.mpv.dto.ScannedProduct;
import com.nutriscan.mpv.dto.SuggestedProductDto;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SuggestionService {

    private final OpenFoodFactsService openFoodFactsService;
    private final SimpMessagingTemplate messagingTemplate;

    public SuggestionService(OpenFoodFactsService openFoodFactsService,
                             SimpMessagingTemplate messagingTemplate) {
        this.openFoodFactsService = openFoodFactsService;
        this.messagingTemplate = messagingTemplate;
    }

    @Async
    public void findAndPushSuggestions(String categoryTag, User user, int originalScore) {
        List<SuggestedProductDto> suggestions = openFoodFactsService.searchByCategory(categoryTag, user.getCountry())
                .stream()
                .filter(p -> p.nutriments() != null && p.productName() != null)
                .map(p -> {
                    Product temp = toTempProduct(p);
                    int suggestionScore = CalculateScore.INSTANCE.calculate(temp, user);
                    return new SuggestedProductDto(
                            p.productName(),
                            suggestionScore
                    );
                })
                .filter(s -> s.score() > originalScore)
                .sorted(Comparator.comparingInt(SuggestedProductDto::score).reversed())
                .limit(3)
                .collect(Collectors.toList());

        messagingTemplate.convertAndSendToUser(
                user.getEmail(),
                "/topic/suggestions",
                suggestions
        );
    }

    private Product toTempProduct(ScannedProduct scannedProduct) {
        Product p = new Product();
        p.setEnergy(scannedProduct.nutriments().energyKcalPer100g());
        p.setCarbs(scannedProduct.nutriments().carbohydratesPer100g());
        p.setFat(scannedProduct.nutriments().fatPer100g());
        p.setSugar(scannedProduct.nutriments().sugarsPer100g());
        p.setProtein(scannedProduct.nutriments().proteinsPer100g());
        p.setNovaGroup(scannedProduct.nutriments().novaGroup());
        p.setName(scannedProduct.productName());
        return p;
    }
}

