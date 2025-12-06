package mobile.jira.clonejira.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;
import mobile.jira.clonejira.service.TaskService;
import mobile.jira.clonejira.dto.TaskCreateDTO;
import mobile.jira.clonejira.dto.TaskDTO;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
public class TaskController {
    private final TaskService taskService;
    

    @PostMapping("/{project_id}")
    public ResponseEntity<TaskDTO> createTask(
        @PathVariable("project_id") String project_id,
        @RequestBody TaskCreateDTO taskDTO
    ){
        return ResponseEntity.ok(taskService.createTask(project_id, taskDTO));
    }
}
