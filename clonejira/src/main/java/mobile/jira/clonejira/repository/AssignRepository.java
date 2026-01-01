package mobile.jira.clonejira.repository;

import mobile.jira.clonejira.entity.Task;
import mobile.jira.clonejira.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import mobile.jira.clonejira.entity.Assign;
import mobile.jira.clonejira.entity.key.TaskAssigneeId;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface AssignRepository extends JpaRepository<Assign, TaskAssigneeId> {

    @Query("select a.assignee from Assign a where a.task = :task")
    Optional<List<User>> getAssigneesByTask(Task task);
}
