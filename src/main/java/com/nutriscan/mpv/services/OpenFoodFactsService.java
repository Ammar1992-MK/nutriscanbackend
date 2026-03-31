package com.nutriscan.mpv.services;

import com.nutriscan.mpv.dto.OpenFoodFactsResponse;
import com.nutriscan.mpv.dto.OpenFoodFactsSearchResponse;
import com.nutriscan.mpv.dto.ScannedProduct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class OpenFoodFactsService {

    private static final Logger log = LoggerFactory.getLogger(OpenFoodFactsService.class);

    private final RestTemplate restTemplate;
    private static final String BASE_URL = "https://world.openfoodfacts.org/api/v2/product";
    private static final String SEARCH_URL = "https://world.openfoodfacts.org/api/v2/search";
    private static final String FIELDS = "product_name,nutriscore_data,nutriments,nutrition_grades,categories_tags,image_front_thumb_url";
    private static final int SUGGESTION_PAGE_SIZE = 10;

    public OpenFoodFactsService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public ScannedProduct getProductByBarcode(String barcode) {
        String url = String.format("%s/%s?fields=%s", BASE_URL, barcode, FIELDS);
        try {
            ResponseEntity<OpenFoodFactsResponse> response = restTemplate.getForEntity(url, OpenFoodFactsResponse.class);
            if (response.getBody() == null || response.getBody().status() != 1 || response.getBody().product() == null) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Product with barcode " + barcode + " not found");
            }
            return response.getBody().product();
        } catch (HttpClientErrorException.TooManyRequests e) {
            throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS, "OpenFoodFacts rate limit reached, please try again later");
        } catch (HttpClientErrorException.NotFound e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Product with barcode " + barcode + " not found");
        }
    }

    @Cacheable(value = "suggestions", key = "#categoryTag + '-' + #country")
    public List<ScannedProduct> searchByCategory(String categoryTag, String country) {
        String normalizedCountry = "en:" + country.toLowerCase().replace(" ", "-");
        String url = String.format(
                "%s?categories_tags=%s&countries_tags=%s&fields=%s&sort_by=nutriscore_score&page_size=%d",
                SEARCH_URL,
                categoryTag,
                normalizedCountry,
                FIELDS,
                SUGGESTION_PAGE_SIZE
        );

        try {
            ResponseEntity<OpenFoodFactsSearchResponse> response =
                    restTemplate.getForEntity(url, OpenFoodFactsSearchResponse.class);

            if (response.getBody() == null || response.getBody().products() == null) {
                return List.of();
            }

            return response.getBody().products();

        } catch (HttpServerErrorException.ServiceUnavailable e) {
            log.warn("OpenFoodFacts search unavailable for category {}: {}", categoryTag, e.getMessage());
            return List.of();
        } catch (HttpClientErrorException.TooManyRequests e) {
            log.warn("OpenFoodFacts rate limit hit during search for category {}", categoryTag);
            return List.of();
        } catch (Exception e) {
            log.error("Unexpected error during OpenFoodFacts search for category {}: {}", categoryTag, e.getMessage());
            return List.of();
        }
    }
}
