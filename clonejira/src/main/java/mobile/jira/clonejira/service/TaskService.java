package mobile.jira.clonejira.service;

import java.util.List;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import mobile.jira.clonejira.entity.Assign;
import mobile.jira.clonejira.entity.Task;
import mobile.jira.clonejira.entity.key.ProjectTaskId;
import mobile.jira.clonejira.entity.key.TaskAssigneeId;
import mobile.jira.clonejira.repository.AssignRepository;
import mobile.jira.clonejira.repository.TaskRepository;
import mobile.jira.clonejira.mapper.*;
import mobile.jira.clonejira.dto.TaskCreateDTO;
import mobile.jira.clonejira.dto.TaskDTO;

@Service
@RequiredArgsConstructor
public class TaskService {
    private final TaskRepository taskRepository;
    private final AssignRepository assignRepository;
    private final TaskMapper taskMapper;

    public TaskDTO createTask(String project_id, TaskCreateDTO taskDTO) {
        TaskDTO tdto = new TaskDTO();
        Integer task_id = taskRepository.getMaxTaskIdx(project_id);
        tdto.setProj_id(project_id);
        tdto.setTask_id(task_id + 1);
        tdto.setContent(taskDTO.getContent());
        tdto.setTask_name(taskDTO.getTask_name());
        tdto.setStatus(taskDTO.getStatus());
        tdto.setStartAt(taskDTO.getStartAt());
        tdto.setEndAt(taskDTO.getEndAt());
        Task task = taskMapper.toEntity(tdto);
        Task savedTask = taskRepository.save(task);
        return taskMapper.toDTO(savedTask);
    }

    public void assignTask(String uid, String proj_id, Integer task_id) {
        TaskAssigneeId id = new TaskAssigneeId(uid, proj_id, task_id);
        Assign newAssignment = new Assign(id);

        assignRepository.save(newAssignment);
    }

    public List<TaskDTO> getAllTasks(){
        List<Task> tasks = taskRepository.findAll();

        return tasks.stream()
                .map(taskMapper::toDTO)
                .toList();
    }

    public List<TaskDTO> getTasksByProject(String project_id){
        List<Task> tasks = taskRepository.findTaskByProjId(project_id);

        return tasks.stream()
        .map(taskMapper::toDTO)
        .toList();
    }

    public TaskDTO getTaskById(String project_id, Integer task_id){
        ProjectTaskId id = new ProjectTaskId(project_id, task_id);
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found"));
        return taskMapper.toDTO(task);
    }
}
