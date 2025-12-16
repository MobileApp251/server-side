package mobile.jira.clonejira.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import mobile.jira.clonejira.dto.ProjectCreateDTO;
import mobile.jira.clonejira.dto.ProjectDTO;
import mobile.jira.clonejira.dto.ProjectJoinDTO;
import mobile.jira.clonejira.service.ProjectService;

@RestController
@RequestMapping("/projects")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class ProjectController {
    private final ProjectService projectService;

    @PostMapping()
    public ResponseEntity<?> createNewProject(
        @AuthenticationPrincipal UserDetails userDetails,
        @RequestBody ProjectCreateDTO projectDTO
    ){
        String uid = userDetails.getUsername();

        try {
            return ResponseEntity.ok(projectService.createProject(uid, projectDTO));
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Internal Error!");
        }
    }

    @PostMapping("/join")
    public ResponseEntity<?> joinProject(
        @AuthenticationPrincipal UserDetails userDetails,
        @RequestBody ProjectJoinDTO project
    ) {
        try {
            String uid = userDetails.getUsername();

            projectService.joinProject(uid, project.getProj_id(), project.getStatus());

            return ResponseEntity.ok("Join Project successfully!");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Internal Error!");
        }
    }

    @GetMapping()
    public ResponseEntity<?> getAllMyProjects (
        @AuthenticationPrincipal UserDetails userDetails
    ) {
        try {
            String uid = userDetails.getUsername();

            System.out.print("User with id: ");
            System.out.println(uid);

            List<ProjectDTO> projects = projectService.getAllMyProjects(uid);
            System.out.print("Project Fetch: ");
            System.out.println(projects.size());
            return ResponseEntity.ok(projects);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return ResponseEntity.status(500).body("Internal Error!");
        }
    }

    @GetMapping("/{project_id}")
    public ResponseEntity<?> getProjectById(
        @PathVariable("project_id") String project_id
    ) {
        try {
            ProjectDTO project = projectService.getProjectById(project_id);

            return ResponseEntity.ok(project);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Internal Error!");
        }       
    }
}
