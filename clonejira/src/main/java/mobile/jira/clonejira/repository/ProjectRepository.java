package mobile.jira.clonejira.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import mobile.jira.clonejira.entity.ProjectMember;
import mobile.jira.clonejira.entity.User;
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

    @Query("SELECT p, u.member, u.role FROM Project p JOIN p.members u WHERE p IN :projects")
    List<Object[]> findAllMembersByProjects(@Param("projects") List<Project> projects);

    @Query(
            "select new ProjectMember(p.member.uid, p.member.email, p.member.username, p.role) from Participate p where p.project.proj_id = :proj_id"
    )
    List<ProjectMember> findMembersOfProject(@Param("proj_id") UUID proj_id);

    @Query(
            "SELECT prj.proj_id FROM Project prj " +
            "join Participate p ON p.project = prj "+
            "where p.member.uid = :uid and prj.proj_id = :proj_id"
    )
    Optional<Project> findProjectByUid(@Param("uid") UUID uid, @Param("proj_id") UUID proj_id);
}
