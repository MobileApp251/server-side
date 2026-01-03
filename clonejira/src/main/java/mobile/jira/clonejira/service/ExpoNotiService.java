package mobile.jira.clonejira.service;

import lombok.RequiredArgsConstructor;
import mobile.jira.clonejira.entity.Notifications;
import mobile.jira.clonejira.entity.Project;
import mobile.jira.clonejira.entity.Task;
import mobile.jira.clonejira.entity.User;
import mobile.jira.clonejira.entity.key.ProjectTaskId;
import mobile.jira.clonejira.enums.NotifyType;
import mobile.jira.clonejira.repository.NotificationRepository;
import mobile.jira.clonejira.repository.ProjectRepository;
import mobile.jira.clonejira.repository.TaskRepository;
import mobile.jira.clonejira.repository.UserRepository;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ExpoNotiService {
    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;
    private final TaskRepository taskRepository;

    public void sendPushNotification(String expoToken, String title, String message, NotifyType notifyType, String uid, String project_id, Integer task_id) {
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
            String EXPO_PUSH_URL = "https://exp.host/--/api/v2/push/send";
            ResponseEntity<String> response = restTemplate.postForEntity(EXPO_PUSH_URL, entity, String.class);
            Instant now = Instant.now();
            Optional<User> user = userRepository.findById(UUID.fromString(uid));
            Optional<Project> project = projectRepository.findById(UUID.fromString(project_id));
            Optional<Task> task = taskRepository.findById(new ProjectTaskId(project_id, task_id));
            Notifications  notifications = Notifications.builder()
                    .content(message).title(title).createdAt(now).notifyAt(now).notifyType(notifyType)
                    .user(user.orElse(null)).task(task.orElse(null)).project(project.orElse(null)).build();

            notificationRepository.save(notifications);
            System.out.println("Response: " + response.getBody());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
