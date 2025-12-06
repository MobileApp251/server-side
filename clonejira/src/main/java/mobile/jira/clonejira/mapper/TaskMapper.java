package mobile.jira.clonejira.mapper;

import org.springframework.stereotype.Component;

import mobile.jira.clonejira.dto.TaskDTO;
import mobile.jira.clonejira.entity.Task;
import mobile.jira.clonejira.entity.key.ProjectTaskId;

@Component
public class TaskMapper {
    public TaskDTO toDTO(Task task){
        if (task == null) return null;

        TaskDTO dto = new TaskDTO();
        dto.setTask_id(task.getId().getTask_id());
        dto.setProj_id(task.getId().getProj_id());
        dto.setTask_name(task.getTask_name());
        dto.setContent(task.getContent());
        dto.setCreateAt(task.getCreateAt());
        dto.setUpdateAt(task.getUpdateAt());
        dto.setStatus(task.getStatus());
        return dto;
    }

    public Task toEntity(TaskDTO dto){
        if (dto == null) return null;

        Task task = new Task(null, null, null, null, null, null);
        ProjectTaskId id = new ProjectTaskId();
        id.setProj_id(dto.getProj_id());
        id.setTask_id(dto.getTask_id());
        task.setId(id);
        task.setTask_name(dto.getTask_name());
        task.setContent(dto.getContent());
        task.setCreateAt(dto.getCreateAt());
        task.setUpdateAt(dto.getUpdateAt());
        task.setStatus(dto.getStatus());
        return task;
    }
}
