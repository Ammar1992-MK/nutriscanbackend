package com.nutriscan.mpv.services;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Service
public class OpenFoodFactsService {

    private final RestTemplate restTemplate;
    private static final String BASE_URL = "https://world.openfoodfacts.net/api/v2/product";
    private static final String FIELDS = "product_name,nutriscore_data,nutriments,nutrition_grades";

    public OpenFoodFactsService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public void getProductByBarcode(String barcode) {
        String url =  String.format("%s/%s?fields=%s", BASE_URL, barcode, FIELDS);
        try {
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            System.out.println(response.getBody());
        }catch (HttpClientErrorException e) {
            throw new RuntimeException("Failed to fetch product data: " + e.getMessage(),e);
        }
    }
}
