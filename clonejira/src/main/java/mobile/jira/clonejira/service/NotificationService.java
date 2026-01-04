package mobile.jira.clonejira.service;

import lombok.RequiredArgsConstructor;
import mobile.jira.clonejira.dto.notification.NotificationDTO;
import mobile.jira.clonejira.entity.Notifications;
import mobile.jira.clonejira.mapper.NotificationMapper;
import mobile.jira.clonejira.repository.NotificationRepository;
import org.springframework.data.domain.Sort;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class NotificationService {
    private final SimpMessagingTemplate simpMessagingTemplate;

    private final NotificationRepository notificationRepository;

    private final NotificationMapper notificationMapper;

    public void sendNotification(String uid, String message) {
        simpMessagingTemplate.convertAndSendToUser(uid, "/queue/notification", message);

        System.out.println("Notification sent to " + uid + ": " + message);
    }

    public List<NotificationDTO> getMyNotifications(String uid) {
        Sort sort = Sort.by("createAt").descending();
        List<Notifications> notis = (List<Notifications>) notificationRepository.findByUser_Uid(UUID.fromString(uid), sort);

        return notis.stream().map(notificationMapper::toDTO).toList();
    }
}
