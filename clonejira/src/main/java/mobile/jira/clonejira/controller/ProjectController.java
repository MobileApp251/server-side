package mobile.jira.clonejira.controller;

import java.util.List;

import mobile.jira.clonejira.dto.*;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
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

    @PostMapping("/join/{uid}")
    public ResponseEntity<?> joinProject(
            @PathVariable("uid") String uid,
            @RequestBody ProjectJoinDTO project
    ) {
        try {

            projectService.joinProject(uid, project.getProj_id(), project.getRole());

            return ResponseEntity.ok("Join Project successfully!");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Internal Error!");
        }
    }

    @GetMapping()
    public ResponseEntity<?> getAllMyProjects (
        @AuthenticationPrincipal UserDetails userDetails,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size,
        @RequestParam(defaultValue = "startAt") String sortBy,
        @RequestParam(defaultValue = "desc") String sortDir
    ) {
        try {
            String uid = userDetails.getUsername();

            System.out.print("User with id: ");
            System.out.println(uid);
            List<ProjectDTO> projects = projectService.getAllMyProjects(uid, page, size, sortBy, sortDir);
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
            ProjectGetDTO project = projectService.getProjectById(project_id);

            return ResponseEntity.ok(project);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(e.getMessage());
        }       
    }

    @PatchMapping("/{project_id}")
    public ResponseEntity<?> updateProjectById(
        @PathVariable("project_id") String project_id,
        @RequestBody ProjectUpdateDTO dto
    ){
        try {
            return ResponseEntity.ok(projectService.updateProject(project_id, dto));
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Internal Error!");
        }
    }

    @DeleteMapping("/{project_id}")
    public ResponseEntity<?> deleteProjectById(
        @PathVariable("project_id") String project_id
    ){
        try {
            projectService.deleteProject(project_id);
            return ResponseEntity.ok("Delete Project successfully!");
        } catch (Exception e) {
            return ResponseEntity.status(500).body(e.getMessage());
        }
    }
}
