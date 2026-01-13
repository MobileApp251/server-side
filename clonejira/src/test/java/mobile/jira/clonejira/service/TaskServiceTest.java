package mobile.jira.clonejira.service;

import mobile.jira.clonejira.dto.auth.UserDTO;
import mobile.jira.clonejira.dto.task.*;
import mobile.jira.clonejira.entity.Assign;
import mobile.jira.clonejira.entity.Task;
import mobile.jira.clonejira.entity.User;
import mobile.jira.clonejira.entity.key.ProjectTaskId;
import mobile.jira.clonejira.entity.key.TaskAssigneeId;
import mobile.jira.clonejira.enums.TaskPriority;
import mobile.jira.clonejira.enums.TaskStatus;
import mobile.jira.clonejira.mapper.TaskMapper;
import mobile.jira.clonejira.mapper.UserMapper;
import mobile.jira.clonejira.repository.AssignRepository;
import mobile.jira.clonejira.repository.ProjectRepository;
import mobile.jira.clonejira.repository.TaskRepository;
import org.apache.coyote.BadRequestException;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Tags;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;

import java.time.Instant;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@Tags({
        @Tag("service"),
        @Tag("unit")
})
public class TaskServiceTest {

    @InjectMocks
    private TaskService taskService;

    @Mock
    private NotificationService notificationService;

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private AssignRepository assignRepository;

    @Mock
    private TaskMapper taskMapper;

    @Mock
    private UserMapper userMapper;

    @Test
    void testCreateTask() {
        // Input
        String projectId = "proj-001";
        TaskCreateDTO createDTO = new TaskCreateDTO();
        createDTO.setTask_name("New Task");
        createDTO.setContent("Content");
        createDTO.setStatus(TaskStatus.OPEN);
        createDTO.setPriority(TaskPriority.HIGH);

        Task taskEntity = new Task();
        taskEntity.setTask_name("New Task");

        Task savedTask = new Task();
        savedTask.setId(new ProjectTaskId(projectId, 1));
        savedTask.setTask_name("New Task");

        TaskDTO expectedDTO = new TaskDTO();
        expectedDTO.setProj_id(projectId);
        expectedDTO.setTask_id(1);

        // Mocks
        when(taskRepository.getMaxTaskIdx(projectId)).thenReturn(0);
        when(taskMapper.toEntity(any(TaskDTO.class))).thenReturn(taskEntity);
        when(taskRepository.save(any(Task.class))).thenReturn(savedTask);
        when(taskMapper.toDTO(savedTask)).thenReturn(expectedDTO);

        // Execute
        TaskDTO result = taskService.createTask(projectId, createDTO);

        // Verify
        assertNotNull(result);
        assertEquals(1, result.getTask_id());
        assertEquals(projectId, result.getProj_id());

        ArgumentCaptor<TaskDTO> dtoCaptor = ArgumentCaptor.forClass(TaskDTO.class);
        verify(taskMapper).toEntity(dtoCaptor.capture());
        assertEquals(1, dtoCaptor.getValue().getTask_id()); // Check logic task_id = max + 1

        System.out.println("Pass CreateTask");
    }

    @Test
    void testUnassignTask() throws BadRequestException {
        String uid = UUID.randomUUID().toString();
        String projId = "proj-1";
        Integer taskId = 10;

        taskService.unassignTask(uid, projId, taskId);

        verify(assignRepository).deleteById(any(TaskAssigneeId.class));
        System.out.println("Pass UnassignTask");
    }

    @Test
    void testGetAllTasks() {
        // Input
        String uid = UUID.randomUUID().toString();
        Map<String, String> params = new HashMap<>();
        params.put("page", "0");
        params.put("limit", "5");
        params.put("sortBy", "startAt");
        params.put("sortDir", "asc");

        Task task = new Task();
        TaskDTO taskDTO = new TaskDTO();

        // Mocks
        when(taskRepository.getAllTasksByUserId(eq(UUID.fromString(uid)), any(Pageable.class), any(), any()))
                .thenReturn(List.of(task));
        when(taskMapper.toDTO(task)).thenReturn(taskDTO);

        // Execute
        List<TaskDTO> result = taskService.getAllTasks(uid, params);

        // Verify
        assertEquals(1, result.size());
        verify(taskRepository).getAllTasksByUserId(any(), any(Pageable.class), any(), any());
        System.out.println("Pass GetAllTasks");
    }

    @Test
    void testGetTasksByProject_Grouping() {
        // Scenario: 1 Task assigned to 1 User
        String projId = "proj-1";

        Task task = new Task();
        task.setTask_name("Task A");
        User user = new User();
        user.setUsername("User A");

        Object[] row = new Object[]{task, user};

        TaskDTO taskDTO = new TaskDTO();
        taskDTO.setTask_name("Task A");
        UserDTO userDTO = new UserDTO();
        userDTO.setUsername("User A");

        // Mocks
        when(taskRepository.findTaskByProjIdWithAssignee(projId)).thenReturn(List.<Object[]>of(row));
        when(taskMapper.toDTO(task)).thenReturn(taskDTO);
        when(userMapper.toDTO(user)).thenReturn(userDTO);

        // Execute
        List<TaskAssigneeGroupDTO> result = taskService.getTasksByProject(projId);

        // Verify
        assertEquals(1, result.size());
        assertEquals("Task A", result.get(0).getTask().getTask_name());
        assertEquals(1, result.get(0).getMembers().size());
        assertEquals("User A", result.get(0).getMembers().get(0).getUsername());

        System.out.println("Pass GetTasksByProject");
    }

    @Test
    void testGetAllTasksByProject_MergeLogic() {
        // Scenario:
        // Task A has 1 Assignee (from getTasksByProject logic)
        // Task B has 0 Assignees (exists in findTaskByProjId but not in assignment list)
        String projId = "proj-1";

        Task taskA = new Task(); taskA.setId(new ProjectTaskId(projId, 1));
        Task taskB = new Task(); taskB.setId(new ProjectTaskId(projId, 2));
        User userA = new User();

        TaskDTO taskDTO_A = new TaskDTO(); taskDTO_A.setTask_id(1);
        TaskDTO taskDTO_B = new TaskDTO(); taskDTO_B.setTask_id(2);
        UserDTO userDTO_A = new UserDTO();

        // Mock 1: Assignments (Only Task A has assignment)
        Object[] assignmentRow = new Object[]{taskA, userA};
        when(taskRepository.findTaskByProjIdWithAssignee(projId)).thenReturn(List.<Object[]>of(assignmentRow));
        when(taskMapper.toDTO(taskA)).thenReturn(taskDTO_A); // Called in getTasksByProject
        when(userMapper.toDTO(userA)).thenReturn(userDTO_A);

        // Mock 2: All Tasks (Task A and Task B exist)
        when(taskRepository.findTaskByProjId(projId)).thenReturn(List.of(taskA, taskB));
        // Note: taskMapper.toDTO called again inside getAllTasksByProject for the full list
        // Since we are mocking, we just ensure it returns the DTOs
        when(taskMapper.toDTO(taskB)).thenReturn(taskDTO_B);

        // Execute
        List<TaskAssigneeGroupDTO> result = taskService.getAllTasksByProject(projId);

        // Verify
        assertEquals(2, result.size());

        // Find Task A result
        TaskAssigneeGroupDTO resA = result.stream().filter(r -> r.getTask().getTask_id() == 1).findFirst().orElseThrow();
        assertEquals(1, resA.getMembers().size());

        // Find Task B result
        TaskAssigneeGroupDTO resB = result.stream().filter(r -> r.getTask().getTask_id() == 2).findFirst().orElseThrow();
        assertEquals(0, resB.getMembers().size());

        System.out.println("Pass GetAllTasksByProject (Merging)");
    }

    @Test
    void testGetTaskById_Success() throws BadRequestException {
        String projId = "proj-1";
        Integer taskId = 1;
        ProjectTaskId compositeId = new ProjectTaskId(projId, taskId);

        Task task = new Task();
        User user = new User();

        when(taskRepository.findById(compositeId)).thenReturn(Optional.of(task));
        when(assignRepository.getAssigneesByTask(task)).thenReturn(Optional.of(List.of(user)));
        when(taskMapper.toDTO(task)).thenReturn(new TaskDTO());
        when(userMapper.toDTO(user)).thenReturn(new UserDTO());

        TaskAssigneeGroupDTO result = taskService.getTaskById(projId, taskId);

        assertNotNull(result);
        assertEquals(1, result.getMembers().size());
        System.out.println("Pass GetTaskById_Success");
    }

    @Test
    void testGetTaskById_NotFound() {
        String projId = "proj-1";
        Integer taskId = 99;
        when(taskRepository.findById(any())).thenReturn(Optional.empty());

        assertThrows(BadRequestException.class, () -> taskService.getTaskById(projId, taskId));
        System.out.println("Pass GetTaskById_NotFound");
    }

    @Test
    void testUpdateTask() {
        String projId = "proj-1";
        Integer taskId = 1;
        TaskUpdateDTO updateDTO = new TaskUpdateDTO();
        updateDTO.setContent("Updated Content");

        Task existingTask = new Task();
        Task savedTask = new Task();
        TaskDTO resDTO = new TaskDTO();
        resDTO.setContent("Updated Content");

        when(taskRepository.findById(new ProjectTaskId(projId, taskId))).thenReturn(Optional.of(existingTask));
        when(taskRepository.save(existingTask)).thenReturn(savedTask);
        when(taskMapper.toDTO(savedTask)).thenReturn(resDTO);

        // Simulate Mapper Action
        doAnswer(invocation -> {
            // Task arg = invocation.getArgument(1);
            // arg.setContent(updateDTO.getContent()); // Mock behavior if needed
            return null;
        }).when(taskMapper).updateTask(eq(updateDTO), eq(existingTask));

        TaskDTO result = taskService.updateTask(projId, taskId, updateDTO);

        assertEquals("Updated Content", result.getContent());
        verify(taskRepository).save(existingTask);
        System.out.println("Pass UpdateTask");
    }

    @Test
    void testDeleteTask() {
        String projId = "proj-1";
        Integer taskId = 1;

        taskService.deleteTask(projId, taskId);

        verify(taskRepository).deleteById(new ProjectTaskId(projId, taskId));
        System.out.println("Pass DeleteTask");
    }

    @Test
    void testGetAllTasksByAssignee() {
        String projId = UUID.randomUUID().toString();
        String uid = UUID.randomUUID().toString();

        Task task = new Task();
        when(taskRepository.findAllByAssignee(UUID.fromString(projId), UUID.fromString(uid)))
                .thenReturn(List.of(task));
        when(taskMapper.toDTO(task)).thenReturn(new TaskDTO());

        List<TaskDTO> result = taskService.getAllTasksByAssignee(projId, uid);

        assertEquals(1, result.size());
        System.out.println("Pass GetAllTasksByAssignee");
    }
}