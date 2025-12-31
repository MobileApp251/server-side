package mobile.jira.clonejira.service;

import java.util.Optional;
import java.util.UUID;

import jakarta.validation.constraints.Email;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import mobile.jira.clonejira.dto.auth.UserDTO;
import mobile.jira.clonejira.entity.User;
import mobile.jira.clonejira.mapper.*;
import mobile.jira.clonejira.repository.UserRepository;
import org.springframework.validation.annotation.Validated;

@Service
@RequiredArgsConstructor
@Validated
public class UserService {
    private final UserRepository userRepository;

    @Qualifier("userMapper")
    private final UserMapper mapper;

    public UserDTO createUser(
          @Email(message = "Invalid email") String email
    ) {
        Optional<User> user = userRepository.findByEmail(email);
        User userRes;
        if (user.isPresent()) {
            userRes = user.get();
            userRes.set_google_login(true);
            return mapper.toDTO(userRepository.save(userRes));
        }

        userRes = new User();
        userRes.setEmail(email);
        userRes.set_google_login(true);
        User newUser =  userRepository.save(userRes);
        return mapper.toDTO(newUser);
    }

    public UserDTO getUserById(String uid) throws BadRequestException {
        if (uid == null) throw new BadRequestException("Field missed!");
        return mapper.toDTO(userRepository.getReferenceById(UUID.fromString(uid)));
    }

    public Optional<UserDTO> getUserByEmail(String email) throws BadRequestException {
        if (email == null) throw new BadRequestException("Email missed!");
        return userRepository.findByEmail(email)
                            .map(mapper::toDTO);
    }
}
