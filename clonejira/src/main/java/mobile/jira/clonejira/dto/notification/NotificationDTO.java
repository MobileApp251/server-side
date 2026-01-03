package mobile.jira.clonejira.dto.notification;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import lombok.Data;
import mobile.jira.clonejira.enums.NotifyType;
import mobile.jira.clonejira.enums.converter.NotifyTypeConverter;
import org.springframework.data.annotation.CreatedDate;

import java.time.Instant;
import java.util.UUID;

@Data
public class NotificationDTO {
    private String noti_id;

    private String title;

    private String content;

    private Instant createdAt;

    private Instant notifyAt;

    private NotifyType notifyType;
}
