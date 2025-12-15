package mobile.jira.clonejira.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import mobile.jira.clonejira.entity.Participate;
import mobile.jira.clonejira.entity.key.ProjectMemberId;

public interface ParticipateRepository extends JpaRepository<Participate, ProjectMemberId> {
    
}
