package mobile.jira.clonejira.controller;

import mobile.jira.clonejira.dto.auth.UserDTO;
import mobile.jira.clonejira.mapper.NotificationMapper;
import mobile.jira.clonejira.repository.ExpoNotiRepository;
import mobile.jira.clonejira.repository.NotificationRepository;
import mobile.jira.clonejira.repository.UserRepository;
import mobile.jira.clonejira.security.JwtTokenProvider;
import mobile.jira.clonejira.service.ExpoNotiService;
import mobile.jira.clonejira.service.JwtService;
import mobile.jira.clonejira.service.NotificationService;
import mobile.jira.clonejira.service.UserService;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Tags;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import tools.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.Mockito.when;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
@Tags({
        @Tag("api"),
        @Tag("unit")
})
public class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;
    @MockitoBean
    private JwtTokenProvider jwtTokenProvider;
    @MockitoBean
    private JwtService jwtService;
    @MockitoBean
    private JpaMetamodelMappingContext jpaMetamodelMappingContext;

    @MockitoBean
    private ExpoNotiRepository expoNotiRepository;

    @MockitoBean
    private ExpoNotiService expoNotiService;

    @MockitoBean
    private NotificationService notificationService;

    @MockitoBean
    private NotificationRepository notificationRepository;

    @MockitoBean
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private NotificationMapper notificationMapper;

    @Test
    public void testGetUserDetails() throws Exception {
        UserDTO mockedUser = new UserDTO();
        mockedUser.setUid(UUID.randomUUID().toString());
        mockedUser.setEmail("mock@mail.ex");
        mockedUser.setPassword("password");
        mockedUser.setUsername("username");

        when(userService.getUserByEmail("mock@mail.ex")).thenReturn(Optional.of(mockedUser));

        mockMvc.perform(MockMvcRequestBuilders.get("/users").param("email", mockedUser.getEmail()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string(objectMapper.writeValueAsString(mockedUser)));
    }

//    @Test
//    public void testGetUserDetails_NotFound() throws Exception {
//
//    }
}
