package mobile.jira.clonejira.controller;

import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;

import org.apache.coyote.BadRequestException;
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import lombok.RequiredArgsConstructor;
import mobile.jira.clonejira.dto.AccessTokenDTO;
import mobile.jira.clonejira.dto.UserDTO;
import mobile.jira.clonejira.security.JwtTokenProvider;
import mobile.jira.clonejira.service.UserService;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    private final JwtTokenProvider jwtTokenProvider;

    @GetMapping()
    public UserDTO getUserDetail(
        @RequestParam String email
    ) throws NotFoundException, BadRequestException {
        Optional<UserDTO> user = userService.getUserByEmail(email);

        if (user.isEmpty()) throw new NotFoundException();

        return user.get();
    }

    @GetMapping("/login")
    public AccessTokenDTO user(
        @AuthenticationPrincipal OAuth2User princtiplUser
    ) throws BadRequestException{
        Map<String, Object> loginRes = princtiplUser.getAttributes();
        String email = (String) loginRes.get("email");
        Optional<UserDTO> user = userService.getUserByEmail(email);
        UserDTO userRes;
        if (user.isEmpty()) {
            userRes = userService.createUser(email);
        }
        else {
            userRes = user.get();
        }

        Authentication auth = new UsernamePasswordAuthenticationToken(
                userRes.getUid(),
                null,
                Collections.emptyList()
        );

        String token = jwtTokenProvider.generateToken(auth);
        return new AccessTokenDTO(token);
    }
}
