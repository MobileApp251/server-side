package mobile.jira.clonejira.repository;

import mobile.jira.clonejira.entity.Task;
import mobile.jira.clonejira.entity.key.ProjectTaskId;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskRepository extends JpaRepository<Task, ProjectTaskId> {
    // JpaRepository đã có sẵn các hàm: save, findAll, findById, delete...

    @Query("SELECT COALESCE(MAX(t.id.task_id), 0) FROM Task t WHERE t.id.proj_id = :project_id")
    Integer getMaxTaskIdx(@Param("project_id") String project_id);
}