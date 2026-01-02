package mobile.jira.clonejira.repository;

import mobile.jira.clonejira.entity.User;
import mobile.jira.clonejira.enums.ProjectRole;
import org.springframework.data.jpa.repository.JpaRepository;

import mobile.jira.clonejira.entity.Participate;
import mobile.jira.clonejira.entity.key.ProjectMemberId;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface ParticipateRepository extends JpaRepository<Participate, ProjectMemberId> {
    @Query(
            "select p.member from Participate p where p.member.uid = :uid and p.role = :role"
    )
    Optional<User> checkLeader(@Param("uid") UUID uid, @Param("role") ProjectRole role);
}
