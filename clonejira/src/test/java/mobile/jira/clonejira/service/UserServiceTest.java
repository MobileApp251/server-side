package mobile.jira.clonejira.service;

import mobile.jira.clonejira.dto.auth.UserDTO;
import mobile.jira.clonejira.entity.User;
import mobile.jira.clonejira.mapper.UserMapper;
import mobile.jira.clonejira.repository.UserRepository;
import org.apache.coyote.BadRequestException;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Tags;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Tags({
        @Tag("service"),
        @Tag("unit")
})
@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Mock
    private UserMapper userMapper;

    @Test
    void testUserByEmail() throws BadRequestException {
        UUID uid = UUID.randomUUID();
        String email = "duongdd1@member.com";
        User user = new User();
        user.setUid(uid);
        user.setEmail(email);
        user.setPassword("password");
        user.setUsername("username");

        UserDTO userDTOResult = new UserDTO();
        userDTOResult.setUid(uid.toString());
        userDTOResult.setEmail(email);
        userDTOResult.setPassword("password");
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        when(userMapper.toDTO(user)).thenReturn(userDTOResult);

        Optional<UserDTO> res = userService.getUserByEmail(email);

        if (res.isEmpty()) {
            throw new BadRequestException("No user found with email " + email);
        }

        assert(res.get().getEmail().equals(email));
        verify(userRepository).findByEmail(email);
        System.out.println("Pass GetUserByEmail");
    }

    @Test
    void testCreateUser() throws BadRequestException {
        when(userRepository.save(any(User.class)))
        .thenReturn(new User())
                .thenAnswer(invocation -> {
                    User user = invocation.getArgument(0);
                    user.setUid(UUID.randomUUID());
                    return user;}
        );

        userService.createUser("mock");

        verify(userRepository).save(any(User.class));
        System.out.println("Pass CreateUser");
    }
}
