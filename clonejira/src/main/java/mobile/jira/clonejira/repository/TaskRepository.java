package mobile.jira.clonejira.repository;

import mobile.jira.clonejira.entity.Assign;
import mobile.jira.clonejira.entity.Task;
import mobile.jira.clonejira.entity.key.ProjectTaskId;

import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskRepository extends JpaRepository<Task, ProjectTaskId> {
    // JpaRepository đã có sẵn các hàm: save, findAll, findById, delete...

    @Query("SELECT COALESCE(MAX(t.id.task_id), 0) FROM Task t WHERE t.id.proj_id = :project_id")
    Integer getMaxTaskIdx(@Param("project_id") String project_id);

    @Query(
        "SELECT t, a.assignee FROM Task t " +
        "join Assign a on a.task.id = t.id " +
        "WHERE t.id.proj_id = :project_id"
    )
    List<Object[]> findTaskByProjId(@Param("project_id") String project_id);

    @Query(
        "select t from Task t " +
        "join Assign a on t.id = a.task.id " +
        "where t.project.proj_id = :project_id and a.assignee.uid = :uid"
    )
    List<Task> findAllByAssignee(@Param("project_id") UUID project_id, @Param("uid") UUID uid);
}