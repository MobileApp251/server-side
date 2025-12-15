package mobile.jira.clonejira.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import mobile.jira.clonejira.entity.Project;

public interface ProjectRepository extends JpaRepository<Project, UUID> {
    @Query(
        "SELECT p.project FROM Participate p " +
        "JOIN p.project " +
        "WHERE p.member.uid = :uid"
    )
    List<Project> findAllMyProjects(@Param("uid") UUID uid);
}
