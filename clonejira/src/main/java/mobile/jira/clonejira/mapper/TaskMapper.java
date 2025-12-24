package mobile.jira.clonejira.mapper;

import mobile.jira.clonejira.dto.TaskUpdateDTO;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Component;

import mobile.jira.clonejira.dto.TaskDTO;
import mobile.jira.clonejira.entity.Task;

@Mapper(componentModel = "spring")
@Component
public interface TaskMapper {
    TaskMapper INSTANCE = Mappers.getMapper(TaskMapper.class);

    @Mapping(source = "task_id", target = "id.task_id")
    @Mapping(source = "proj_id", target = "id.proj_id")
    @Mapping(target = "project", ignore = true)
    Task toEntity(TaskDTO dto);

    @Mapping(source = "id.task_id", target = "task_id")
    @Mapping(source = "id.proj_id", target = "proj_id")
    @Mapping(target = "createAt", ignore = true)
    @Mapping(target = "updateAt", ignore = true)
    TaskDTO toDTO(Task task);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateTask(TaskUpdateDTO dto, @MappingTarget Task task);
}
