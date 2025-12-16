package mobile.jira.clonejira.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
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
        HttpServletRequest request,
        @PathVariable("uid") String uid,
        @PathVariable("project_id") String project_id,
        @PathVariable("task_id") Integer task_id
    ) {
        System.out.println(uid);
        System.out.println(project_id);
        System.out.println(task_id);
        // try {
            String authHeader = request.getHeader("Authorization");

            System.out.println(authHeader);
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(401).body("Invalid Token");
            }

            System.out.println("Finish Auth");
            taskService.assignTask(uid, project_id, task_id);
            
            return ResponseEntity.ok("Assign Task Successfully!");
        // } catch (Exception e) {
        //     return ResponseEntity.status(500).body("Internal Error!");
        // }
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
