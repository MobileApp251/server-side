package mobile.jira.clonejira.controller;

import mobile.jira.clonejira.dto.project.*;
import mobile.jira.clonejira.entity.User;
import mobile.jira.clonejira.enums.ProjectRole;
import mobile.jira.clonejira.repository.ParticipateRepository;
import mobile.jira.clonejira.repository.UserRepository;
import mobile.jira.clonejira.security.JwtTokenProvider;
import mobile.jira.clonejira.service.JwtService;
import mobile.jira.clonejira.service.ProjectService;
import mobile.jira.clonejira.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Tags;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import tools.jackson.databind.ObjectMapper;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;

@WebMvcTest(ProjectController.class)
@AutoConfigureMockMvc(addFilters = false)
@Tags({
        @Tag("api"),
        @Tag("unit")
})
public class ProjectControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    // --- Services & Repositories trực tiếp của ProjectController ---
    @MockitoBean
    private ProjectService projectService;

    @MockitoBean
    private ParticipateRepository participateRepository;

    // --- Các Bean phụ thuộc khác (Security, User, JPA) để đảm bảo Context load thành công ---
    @MockitoBean
    private UserService userService;

    @MockitoBean
    private UserRepository userRepository;

    @MockitoBean
    private JwtTokenProvider jwtTokenProvider;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private JpaMetamodelMappingContext jpaMetamodelMappingContext;

    @MockitoBean
    private UserDetails userDetails;

    // Các hằng số dùng chung trong test
    private final String TEST_UID = UUID.randomUUID().toString();
    private final String TEST_PROJ_ID = "project-uuid-456";

    // Khai báo biến chung
    private UsernamePasswordAuthenticationToken mockAuth;

    @BeforeEach
    void setup() {
        // 1. Khởi tạo Entity User của bạn thủ công (Vì Entity thường không có .withUsername())
        User myUser = new User();
        myUser.setUid(UUID.fromString(TEST_UID));
        myUser.setEmail("test@email.com");
        myUser.setUsername("testuser");
        // myUser.setPassword("encoded_password"); // Set nếu cần
        // myUser.setRole("USER"); // Set nếu cần

        // 2. Tạo Authentication Token chứa object myUser này
        // Lưu ý: Param thứ 3 (Authorities) nên lấy từ myUser nếu có, hoặc dùng List rỗng để tránh lỗi
        mockAuth = new UsernamePasswordAuthenticationToken(myUser, null, Collections.emptyList());
    }

    // Khi dùng chỉ cần gọi: .with(authentication(mockAuth))
    @Test
    @WithMockUser(username = "testuser", roles = "USER")
    public void testCreateNewProject_Success() throws Exception {
        // Prepare DTOs
        ProjectCreateDTO createDTO = new ProjectCreateDTO();
        createDTO.setProj_name("New Project");

        ProjectDTO resDTO = new ProjectDTO();
        resDTO.setProj_id(TEST_PROJ_ID);
        resDTO.setProj_name("New Project");

        // Mock behavior
        when(projectService.createProject(eq("testuser"), any(ProjectCreateDTO.class)))
                .thenReturn(resDTO);

        // Perform & Assert
        mockMvc.perform(MockMvcRequestBuilders.post("/projects")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDTO)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.proj_id").value(TEST_PROJ_ID))
                .andExpect(MockMvcResultMatchers.jsonPath("$.proj_name").value("New Project"));
    }

    @Test
    @WithMockUser(username = "testuser", roles = "USER")
    public void testCreateNewProject_InternalError() throws Exception {
        ProjectCreateDTO createDTO = new ProjectCreateDTO();

        // 1. MOCK SERVICE: Giả lập lỗi
        when(projectService.createProject(anyString(), any())).thenThrow(new RuntimeException("DB Error"));

        // 3. PERFORM REQUEST
        mockMvc.perform(MockMvcRequestBuilders.post("/projects")
                        .with(authentication(mockAuth)) // <--- QUAN TRỌNG: Phải có dòng này
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDTO)))
                .andExpect(MockMvcResultMatchers.status().isInternalServerError())
                .andExpect(MockMvcResultMatchers.content().string("Internal Error!"));
    }

    @Test
    public void testJoinProject_Success() throws Exception {
        ProjectJoinDTO joinDTO = new ProjectJoinDTO();
        joinDTO.setProj_id(TEST_PROJ_ID);
        joinDTO.setRole("MEMBER");

        // Service trả về void nên không cần mock return, chỉ cần không throw exception là được

        mockMvc.perform(MockMvcRequestBuilders.post("/projects/join/{uid}", TEST_UID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(joinDTO)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("Join Project successfully!"));
    }

    @Test
    public void testAddMemberToProject_Success() throws Exception {
        String email = "test@example.com";

        mockMvc.perform(MockMvcRequestBuilders.post("/projects/add/{project_id}/{email}", TEST_PROJ_ID, email))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("Add member to project successfully!"));
    }

    @Test
    @WithMockUser(username = "testuser", roles = "USER")
    public void testGetAllMyProjects_Success() throws Exception {

        // 3. Mock Service
        ProjectParticipantGroupDTO groupDTO = new ProjectParticipantGroupDTO();
        List<ProjectParticipantGroupDTO> resultList = Collections.singletonList(groupDTO);

        // Lưu ý: service.getAllMyProjects có thể cần UserDetails hoặc String uid, hãy khớp với mock
        when(projectService.getAllMyProjects(any(), anyInt(), anyInt(), anyString(), anyString()))
                .thenReturn(resultList);

        mockMvc.perform(MockMvcRequestBuilders.get("/projects")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(1));
    }

    @Test
    public void testGetProjectById_Success() throws Exception {
        ProjectGetDTO projectGetDTO = new ProjectGetDTO();
        ProjectDTO pDto = new ProjectDTO();
        pDto.setProj_id(TEST_PROJ_ID);
        projectGetDTO.setProject(pDto);

        when(projectService.getProjectById(TEST_PROJ_ID)).thenReturn(projectGetDTO);

        mockMvc.perform(MockMvcRequestBuilders.get("/projects/{project_id}", TEST_PROJ_ID))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.project.proj_id").value(TEST_PROJ_ID));
    }

    @Test
    public void testGetProjectById_NotFound() throws Exception {
        when(projectService.getProjectById(TEST_PROJ_ID))
                .thenThrow(new RuntimeException("Project not found!"));

        mockMvc.perform(MockMvcRequestBuilders.get("/projects/{project_id}", TEST_PROJ_ID))
                .andExpect(MockMvcResultMatchers.status().isInternalServerError())
                .andExpect(MockMvcResultMatchers.content().string("Project not found!"));
    }

    @Test
    public void testUpdateProjectById_Success() throws Exception {
        ProjectUpdateDTO updateDTO = new ProjectUpdateDTO();
        updateDTO.setProj_name("Updated Name");

        ProjectDTO resDTO = new ProjectDTO();
        resDTO.setProj_name("Updated Name");

        when(projectService.updateProject(eq(TEST_PROJ_ID), any(ProjectUpdateDTO.class))).thenReturn(resDTO);

        mockMvc.perform(MockMvcRequestBuilders.patch("/projects/{project_id}", TEST_PROJ_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.proj_name").value("Updated Name"));
    }

//    @Test
//    @WithMockUser(username = "testuser", roles = "USER") // Đảm bảo TEST_UID là chuỗi UUID hợp lệ
//    public void testRemoveMemberFromProject_Success_AsLeader() throws Exception {
//        String targetUid = "target-user-uid";
//
//        // 1. SỬA LẠI MOCK REPOSITORY:
//        // Dùng any() cho các tham số để tránh việc so sánh sai khiến Mock trả về null.
//        // Lưu ý: User này phải là Entity User của bạn, không phải User của Spring Security
//        when(participateRepository.checkLeader(any(UUID.class), any()))
//                .thenReturn(Optional.of(new mobile.jira.clonejira.entity.User()));
//
//        // 2. Mock Service (như cũ)
//        Mockito.doNothing().when(projectService).removeMember(anyString(), anyString());
//
//        // 3. Perform
//        mockMvc.perform(MockMvcRequestBuilders.delete("/projects/remove/{project_id}/{uid}", TEST_PROJ_ID, targetUid)
//                        .with(user(TEST_UID))) // TEST_UID phải khớp với username trong @WithMockUser (hoặc bỏ @WithMockUser nếu dùng .with(user))
//                .andExpect(MockMvcResultMatchers.status().isOk());
//    }
//
//    @Test
//    public void testRemoveMemberFromProject_Forbidden_NotLeader() throws Exception {
//        String targetUid = "target-user-uid";
//
//        // Mock checkLeader: Trả về empty (User hiện tại KHÔNG phải leader)
//        when(participateRepository.checkLeader(UUID.fromString(TEST_UID), ProjectRole.LEADER))
//                .thenReturn(Optional.empty());
//
//        Mockito.doNothing().when(projectService).removeMember(
//                eq(TEST_PROJ_ID),
//                eq(targetUid)     // uid người bị xóa
//        );
//
//        mockMvc.perform(MockMvcRequestBuilders.delete("/projects/remove/{project_id}/{uid}", TEST_PROJ_ID, targetUid)
//                        .with(user(TEST_UID)))
//                .andExpect(MockMvcResultMatchers.status().isForbidden());
//    }

    @Test
    public void testDeleteProjectById_Success() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/projects/{project_id}", TEST_PROJ_ID))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("Delete Project successfully!"));
    }

    @Test
    public void testDeleteProjectById_Error() throws Exception {
        doThrow(new RuntimeException("Error deleting")).when(projectService).deleteProject(TEST_PROJ_ID);

        mockMvc.perform(MockMvcRequestBuilders.delete("/projects/{project_id}", TEST_PROJ_ID))
                .andExpect(MockMvcResultMatchers.status().isInternalServerError())
                .andExpect(MockMvcResultMatchers.content().string("Error deleting"));
    }
}