package mobile.jira.clonejira.service;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class ExpoNotiService {
    private final String EXPO_PUSH_URL = "https://exp.host/--/api/v2/push/send";

    public void sendPushNotification(String expoToken, String title, String message) {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        // Nếu bạn cấu hình Access Token trong Expo Dashboard (khuyên dùng):
        // headers.setBearerAuth("YOUR_EXPO_ACCESS_TOKEN");

        Map<String, Object> payload = new HashMap<>();
        payload.put("to", expoToken);
        payload.put("title", title);
        payload.put("body", message);
        payload.put("sound", "default");

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(payload, headers);

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(EXPO_PUSH_URL, entity, String.class);
            System.out.println("Response: " + response.getBody());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
