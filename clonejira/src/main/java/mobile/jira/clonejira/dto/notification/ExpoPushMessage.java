package mobile.jira.clonejira.dto.notification;

import lombok.Data;

import java.util.Map;

@Data
public class ExpoPushMessage {
    private String to; // Expo Push Token (ví dụ: ExponentPushToken[xxx])
    private String title;
    private String body;
    private Map<String, Object> data; // Dữ liệu tùy chỉnh gửi kèm
    // Getters and Setters
}