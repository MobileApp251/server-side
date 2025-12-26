package mobile.jira.clonejira.controller;

import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;

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


    @GetMapping()
    public UserDTO getUserDetail(
        @RequestParam String email
    ) throws NotFoundException, BadRequestException {
        Optional<UserDTO> user = userService.getUserByEmail(email);

        if (user.isEmpty()) throw new NotFoundException();

        return user.get();
    }
}
