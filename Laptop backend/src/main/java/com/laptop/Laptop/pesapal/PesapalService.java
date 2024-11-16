package com.laptop.Laptop.pesapal;

import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;


import java.util.HashMap;
import java.util.Map;

@Service

public class PesapalService {

    private final RestTemplate restTemplate;
    private final String pesapalBaseUrl = "https://cybqa.pesapal.com/pesapalv3/api"; // Sandbox URL
    private final String secret = "lBxvF2DCfYMbZXUp3Xspn30T54hKQ1nI"; // Consumer Key
    private final String consumer_secret = "miOco3mlCEZbavNv6MLncbm9OVc="; // Consumer Secret

    private final String callbackUrl = "https://yourdomain.com/api/callback"; // Replace with actual URL

    public PesapalService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public String fetchAccessToken() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.setBasicAuth(secret, consumer_secret); // Automatically Base64 encodes the credentials

        HttpEntity<String> request = new HttpEntity<>(null, headers);

        String tokenUrl = pesapalBaseUrl + "/Auth/RequestToken"; // Token endpoint

        ResponseEntity<Map> response = restTemplate.exchange(
                tokenUrl,
                HttpMethod.POST,
                request,
                Map.class
        );

        if (response.getStatusCode() == HttpStatus.OK) {
            Map<String, Object> responseBody = response.getBody();
            if (responseBody != null) {
                System.out.println("Access Token: " + fetchAccessToken());
                System.out.println(responseBody);
                return (String) responseBody.get("token"); // Ensure the key matches the API response
            }
        }

        throw new RuntimeException("Failed to fetch access token: " + response.getBody());
    }



    public String generatePaymentUrl(String merchantReference, double amount, String description) {
        String accessToken = fetchAccessToken();
        System.out.println(accessToken);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(accessToken);

        Map<String, Object> payload = new HashMap<>();
        payload.put("id", merchantReference);
        payload.put("currency", "KES");
        payload.put("amount", amount);
        payload.put("description", description);
        payload.put("callback_url", callbackUrl);
        payload.put("notification_id", "YOUR_NOTIFICATION_ID");

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(payload, headers);

        ResponseEntity<Map> response = restTemplate.postForEntity(
                pesapalBaseUrl + "/Transactions/SubmitOrderRequest",
                request,
                Map.class
        );

        if (response.getStatusCode() == HttpStatus.OK) {
            Map<String, Object> responseBody = response.getBody();
            return (String) responseBody.get("redirect_url");
        }

        throw new RuntimeException("Failed to generate Pesapal payment URL");
    }
}
