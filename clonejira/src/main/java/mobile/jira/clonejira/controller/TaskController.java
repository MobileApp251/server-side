package mobile.jira.clonejira.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import mobile.jira.clonejira.service.TaskService;
import mobile.jira.clonejira.dto.TaskCreateDTO;
import mobile.jira.clonejira.dto.TaskDTO;
import mobile.jira.clonejira.security.JwtTokenProvider;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
public class TaskController {
    private final TaskService taskService;
    private final JwtTokenProvider jwtTokenProvider;

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

    @PostMapping("/{project_id}/{task_id}")
    public ResponseEntity<?> assignTask(
        HttpServletRequest request,
        @PathVariable("project_id") String project_id,
        @PathVariable("task_id") Integer task_id
    ) {
        try {
            String authHeader = request.getHeader("Authorization");

            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(401).body("Invalid Token");
            }
            String token = authHeader.substring(7);
            String uid = jwtTokenProvider.getUid(token);

            taskService.assignTask(uid, project_id, task_id);
            
            return ResponseEntity.ok("Assign Task Successfully!");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Internal Error!");
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
    public ResponseEntity<List<TaskDTO>> getTasksByProject(
        @PathVariable("project_id") String project_id
    ){
        return ResponseEntity.ok(taskService.getTasksByProject(project_id));
    }
}
