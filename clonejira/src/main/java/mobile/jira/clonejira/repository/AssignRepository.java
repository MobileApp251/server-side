package mobile.jira.clonejira.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import mobile.jira.clonejira.entity.Assign;
import mobile.jira.clonejira.entity.key.TaskAssigneeId;

public interface AssignRepository extends JpaRepository<Assign, TaskAssigneeId> {
    
}
