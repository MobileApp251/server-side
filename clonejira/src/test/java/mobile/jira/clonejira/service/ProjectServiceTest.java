package mobile.jira.clonejira.service;

import mobile.jira.clonejira.dto.auth.UserDTO;
import mobile.jira.clonejira.dto.project.*;
import mobile.jira.clonejira.entity.*;
import mobile.jira.clonejira.entity.key.ProjectMemberId;
import mobile.jira.clonejira.enums.NotifyType;
import mobile.jira.clonejira.enums.ProjectRole;
import mobile.jira.clonejira.mapper.ProjectMapper;
import mobile.jira.clonejira.mapper.UserMapper;
import mobile.jira.clonejira.repository.ExpoNotiRepository;
import mobile.jira.clonejira.repository.ParticipateRepository;
import mobile.jira.clonejira.repository.ProjectRepository;
import mobile.jira.clonejira.repository.UserRepository;
import org.apache.coyote.BadRequestException;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Tags;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Date;
import java.util.List;
import java.util.Optional;
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

    @Mock
    private ExpoNotiService expoNotiService;

    @Mock
    private ExpoNotiRepository expoNotiRepository;

    @Mock
    private UserMapper userMapper;

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

    @Test
    void testAddMember_Success() throws BadRequestException {
        // Input
        String projId = UUID.randomUUID().toString();
        String email = "test@example.com";
        UUID uid = UUID.randomUUID();
        User user = new User();
        user.setUid(uid);
        user.setEmail(email);

        ExpoNotiCode expoCode = new ExpoNotiCode();
        expoCode.setCode("ExponentPushToken[xxx]");

        // Mocking
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(expoNotiRepository.findByUser(user)).thenReturn(List.of(expoCode));
        // joinProject bên trong sẽ gọi participateRepository.save
        when(participateRepository.save(any(Participate.class))).thenReturn(new Participate());

        // Execute
        projectService.addMember(projId, email);

        // Verify
        verify(userRepository).findByEmail(email);
        verify(participateRepository).save(any(Participate.class)); // Verifies joinProject logic
        verify(expoNotiService).sendPushNotification(
                eq(expoCode.getCode()),
                eq("Project Adding"),
                anyString(),
                eq(NotifyType.ADD_PROJECT),
                eq(uid.toString()),
                eq(projId),
                isNull()
        );
        System.out.println("Pass AddMember_Success");
    }

    @Test
    void testAddMember_UserNotFound() {
        String projId = UUID.randomUUID().toString();
        String email = "notfound@example.com";

        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        assertThrows(BadRequestException.class, () -> {
            projectService.addMember(projId, email);
        });
        System.out.println("Pass AddMember_UserNotFound");
    }

    @Test
    void testGetAllMyProjects_Success() throws BadRequestException {
        // 1. Setup Input Data
        String uid = UUID.randomUUID().toString();

        // Tạo User Entity
        User user = new User();
        user.setUid(UUID.fromString(uid));

        // Tạo Project Entity
        Project projectEntity = new Project();
        projectEntity.setProj_id(UUID.randomUUID());
        projectEntity.setProj_name("Test Project");

        // Tạo DTO tương ứng để mock kết quả mapper
        ProjectDTO projectDTO = new ProjectDTO();
        projectDTO.setProj_id(projectEntity.getProj_id().toString());
        projectDTO.setProj_name("Test Project");

        UserDTO userDTO = new UserDTO();
        userDTO.setUid(uid);

        // 2. Mocking Repositories & Mappers

        // 2.1. Mock UserRepository
        when(userRepository.findById(UUID.fromString(uid))).thenReturn(Optional.of(user));

        // 2.2. Mock Query 1: Lấy danh sách Project (Phân trang)
        // QUAN TRỌNG: Chỉ trả về List<Project>, không phải Object[]
        List<Project> projectList = List.of(projectEntity);
        Page<Project> pageProject = new PageImpl<>(projectList);

        when(projectRepository.findAllMyProjects(eq(UUID.fromString(uid)), any(Pageable.class)))
                .thenReturn(pageProject);

        // 2.3. Mock Query 2: Lấy chi tiết thành viên (List<Object[]>)
        // Row giả lập kết quả JOIN: index 0 là Project, index 1 là User
        Object[] row = new Object[]{projectEntity, user};
        List<Object[]> participantsData = List.<Object[]>of(row);

        when(projectRepository.findAllMembersByProjects(projectList)) // Truyền đúng list project từ query 1
                .thenReturn(participantsData);

        // 2.4. Mock Mappers
        // Lưu ý: Đảm bảo biến mapper trong test khớp với tên biến trong Service (ở đây mình dùng projectMapper theo code mẫu cũ của bạn)
        when(projectMapper.toDTO(projectEntity)).thenReturn(projectDTO);
        when(userMapper.toDTO(user)).thenReturn(userDTO);

        // 3. Execute Service
        List<ProjectParticipantGroupDTO> result = projectService.getAllMyProjects(uid, 0, 10, "proj_name", "asc");

        // 4. Verify
        assertNotNull(result);
        assertEquals(1, result.size()); // Kiểm tra số lượng Group Project

        // Kiểm tra dữ liệu project
        assertEquals(projectDTO.getProj_id(), result.get(0).getProject().getProj_id());

        System.out.println("Pass GetAllMyProjects_Success");
    }

    @Test
    void testGetAllMyProjects_UserNotFound() {
        String uid = UUID.randomUUID().toString();
        when(userRepository.findById(UUID.fromString(uid))).thenReturn(Optional.empty());

        assertThrows(BadRequestException.class, () -> {
            projectService.getAllMyProjects(uid, 0, 10, "name", "asc");
        });
        System.out.println("Pass GetAllMyProjects_UserNotFound");
    }

    @Test
    void testGetProjectById_Success() throws BadRequestException {
        // Input
        UUID projId = UUID.randomUUID();
        Project project = new Project();
        project.setProj_id(projId);

        ProjectMember member = new ProjectMember() {
            // Mock interface projection if necessary, or just use simple implementation/mock
            // Since mapper::toDTO handles ProjectMember, we assume mapper behaves correctly
            // For simple unit test, we can mock the list return.
            @Override public UUID getUid() { return UUID.randomUUID(); }
            @Override public String getUsername() { return "test"; }
            @Override public String getEmail() { return "a@a.com"; }
        };

        // Mocking
        when(projectRepository.findById(projId)).thenReturn(Optional.of(project));
        when(projectRepository.findMembersOfProject(projId)).thenReturn(List.of(member));

        ProjectDTO projectDTO = new ProjectDTO();
        projectDTO.setProj_id(projId.toString());
        when(projectMapper.toDTO(project)).thenReturn(projectDTO);

        ProjectMemberDTO memberDTO = new ProjectMemberDTO();
        when(projectMapper.toDTO(member)).thenReturn(memberDTO);

        // Execute
        ProjectGetDTO result = projectService.getProjectById(projId.toString());

        // Verify
        assertNotNull(result);
        assertEquals(projId.toString(), result.getProject().getProj_id());
        assertEquals(1, result.getMembers().size());
        System.out.println("Pass GetProjectById_Success");
    }

    @Test
    void testGetProjectById_NotFound() {
        String projId = UUID.randomUUID().toString();
        when(projectRepository.findById(UUID.fromString(projId))).thenReturn(Optional.empty());

        assertThrows(BadRequestException.class, () -> {
            projectService.getProjectById(projId);
        });
        System.out.println("Pass GetProjectById_NotFound");
    }

    @Test
    void testUpdateProject_Success() throws BadRequestException {
        // Input
        UUID projId = UUID.randomUUID();
        ProjectUpdateDTO updateDTO = new ProjectUpdateDTO();
        updateDTO.setProj_name("New Name");

        Project existingProject = new Project();
        existingProject.setProj_id(projId);
        existingProject.setProj_name("Old Name");

        Project updatedProject = new Project();
        updatedProject.setProj_id(projId);
        updatedProject.setProj_name("New Name");

        ProjectDTO resDTO = new ProjectDTO();
        resDTO.setProj_name("New Name");

        // Mocking
        when(projectRepository.findById(projId)).thenReturn(Optional.of(existingProject));
        // Mapper.updateProject returns void, assumes it modifies the entity in place.
        // We can mimic this or just rely on the repository saving the passed object.
        doAnswer(invocation -> {
            Project p = invocation.getArgument(1);
            p.setProj_name(updateDTO.getProj_name());
            return null;
        }).when(projectMapper).updateProject(eq(updateDTO), any(Project.class));

        when(projectRepository.save(any(Project.class))).thenReturn(updatedProject);
        when(projectMapper.toDTO(updatedProject)).thenReturn(resDTO);

        // Execute
        ProjectDTO result = projectService.updateProject(projId.toString(), updateDTO);

        // Verify
        assertEquals("New Name", result.getProj_name());
        verify(projectRepository).save(any(Project.class));
        System.out.println("Pass UpdateProject_Success");
    }

    @Test
    void testUpdateProject_NotFound() {
        String projId = UUID.randomUUID().toString();
        when(projectRepository.findById(UUID.fromString(projId))).thenReturn(Optional.empty());

        assertThrows(BadRequestException.class, () -> {
            projectService.updateProject(projId, new ProjectUpdateDTO());
        });
        System.out.println("Pass UpdateProject_NotFound");
    }

    @Test
    void testDeleteProject_Success() {
        String projId = UUID.randomUUID().toString();
        when(projectRepository.existsById(UUID.fromString(projId))).thenReturn(true);

        projectService.deleteProject(projId);

        verify(projectRepository).deleteById(UUID.fromString(projId));
        System.out.println("Pass DeleteProject_Success");
    }

    @Test
    void testDeleteProject_NotFound() {
        String projId = UUID.randomUUID().toString();
        when(projectRepository.existsById(UUID.fromString(projId))).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () -> {
            projectService.deleteProject(projId);
        });
        System.out.println("Pass DeleteProject_NotFound");
    }

    @Test
    void testRemoveMember() {
        String projId = UUID.randomUUID().toString();
        String uid = UUID.randomUUID().toString();

        projectService.removeMember(projId, uid);

        verify(participateRepository).deleteById(any(ProjectMemberId.class));
        System.out.println("Pass RemoveMember");
    }
}
