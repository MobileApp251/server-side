package mobile.jira.clonejira.controller;

import mobile.jira.clonejira.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;

import java.util.List;
import java.util.Optional;

import org.apache.coyote.BadRequestException;
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;

import lombok.RequiredArgsConstructor;
import mobile.jira.clonejira.dto.auth.UserDTO;
import mobile.jira.clonejira.service.UserService;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class UserController {
    private final UserService userService;
    private final UserRepository userRepository;


    @GetMapping()
    public UserDTO getUserDetail(
        @RequestParam String email
    ) throws NotFoundException, BadRequestException {
        Optional<UserDTO> user = userService.getUserByEmail(email);

        if (user.isEmpty()) throw new NotFoundException();

        return user.get();
    }

    @GetMapping("email")
    public ResponseEntity<?> searchEmailsList(String pattern){
        try {
            Optional<List<String>> listEmails = userRepository.getEmailList("%" + pattern + "%");
            if (listEmails.isEmpty()) return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No email found");

            return ResponseEntity.status(HttpStatus.OK).body(listEmails.get());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
}
