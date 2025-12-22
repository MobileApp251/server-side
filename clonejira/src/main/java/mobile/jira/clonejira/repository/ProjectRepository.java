package mobile.jira.clonejira.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import mobile.jira.clonejira.entity.Project;

public interface ProjectRepository extends JpaRepository<Project, UUID> {
    @Query(
        "SELECT prj FROM Project prj " +
        "JOIN Participate p ON p.project = prj" +
        " WHERE p.member.uid = :uid"
    )
    Page<Project> findAllMyProjects(@Param("uid") UUID uid, Pageable pageable);
}
