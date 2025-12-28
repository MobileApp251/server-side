package mobile.jira.clonejira.service;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationService {
    private final SimpMessagingTemplate simpMessagingTemplate;

    public void sendNotification(String uid, String message) {
        simpMessagingTemplate.convertAndSendToUser(uid, "/queue/notification", message);

        System.out.println("Notification sent to " + uid + ": " + message);
    }
}
