package mobile.jira.clonejira.repository;

import mobile.jira.clonejira.entity.Task;
import mobile.jira.clonejira.entity.key.ProjectTaskId;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskRepository extends JpaRepository<Task, ProjectTaskId> {
    // JpaRepository đã có sẵn các hàm: save, findAll, findById, delete...
}