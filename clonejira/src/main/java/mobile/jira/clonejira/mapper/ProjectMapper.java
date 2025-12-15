package mobile.jira.clonejira.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Component;

import mobile.jira.clonejira.dto.ProjectDTO;
import mobile.jira.clonejira.entity.Project;

@Mapper(componentModel = "spring")
@Component
public interface ProjectMapper {
    ProjectMapper INSTANCE = Mappers.getMapper(ProjectMapper.class);

    @Mapping(target = "tasks", ignore = true)
    @Mapping(target = "members", ignore = true)
    Project toEntity(ProjectDTO dto);

    ProjectDTO toDTO(Project project);
}
