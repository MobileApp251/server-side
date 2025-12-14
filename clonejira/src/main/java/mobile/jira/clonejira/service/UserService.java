package mobile.jira.clonejira.service;

import java.util.Optional;
import java.util.UUID;

import org.apache.coyote.BadRequestException;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import mobile.jira.clonejira.dto.UserDTO;
import mobile.jira.clonejira.entity.User;
import mobile.jira.clonejira.mapper.*;
import mobile.jira.clonejira.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper mapper;

    public UserDTO createUser(String email) {
        User user = new User();
        user.setEmail(email);
        User newUser =  userRepository.save(user);
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
