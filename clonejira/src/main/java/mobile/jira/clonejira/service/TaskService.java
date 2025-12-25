package mobile.jira.clonejira.service;

import java.util.*;
import java.util.stream.Collectors;

import mobile.jira.clonejira.dto.auth.UserDTO;
import mobile.jira.clonejira.dto.task.*;
import mobile.jira.clonejira.repository.ProjectRepository;
import org.apache.coyote.BadRequestException;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import mobile.jira.clonejira.entity.Assign;
import mobile.jira.clonejira.entity.*;
import mobile.jira.clonejira.entity.key.ProjectTaskId;
import mobile.jira.clonejira.entity.key.TaskAssigneeId;
import mobile.jira.clonejira.repository.AssignRepository;
import mobile.jira.clonejira.repository.TaskRepository;
import mobile.jira.clonejira.mapper.*;

@Service
@RequiredArgsConstructor
public class TaskService {
    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;
    private final AssignRepository assignRepository;
    private final TaskMapper taskMapper;
    private final UserMapper userMapper;

    public TaskDTO createTask(String project_id, TaskCreateDTO taskDTO) {
        TaskDTO tdto = new TaskDTO();
        Integer task_id = taskRepository.getMaxTaskIdx(project_id);
        tdto.setProj_id(project_id);
        tdto.setTask_id(task_id + 1);
        tdto.setContent(taskDTO.getContent());
        tdto.setTask_name(taskDTO.getTask_name());
        tdto.setStatus(taskDTO.getStatus());
        tdto.setPriority(taskDTO.getPriority());
        tdto.setStartAt(taskDTO.getStartAt());
        tdto.setEndAt(taskDTO.getEndAt());
        Task task = taskMapper.toEntity(tdto);
        Task savedTask = taskRepository.save(task);
        return taskMapper.toDTO(savedTask);
    }

    public void assignTask(String uid, String proj_id, Integer task_id) throws BadRequestException {

        TaskAssigneeId id = new TaskAssigneeId(uid, proj_id, task_id);
        Assign newAssignment = new Assign();
        newAssignment.setId(id);

        assignRepository.save(newAssignment);
    }

    public List<TaskDTO> getAllTasks(){
        List<Task> tasks = taskRepository.findAll();

        return tasks.stream()
                .map(taskMapper::toDTO)
                .toList();
    }

    public List<TaskAssigneeGroupDTO> getTasksByProject(String project_id){
        List<Object[]> tasks = taskRepository.findTaskByProjId(project_id);

        List<TaskAssigneeDTO> taskAssignments = tasks.stream().map(item -> {
            TaskDTO task = taskMapper.toDTO((Task) item[0]);
            UserDTO user = userMapper.toDTO((User) item[1]);

            TaskAssigneeDTO taskAssign = new TaskAssigneeDTO();
            taskAssign.setTask(task);
            taskAssign.setMember(user);
            return taskAssign;
        }).toList();

        List<TaskAssigneeGroupDTO> taskGroup = taskAssignments.stream()
                .collect(Collectors.groupingBy(
                        item -> item.getTask(),
                        Collectors.mapping(TaskAssigneeDTO::getMember, Collectors.toList())
                ))
                .entrySet().stream()
                .map(entry -> {
                   TaskAssigneeGroupDTO groupDTO = new TaskAssigneeGroupDTO();
                   groupDTO.setTask(entry.getKey());
                   groupDTO.setMembers(entry.getValue());
                   return groupDTO;
                } ).toList();

        return taskGroup;
    }

    public TaskDTO getTaskById(String project_id, Integer task_id){
        ProjectTaskId id = new ProjectTaskId(project_id, task_id);
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found"));
        return taskMapper.toDTO(task);
    }

    public List<TaskDTO> getAllTasksByAssignee(String project_id, String uid){
        List<Task> tasks = taskRepository.findAllByAssignee(UUID.fromString(project_id), UUID.fromString(uid));

        return tasks.stream().map(taskMapper::toDTO).toList();
    }

    public TaskDTO updateTask(String project_id, Integer task_id, TaskUpdateDTO taskUpdateDTO) {
        ProjectTaskId id = new ProjectTaskId(project_id, task_id);
        Task task = taskRepository.findById(id).orElseThrow(() -> new RuntimeException("Task not found"));
        taskMapper.updateTask(taskUpdateDTO, task);

        Task taskRes = taskRepository.save(task);
        return taskMapper.toDTO(taskRes);
    }

    public void deleteTask(String project_id, Integer task_id) {
        ProjectTaskId id = new ProjectTaskId(project_id, task_id);
        taskRepository.deleteById(id);
    }
}
