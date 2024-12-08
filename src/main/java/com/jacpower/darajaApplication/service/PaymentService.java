package com.jacpower.darajaApplication.service;

import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@Service
public class PaymentService {
    @Value("${daraja.consumerKey}")
    private String consumerKey;

    @Value("${daraja.consumerSecret}")
    private String consumerSecret;

    public String getAccessToken() {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth(consumerKey, consumerSecret);
        HttpEntity<String> request = new HttpEntity<>(headers);
        ResponseEntity<Map> response = restTemplate.exchange("https://api.safaricom.co.ke/oauth/v1/generate?grant_type=client_credentials", HttpMethod.GET, request, Map.class);
        return response.getBody().get("access_token").toString();
    }

    public String initiateStkPush(String phoneNumber, double amount) {
        String accessToken = getAccessToken();
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        headers.setContentType(MediaType.APPLICATION_JSON);
        Map<String, Object> body = new HashMap<>();
        body.put("BusinessShortCode", "4444222");
        body.put("Password", generatePassword());
        body.put("Timestamp", generateTimestamp());
        body.put("TransactionType", "CustomerPayBillOnline");
        body.put("Amount", amount);
        body.put("PartyA", phoneNumber);
        body.put("PartyB", "4444222");
        body.put("PhoneNumber", phoneNumber);
        body.put("CallBackURL", "https://webhook.site/8e1e949a-9ee8-4e21-b146-3b4f73afde05");
        body.put("AccountReference", "Order1234");
        body.put("TransactionDesc", "Payment for Order");

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
        ResponseEntity<String> response = restTemplate.postForEntity("https://api.safaricom.co.ke/mpesa/stkpush/v1/processrequest", request, String.class);

        return response.getBody();
    }

    private String generatePassword() {
        String passkey = "2f56c72bbf5c15babfb41711ecd825bf41838e464e8a6605d780b1cecf9929bc";
        String shortCode = "4444222";
        String credentials = shortCode + passkey + generateTimestamp();
        return Base64.encodeBase64String(credentials.getBytes());
    }

    private String generateTimestamp() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
    }
}
