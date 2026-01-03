package mobile.jira.clonejira.service;

import java.util.*;
import java.util.stream.Collectors;

import mobile.jira.clonejira.dto.auth.UserDTO;
import mobile.jira.clonejira.dto.project.*;
import mobile.jira.clonejira.entity.*;
import mobile.jira.clonejira.enums.NotifyType;
import mobile.jira.clonejira.repository.*;
import org.apache.coyote.BadRequestException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import mobile.jira.clonejira.entity.key.ProjectMemberId;
import mobile.jira.clonejira.enums.ProjectRole;
import mobile.jira.clonejira.mapper.*;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProjectService {
    private final NotificationService notificationService;
    private final ProjectRepository projectRepository;
    private final ParticipateRepository participateRepository;
    private final UserRepository userRepository;
    private final ExpoNotiService expoNotiService;
    private final ExpoNotiRepository expoNotiRepository;
    private final ProjectMapper mapper;
    private final UserMapper userMapper;

    public void joinProject(String uid, String proj_id, String role) {
        ProjectMemberId id = new ProjectMemberId(uid, proj_id);

        ProjectRole roleEnum = ProjectRole.valueOf(role.toUpperCase());

        Participate participate = Participate.builder()
                            .id(id).role(roleEnum).build();

        notificationService.sendNotification(uid, "You are added into project #" + proj_id);
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

    public void addMember(String proj_id, String email) throws BadRequestException {
        Optional<User> user = userRepository.findByEmail(email);

        if  (user.isEmpty()) throw new BadRequestException("User not found!");

        joinProject(user.get().getUid().toString(), proj_id, "member");

        List<ExpoNotiCode> codeList = expoNotiRepository.findByUser(user.get());

        for (ExpoNotiCode code : codeList) {
            try {
                expoNotiService.sendPushNotification(code.getCode(),"Project Adding" , "You are add into project #" + proj_id, NotifyType.ADD_PROJECT, user.get().getUid().toString(),proj_id, null);
            } catch (Exception e) {
                continue;
            }
        }
    }

    public List<ProjectParticipantGroupDTO> getAllMyProjects(String uid, int page, int size, String sortBy, String sortDir) throws BadRequestException {

        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name())
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(page, size, sort);
        Optional<User> user = userRepository.findById(UUID.fromString(uid));

        if (user.isEmpty()) throw new BadRequestException("User not found!");

        List<Object[]> listProjects = projectRepository
            .findAllMyProjects(UUID.fromString(uid), pageable).stream().toList();

        List<ProjectParticipantDTO> projectsWithParticipants = listProjects.stream()
                .map(item -> {
                    ProjectDTO dto = mapper.toDTO((Project) item[0]);
                    UserDTO userDTO = userMapper.toDTO((User) item[1]);

                    return new ProjectParticipantDTO(dto, userDTO);
                }).toList();

        List<ProjectParticipantGroupDTO> pRes = projectsWithParticipants.stream()
                .collect(Collectors.groupingBy(
                        item -> item.getProject(),
                        Collectors.mapping(ProjectParticipantDTO::getMember,Collectors.toList())
                )).entrySet().stream()
                .map(item -> new ProjectParticipantGroupDTO(item.getKey(), item.getValue()))
                .toList();

        return pRes;
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

    @Transactional
    public void deleteProject(String project_id) {

        if (!projectRepository.existsById(UUID.fromString(project_id))) {
            throw new IllegalArgumentException("Project not found!");
        }
        projectRepository.deleteById(UUID.fromString(project_id));
    }

    public void removeMember(String proj_id, String uid) {
        participateRepository.deleteById(new ProjectMemberId(uid, proj_id));
    }
}
