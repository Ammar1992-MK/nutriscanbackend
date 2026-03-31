package com.nutriscan.mpv.configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.List;

@Configuration
public class RestTemplateConfig {

    private static final int MAX_RETRIES = 3;
    private static final long RETRY_DELAY_MS = 2000;

    @Bean
    public RestTemplate restTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setInterceptors(List.of(new UserAgentInterceptor(), new RetryInterceptor()));
        return restTemplate;
    }

    private static class UserAgentInterceptor implements ClientHttpRequestInterceptor {
        @Override
        public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
            request.getHeaders().set("User-Agent", "NutriScan - iOS - Version 1.0 - ammarkhalil08@gmail.com");
            return execution.execute(request, body);
        }
    }

    private static class RetryInterceptor implements ClientHttpRequestInterceptor {
        @Override
        public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
            ClientHttpResponse response = execution.execute(request, body);
            int attempts = 0;

            while (shouldRetry(response) && attempts < MAX_RETRIES) {
                attempts++;
                try {
                    Thread.sleep(RETRY_DELAY_MS * attempts); // 2s, 4s, 6s
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
                response = execution.execute(request, body);
            }

            return response;
        }

        private boolean shouldRetry(ClientHttpResponse response) throws IOException {
            return response.getStatusCode() == HttpStatus.TOO_MANY_REQUESTS
                    || response.getStatusCode() == HttpStatus.SERVICE_UNAVAILABLE;
        }
    }
}

