package mobile.jira.clonejira.controller;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import mobile.jira.clonejira.dto.TaskUpdateDTO;
import mobile.jira.clonejira.entity.Project;
import mobile.jira.clonejira.repository.ProjectRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import mobile.jira.clonejira.service.TaskService;
import mobile.jira.clonejira.dto.TaskCreateDTO;
import mobile.jira.clonejira.dto.TaskDTO;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class TaskController {
    private final TaskService taskService;
    private final ProjectRepository projectRepository;

    @PostMapping("/{project_id}")
    public ResponseEntity<?> createTask(
        @PathVariable("project_id") String project_id,
        @RequestBody TaskCreateDTO taskDTO
    ){
        try {
            return ResponseEntity.ok(taskService.createTask(project_id, taskDTO));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(e.getMessage());
        }
    }

    @PostMapping("/{uid}/{project_id}/{task_id}")
    public ResponseEntity<?> assignTask(
        @PathVariable("uid") String uid,
        @PathVariable("project_id") String project_id,
        @PathVariable("task_id") Integer task_id
    ) {
        System.out.println(uid);
        System.out.println(project_id);
        System.out.println(task_id);
        try {
            Optional<Project> checkProject = projectRepository.findProjectByUid(UUID.fromString(uid), UUID.fromString(project_id));
            System.out.println("------------ Check Point -------------");
            System.out.println(checkProject);
            if (checkProject.isEmpty()) {
                return ResponseEntity.status(400).body("User is not present in project!");
            }
            taskService.assignTask(uid, project_id, task_id);
            
            return ResponseEntity.ok("Assign Task Successfully!");
        } catch (Exception e) {
            return ResponseEntity.status(500).body(e.getMessage());
        }
    }

    @GetMapping()
    public ResponseEntity<?> getAllTasks(){
        try {
            return ResponseEntity.ok(taskService.getAllTasks());
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return ResponseEntity.status(500).body("Internal Error!");
        }
    }

    @GetMapping("/{project_id}")
    public ResponseEntity<?> getTasksByProject(
        @PathVariable("project_id") String project_id
    ){
        try {
            return ResponseEntity.ok(taskService.getTasksByProject(project_id));
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Internal Error");
        }
    }

    @GetMapping("/assignee/{project_id}/{uid}")
    public ResponseEntity<?> getTasksByProjectNUser(
            @PathVariable("project_id") String project_id,
            @PathVariable("uid") String uid
    ){
        try {
            return ResponseEntity.ok(taskService.getAllTasksByAssignee(project_id, uid));
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Internal Error");
        }
    }

    @GetMapping("/{project_id}/{task_id}")
    public ResponseEntity<?> getTaskByProject(
        @AuthenticationPrincipal UserDetails user,
        @PathVariable("project_id") String project_id,
        @PathVariable("task_id") Integer task_id
    ){
        try {
            String uid = user.getUsername();
            Optional<Project> checkProject =  projectRepository.findProjectByUid(UUID.fromString(uid), UUID.fromString(project_id));
            if (checkProject.isEmpty()) {
                return ResponseEntity.status(400).body("User is not present in project!");
            }
            TaskDTO taskDTO = taskService.getTaskById(project_id, task_id);

            return ResponseEntity.ok(taskDTO);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Internal Error!");
        }
    }

    @PatchMapping("/{project_id}/{task_id}")
    public ResponseEntity<?> updateTask(
        @AuthenticationPrincipal UserDetails user,
        @PathVariable("project_id") String project_id,
        @PathVariable("task_id") Integer task_id,
        @RequestBody TaskUpdateDTO taskUpdateDTO
    ){
        try {
            String uid = user.getUsername();
            Optional<Project> checkProject =  projectRepository.findProjectByUid(UUID.fromString(uid), UUID.fromString(project_id));
            if (checkProject.isEmpty()) {
                return ResponseEntity.status(400).body("User is not present in project!");
            }
            TaskDTO taskUpdate =  taskService.updateTask(project_id, task_id, taskUpdateDTO);
            return ResponseEntity.ok(taskUpdate);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Internal Error!");
        }
    }

    @DeleteMapping("/{project_id}/{task_id}")
    public ResponseEntity<?> deleteTask(
        @PathVariable("project_id") String project_id,
        @PathVariable("task_id") Integer task_id
    ){
        try {
            taskService.deleteTask(project_id, task_id);
            return ResponseEntity.ok("Delete Task Successfully!");
        } catch (Exception e) {
            return ResponseEntity.status(500).body(e.getMessage());
        }
    }
}
