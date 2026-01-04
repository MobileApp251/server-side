package mobile.jira.clonejira.repository;

import mobile.jira.clonejira.entity.Notifications;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface NotificationRepository extends JpaRepository<Notifications, UUID> {
    List<?> findByUser_Uid(UUID userUid, Sort sort);
}
