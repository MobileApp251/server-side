package mobile.jira.clonejira.controller;

import mobile.jira.clonejira.dto.auth.AccessTokenDTO;
import mobile.jira.clonejira.dto.auth.LoginDTO;
import mobile.jira.clonejira.dto.auth.UserDTO;
import mobile.jira.clonejira.entity.ExpoNotiCode;
import mobile.jira.clonejira.entity.User;
import mobile.jira.clonejira.mapper.UserMapper;
import mobile.jira.clonejira.repository.ExpoNotiRepository;
import mobile.jira.clonejira.repository.NotificationRepository;
import mobile.jira.clonejira.repository.UserRepository;
import mobile.jira.clonejira.security.JwtTokenProvider;
import mobile.jira.clonejira.service.ExpoNotiService;
import mobile.jira.clonejira.service.JwtService;
import mobile.jira.clonejira.service.NotificationService;
import mobile.jira.clonejira.service.UserService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import tools.jackson.databind.ObjectMapper;

import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@WebMvcTest(MobileLoginController.class)
@AutoConfigureMockMvc(addFilters = false)
@Tags({
        @Tag("api"),
        @Tag("unit")
})
public class MobileLoginControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UserRepository userRepository;

    @MockitoBean
    private ExpoNotiRepository expoNotiRepository;

    @MockitoBean
    private NotificationRepository notificationRepository;

    @MockitoBean
    private JwtTokenProvider jwtTokenProvider;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private UserMapper userMapper;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private NotificationService notificationService;

    @MockitoBean
    private ExpoNotiService expoNotiService;

    @MockitoBean
    private JpaMetamodelMappingContext jpaMetamodelMappingContext;

    private User mockUser;
    private LoginDTO loginDTO;

    @MockitoBean
    private PasswordEncoder passwordEncoder;

    private final String MOCK_TOKEN = "mock.jwt.token";

    @BeforeEach
    public void setup() {
        loginDTO = new LoginDTO();
        loginDTO.setEmail("admin@admin");
        loginDTO.setPassword("admin");

        mockUser = new User();
        mockUser.setEmail("admin@admin");
        mockUser.setPassword("admin");
        mockUser.setUid(UUID.randomUUID());
    }

    @Test
    void testSignUp_Success() throws Exception {
        String mockToken = "mock.jwt.token";

        when(passwordEncoder.encode(loginDTO.getPassword())).thenReturn("encoded_password");
        when(userRepository.save(any(User.class))).thenReturn(mockUser);
        when(jwtTokenProvider.generateToken(any(UsernamePasswordAuthenticationToken.class))).thenReturn(mockToken);

        mockMvc.perform(
                MockMvcRequestBuilders.post("/auth/sign-up")
                        .contentType(String.valueOf(MediaType.APPLICATION_JSON))
                        .content(objectMapper.writeValueAsString(loginDTO))
        ).andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.content().string(objectMapper.writeValueAsString(new AccessTokenDTO(mockToken))));

        System.out.println("Pass Sign up successful with mock: " +  mockUser);
    }

    @Test
    void testSignUp_EmailAlreadyInUse() throws Exception {
        // Giả lập user đã tồn tại và đã có password (đã đăng ký)
        when(userRepository.findByEmail(loginDTO.getEmail())).thenReturn(Optional.of(mockUser));

        mockMvc.perform(
                        MockMvcRequestBuilders.post("/auth/sign-up")
                                .contentType(String.valueOf(MediaType.APPLICATION_JSON))
                                .content(objectMapper.writeValueAsString(loginDTO))
                ).andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().string("Email already in use!"));

        System.out.println("Pass Sign up fail - Email exists");
    }

    @Test
    void testSignUp_WithNotificationToken() throws Exception {
        String notiToken = "test-noti-token";

        when(userRepository.findByEmail(loginDTO.getEmail())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(loginDTO.getPassword())).thenReturn("encoded_password");
        when(userRepository.save(any(User.class))).thenReturn(mockUser);
        when(jwtTokenProvider.generateToken(any(UsernamePasswordAuthenticationToken.class))).thenReturn(MOCK_TOKEN);

        mockMvc.perform(
                MockMvcRequestBuilders.post("/auth/sign-up")
                        .param("noti_token", notiToken)
                        .contentType(String.valueOf(MediaType.APPLICATION_JSON))
                        .content(objectMapper.writeValueAsString(loginDTO))
        ).andExpect(MockMvcResultMatchers.status().isOk());

        // Verify logic lưu notification token
        verify(expoNotiRepository).save(any(ExpoNotiCode.class));
        verify(expoNotiService).sendPushNotification(eq(notiToken), anyString(), anyString(), any(), anyString(), any(), any());

        System.out.println("Pass Sign up with Noti Token");
    }

    // --- SIGN IN TESTS ---

    @Test
    void testSignIn_Success() throws Exception {
        when(userRepository.findByEmail(loginDTO.getEmail())).thenReturn(Optional.of(mockUser));
        // Password nhập vào khớp với password trong DB
        when(passwordEncoder.matches(loginDTO.getPassword(), mockUser.getPassword())).thenReturn(true);
        when(jwtTokenProvider.generateToken(any(UsernamePasswordAuthenticationToken.class))).thenReturn(MOCK_TOKEN);

        mockMvc.perform(
                        MockMvcRequestBuilders.post("/auth/sign-in")
                                .contentType(String.valueOf(MediaType.APPLICATION_JSON))
                                .content(objectMapper.writeValueAsString(loginDTO))
                ).andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string(objectMapper.writeValueAsString(new AccessTokenDTO(MOCK_TOKEN))));

        System.out.println("Pass Sign in successful");
    }

    @Test
    void testSignIn_UserNotFound() throws Exception {
        when(userRepository.findByEmail(loginDTO.getEmail())).thenReturn(Optional.empty());

        mockMvc.perform(
                        MockMvcRequestBuilders.post("/auth/sign-in")
                                .contentType(String.valueOf(MediaType.APPLICATION_JSON))
                                .content(objectMapper.writeValueAsString(loginDTO))
                ).andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().string("User not found!"));

        System.out.println("Pass Sign in fail - User not found");
    }

    @Test
    void testSignIn_WrongPassword() throws Exception {
        when(userRepository.findByEmail(loginDTO.getEmail())).thenReturn(Optional.of(mockUser));
        // Password nhập vào KHÔNG khớp
        when(passwordEncoder.matches(loginDTO.getPassword(), mockUser.getPassword())).thenReturn(false);

        mockMvc.perform(
                        MockMvcRequestBuilders.post("/auth/sign-in")
                                .contentType(String.valueOf(MediaType.APPLICATION_JSON))
                                .content(objectMapper.writeValueAsString(loginDTO))
                ).andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().string("Wrong Password!"));

        System.out.println("Pass Sign in fail - Wrong Password");
    }
}
