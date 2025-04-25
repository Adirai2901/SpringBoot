package com.bajaj.webhookproject;

import com.bajaj.webhookproject.model.GenerateWebhookRequest;
import com.bajaj.webhookproject.model.GenerateWebhookResponse;
import com.bajaj.webhookproject.model.User;
import org.springframework.boot.CommandLineRunner;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Component
public class WebhookInitializer implements CommandLineRunner {

    @Override
    public void run(String... args) {
        RestTemplate restTemplate = new RestTemplate();

        // Step 1: Generate Webhook
        String initUrl = "https://bfhldevapigw.healthrx.co.in/hiring/generateWebhook";

        GenerateWebhookRequest requestBody = new GenerateWebhookRequest(
                "Aditya Rai",
                "RA2211003020496",
                "ar9427@srmist.edu.in"
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<GenerateWebhookRequest> entity = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<GenerateWebhookResponse> response = restTemplate.postForEntity(
                    initUrl, entity, GenerateWebhookResponse.class
            );

            GenerateWebhookResponse webhookResponse = response.getBody();
            if (webhookResponse == null) {
                System.out.println("Response is null!");
                return;
            }

            String webhookUrl = webhookResponse.getWebhook();
            String token = webhookResponse.getAccessToken();
            List<com.bajaj.webhookproject.model.User> users = webhookResponse.getData().getUsers();


            // Step 2: Process mutual followers
            List<List<Integer>> result = findMutualFollowers(users);

            // Step 3: Send result to webhook
            Map<String, Object> finalResult = new HashMap<>();
            finalResult.put("regNo", "REG496");
            finalResult.put("outcome", result);

            HttpHeaders authHeaders = new HttpHeaders();
            authHeaders.setContentType(MediaType.APPLICATION_JSON);
            authHeaders.set("Authorization", token);

            HttpEntity<Map<String, Object>> postEntity = new HttpEntity<>(finalResult, authHeaders);

            int retries = 0;
            boolean success = false;

            while (!success && retries < 4) {
                try {
                    ResponseEntity<String> postResponse = restTemplate.postForEntity(webhookUrl, postEntity, String.class);
                    System.out.println("Webhook response: " + postResponse.getBody());
                    success = true;
                } catch (Exception e) {
                    retries++;
                    System.out.println("Retrying... attempt " + retries);
                }
            }

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private List<List<Integer>> findMutualFollowers(List<User> users) {
        Map<Integer, Set<Integer>> followMap = new HashMap<>();
        for (User user : users) {
            followMap.put(user.getId(), new HashSet<>(user.getFollows()));
        }

        Set<String> visited = new HashSet<>();
        List<List<Integer>> mutuals = new ArrayList<>();

        for (User user : users) {
            int userId = user.getId();
            for (int followedId : user.getFollows()) {
                if (followMap.containsKey(followedId) &&
                        followMap.get(followedId).contains(userId)) {
                    int min = Math.min(userId, followedId);
                    int max = Math.max(userId, followedId);
                    String key = min + "-" + max;
                    if (!visited.contains(key)) {
                        visited.add(key);
                        mutuals.add(Arrays.asList(min, max));
                    }
                }
            }
        }

        return mutuals;
    }
}
