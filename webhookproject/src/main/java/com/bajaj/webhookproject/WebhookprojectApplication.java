package com.bajaj.webhookproject;

import com.bajaj.webhookproject.model.GenerateWebhookResponse;
import com.bajaj.webhookproject.model.User;
import com.bajaj.webhookproject.service.FollowerService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SpringBootApplication
public class WebhookprojectApplication implements CommandLineRunner {

	@Autowired
	private FollowerService followerService;

	private final RestTemplate restTemplate = new RestTemplate();

	public static void main(String[] args) {
		SpringApplication.run(WebhookprojectApplication.class, args);
	}

	@Override
	public void run(String... args) {
		// Step 1: Call generateWebhook
		String generateUrl = "https://bfhldevapigw.healthrx.co.in/hiring/generateWebhook";

		Map<String, String> requestBody = new HashMap<>();
		requestBody.put("name", "John Doe");
		requestBody.put("regNo", "REG00496");
		requestBody.put("email", "john@example.com");

		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			HttpEntity<Map<String, String>> entity = new HttpEntity<>(requestBody, headers);

			ResponseEntity<GenerateWebhookResponse> response = restTemplate.postForEntity(
					generateUrl, entity, GenerateWebhookResponse.class);

			if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
				GenerateWebhookResponse webhookData = response.getBody();

				// Check if 'data' and 'users' are not null
				if (webhookData != null && webhookData.getData() != null && webhookData.getData().getUsers() != null) {
					String webhookUrl = webhookData.getWebhook();
					String accessToken = webhookData.getAccessToken();
					List<User> users = webhookData.getData().getUsers();

					// Get the findId and n (assuming you need these values)
					int findId = webhookData.getData().getFindId();
					int n = webhookData.getData().getN();

					// Get the outcome from the follower service
					if (users.isEmpty()) {
						System.out.println("Error: Users list is empty.");
					} else {
						List<Integer> outcome = followerService.getNthLevelFollowers(findId, n, users);

						// Build the result JSON to send to the webhook
						Map<String, Object> result = new HashMap<>();
						result.put("regNo", "REG00496");
						result.put("outcome", outcome);

						// Send the result to the webhook with retry logic
						sendToWebhook(webhookUrl, accessToken, result);
					}
				} else {
					System.out.println("Error: Data or users are null in the response.");
				}
			} else {
				System.out.println("Error: Failed to generate webhook. Status: " + response.getStatusCode());
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void sendToWebhook(String url, String token, Map<String, Object> payload) {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.set("Authorization", token);

		HttpEntity<Map<String, Object>> entity = new HttpEntity<>(payload, headers);

		int attempts = 0;
		while (attempts < 4) {
			try {
				ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);
				if (response.getStatusCode().is2xxSuccessful()) {
					System.out.println("Webhook response: " + response.getBody());
					break;
				} else {
					System.out.println("Retrying... attempt " + (attempts + 1));
				}
			} catch (Exception e) {
				System.out.println("Retrying... attempt " + (attempts + 1));
			}
			attempts++;
		}
	}
}
