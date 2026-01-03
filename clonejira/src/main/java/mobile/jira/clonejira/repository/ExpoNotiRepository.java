package mobile.jira.clonejira.repository;

import mobile.jira.clonejira.entity.ExpoNotiCode;
import mobile.jira.clonejira.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ExpoNotiRepository extends JpaRepository<ExpoNotiCode, String> {
    List<ExpoNotiCode> findByUser(User user);
}
