package mobile.jira.clonejira.repository;

import mobile.jira.clonejira.entity.Notifications;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface NotificationRepository extends JpaRepository<Notifications, UUID> {
}
