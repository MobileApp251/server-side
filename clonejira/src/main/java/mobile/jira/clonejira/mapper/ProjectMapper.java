package mobile.jira.clonejira.mapper;

import mobile.jira.clonejira.dto.project.ProjectMemberDTO;
import mobile.jira.clonejira.dto.project.ProjectUpdateDTO;
import mobile.jira.clonejira.entity.ProjectMember;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

import mobile.jira.clonejira.dto.project.ProjectDTO;
import mobile.jira.clonejira.entity.Project;

@Mapper(componentModel = "spring")
public interface ProjectMapper {
    ProjectMapper INSTANCE = Mappers.getMapper(ProjectMapper.class);

    @Mapping(target = "tasks", ignore = true)
    @Mapping(target = "members", ignore = true)
    Project toEntity(ProjectDTO dto);

    ProjectDTO toDTO(Project project);

    ProjectMemberDTO toDTO(ProjectMember projectMember);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateProject(ProjectUpdateDTO dto, @MappingTarget Project project);
}
