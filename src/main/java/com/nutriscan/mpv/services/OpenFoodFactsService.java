package com.nutriscan.mpv.services;

import com.nutriscan.mpv.dto.OpenFoodFactsResponse;
import com.nutriscan.mpv.dto.ScannedProduct;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

@Service
public class OpenFoodFactsService {

    private final RestTemplate restTemplate;
    private static final String BASE_URL = "https://world.openfoodfacts.net/api/v2/product";
    private static final String FIELDS = "product_name,nutriscore_data,nutriments,nutrition_grades";

    public OpenFoodFactsService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public ScannedProduct getProductByBarcode(String barcode) {
        String url =  String.format("%s/%s?fields=%s", BASE_URL, barcode, FIELDS);
            ResponseEntity<OpenFoodFactsResponse> response = restTemplate.getForEntity(url, OpenFoodFactsResponse.class);

            if(response.getBody() == null || response.getBody().status() != 1 || response.getBody().product() == null) {

                 throw new ResponseStatusException(HttpStatus.NOT_FOUND);
            }
            return response.getBody().product();
    }
}
