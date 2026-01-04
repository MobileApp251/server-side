package mobile.jira.clonejira.service;

import mobile.jira.clonejira.dto.project.ProjectCreateDTO;
import mobile.jira.clonejira.dto.project.ProjectDTO;
import mobile.jira.clonejira.entity.Participate;
import mobile.jira.clonejira.entity.Project;
import mobile.jira.clonejira.entity.User;
import mobile.jira.clonejira.entity.key.ProjectMemberId;
import mobile.jira.clonejira.enums.ProjectRole;
import mobile.jira.clonejira.mapper.ProjectMapper;
import mobile.jira.clonejira.repository.ParticipateRepository;
import mobile.jira.clonejira.repository.ProjectRepository;
import mobile.jira.clonejira.repository.UserRepository;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Tags;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Date;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
@Tags({
        @Tag("service"),
        @Tag("unit")
})
public class ProjectServiceTest {

    @InjectMocks
    private ProjectService projectService;

    @Mock
    private NotificationService notificationService;

    @Mock
    private ProjectMapper projectMapper;

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ParticipateRepository participateRepository;

    @Captor
    private ArgumentCaptor<Project> projectCaptor;

    @Test
    void testCreateProject() {
        // Input Data
        String uid = UUID.randomUUID().toString();
        ProjectCreateDTO projectCreateDTO = new ProjectCreateDTO(
                "sample project",
                "sample",
                new Date().toInstant(),
                new Date().toInstant()
        );

        // Request Transfer
        ProjectDTO requestDTO = new ProjectDTO();
        requestDTO.setProj_name(projectCreateDTO.getProj_name());
        requestDTO.setDescription(projectCreateDTO.getDescription());
        requestDTO.setStartAt(projectCreateDTO.getStartAt());
        requestDTO.setEndAt(projectCreateDTO.getEndAt());

        // DTO -> Entity
        Project project = new Project();
        project.setStartAt(projectCreateDTO.getStartAt());
        project.setEndAt(projectCreateDTO.getEndAt());
        project.setDescription(projectCreateDTO.getDescription());
        project.setProj_name(projectCreateDTO.getProj_name());


        UUID generatedID = UUID.randomUUID();

        ProjectDTO projectDTO = new ProjectDTO();
        projectDTO.setProj_id(generatedID.toString());
        projectDTO.setProj_name(projectCreateDTO.getProj_name());
        projectDTO.setDescription(projectCreateDTO.getDescription());
        projectDTO.setStartAt(projectCreateDTO.getStartAt());
        projectDTO.setEndAt(projectCreateDTO.getEndAt());
        projectDTO.setDescription(projectCreateDTO.getDescription());

        when(projectRepository.save(any(Project.class))).thenAnswer(invocation -> {
            Project res = invocation.getArgument(0);
            res.setProj_id(UUID.randomUUID());
            return res;
        });
        when(participateRepository.save(any(Participate.class))).thenReturn(
                new Participate(new ProjectMemberId(uid, projectDTO.getProj_id()), ProjectRole.LEADER)
        );
        when(projectMapper.toDTO(any(Project.class))).thenAnswer(invocation -> {
            Project entity = invocation.getArgument(0);
            ProjectDTO dto = new ProjectDTO();
            dto.setProj_id(entity.getProj_id().toString());
            dto.setProj_name(entity.getProj_name());
            dto.setDescription(entity.getDescription());
            dto.setStartAt(entity.getStartAt());
            dto.setEndAt(entity.getEndAt());
            return dto;
        });

        projectService.createProject(uid, projectCreateDTO);

        verify(projectRepository).save(projectCaptor.capture());

        Project actual = projectCaptor.getValue();
        assertEquals(projectDTO.getProj_name(), actual.getProj_name());
        verify(participateRepository).save(any(Participate.class));

        System.out.println("Pass CreateProject");
    }

    @Test
    void testJoinProject(){
        // Input Data
        String uid = UUID.randomUUID().toString();
        String proj_id = UUID.randomUUID().toString();
        String role = "member";

        // Process
        when(participateRepository.save(any(Participate.class))).thenReturn(new Participate(new ProjectMemberId(uid, proj_id), ProjectRole.valueOf(role.toUpperCase())));

        projectService.joinProject(uid, proj_id, role);

        verify(participateRepository).save(any(Participate.class));

        System.out.println("Pass JoinProject");
    }
}
