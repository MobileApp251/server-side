package mobile.jira.clonejira.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import mobile.jira.clonejira.entity.User;
import org.springframework.data.jpa.repository.Query;

public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByEmail(String email);

    @Query(
            "select email from User where email like :pattern"
    )
    Optional<List<String>> getEmailList(String pattern);
}
