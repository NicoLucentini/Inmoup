package org.example.services;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class ScheduledRequestService {
    private final RestTemplate restTemplate = new RestTemplate();
    private static final String TARGET_URL = "https://inmoup.onrender.com/heap";

    // Run every 10 minutes (600000 ms)
    //@Scheduled(fixedRate = 600000)
    public void fetchData() {
        try {
            System.out.println("Request for: " + TARGET_URL);
            String response = restTemplate.getForObject(TARGET_URL, String.class);
            System.out.println("Response received: " + response);
        } catch (Exception e) {
            System.err.println("Error making request: " + e.getMessage());
        }
    }
}
