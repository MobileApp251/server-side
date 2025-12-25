package mobile.jira.clonejira.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import mobile.jira.clonejira.dto.*;
import mobile.jira.clonejira.entity.ProjectMember;
import org.apache.coyote.BadRequestException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import mobile.jira.clonejira.entity.Participate;
import mobile.jira.clonejira.entity.Project;
import mobile.jira.clonejira.entity.User;
import mobile.jira.clonejira.entity.key.ProjectMemberId;
import mobile.jira.clonejira.enums.ProjectRole;
import mobile.jira.clonejira.mapper.ProjectMapper;
import mobile.jira.clonejira.repository.ParticipateRepository;
import mobile.jira.clonejira.repository.ProjectRepository;
import mobile.jira.clonejira.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class ProjectService {
    private final ProjectRepository projectRepository;
    private final ParticipateRepository participateRepository;
    private final UserRepository userRepository;
    private final ProjectMapper mapper;

    public void joinProject(String uid, String proj_id, String role) {
        ProjectMemberId id = new ProjectMemberId(uid, proj_id);

        ProjectRole roleEnum = ProjectRole.valueOf(role.toUpperCase());

        Participate participate = Participate.builder()
                            .id(id).role(roleEnum).build();

        participateRepository.save(participate);
    }

    public ProjectDTO createProject(String uid, ProjectCreateDTO dto){
        Project project = new Project();
        
        project.setProj_name(dto.getProj_name());
        project.setDescription(dto.getDescription());
        project.setStartAt(dto.getStartAt());
        project.setEndAt(dto.getEndAt());

        Project newProject = projectRepository.save(project);

        joinProject(uid, newProject.getProj_id().toString(), "leader");

        return mapper.toDTO(newProject);
    }

    public List<ProjectDTO> getAllMyProjects(String uid, int page, int size, String sortBy, String sortDir) throws BadRequestException {

        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name())
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(page, size, sort);
        Optional<User> user = userRepository.findById(UUID.fromString(uid));

        if (user.isEmpty()) throw new BadRequestException("User not found!");

        return projectRepository
            .findAllMyProjects(UUID.fromString(uid), pageable).stream()
            .map(mapper::toDTO).toList();
    }

    public ProjectGetDTO getProjectById(String proj_id) throws BadRequestException {
        Optional<Project> project = projectRepository.findById(UUID.fromString(proj_id));

        if (project.isEmpty()) throw new BadRequestException("Project not found!");

        List<ProjectMember> user = projectRepository.findMembersOfProject(UUID.fromString(proj_id));

        ProjectGetDTO dto = new ProjectGetDTO();
        dto.setProject(mapper.toDTO(project.get()));
        List<ProjectMemberDTO> pm = user.stream().map(mapper::toDTO).toList();
        dto.setMembers(pm);

        return dto;
    }

    public ProjectDTO updateProject(String proj_id, ProjectUpdateDTO dto) throws BadRequestException {
        Project project = projectRepository.findById(UUID.fromString(proj_id)).orElseThrow(() -> new BadRequestException("Project not found!"));
        mapper.updateProject(dto, project);
        Project projectRes = projectRepository.save(project);
        return mapper.toDTO(projectRes);
    }

    public void deleteProject(String project_id) {
        projectRepository.deleteById(UUID.fromString(project_id));
    }
}
