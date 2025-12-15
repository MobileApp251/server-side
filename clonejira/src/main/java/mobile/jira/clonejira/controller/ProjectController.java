package mobile.jira.clonejira.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import mobile.jira.clonejira.dto.ProjectCreateDTO;
import mobile.jira.clonejira.dto.ProjectDTO;
import mobile.jira.clonejira.dto.ProjectJoinDTO;
import mobile.jira.clonejira.security.JwtTokenProvider;
import mobile.jira.clonejira.service.ProjectService;

@RestController
@RequestMapping("/projects")
@RequiredArgsConstructor
public class ProjectController {
    private final ProjectService projectService;
    private final JwtTokenProvider jwtTokenProvider;

    @PostMapping()
    public ResponseEntity<?> createNewProject(
        HttpServletRequest request,
        @RequestBody ProjectCreateDTO projectDTO
    ){
        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(401).body("Invalid Token");
        }
        String token = authHeader.substring(7);
        String uid = jwtTokenProvider.getUid(token);

        return ResponseEntity.ok(projectService.createProject(uid, projectDTO));
    }

    @PostMapping("/join")
    public ResponseEntity<?> joinProject(
        HttpServletRequest request,
        @RequestBody ProjectJoinDTO project
    ) {
        try {
            String authHeader = request.getHeader("Authorization");

            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(401).body("Invalid Token");
            }
            String token = authHeader.substring(7);
            String uid = jwtTokenProvider.getUid(token);

            projectService.joinProject(uid, project.getProj_id(), project.getStatus());

            return ResponseEntity.ok("Join Project successfully!");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Internal Error!");
        }
    }

    @GetMapping()
    public ResponseEntity<?> getAllMyProjects (
        HttpServletRequest request
    ) {
        try {
            String authHeader = request.getHeader("Authorization");

            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(401).body("Invalid Token");
            }
            String token = authHeader.substring(7);
            String uid = jwtTokenProvider.getUid(token);

            List<ProjectDTO> projects = projectService.getAllMyProjects(uid);

            return ResponseEntity.ok(projects);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Internal Error!");
        }
    }

    @GetMapping(":project_id")
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
