package mobile.jira.clonejira.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import mobile.jira.clonejira.dto.AccessTokenDTO;
import mobile.jira.clonejira.dto.UserDTO;
import mobile.jira.clonejira.security.JwtTokenProvider;
import mobile.jira.clonejira.service.UserService;

import java.util.Collections;
import java.util.Optional;

import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

@RestController
@RequestMapping("auth")
@RequiredArgsConstructor
@SecurityRequirement(name = "")
public class MobileLoginController {
    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String googleClientId;

    private final UserService userService;

    private final JwtTokenProvider jwtTokenProvider;

    @PostMapping("/login")
    public ResponseEntity<AccessTokenDTO> login(
        @RequestHeader("Authorization") String authHeader
    ) throws BadRequestException {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new BadRequestException("Invalid Token!");
        }

        String idGGToken = authHeader.substring(7);

        try {
            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), new GsonFactory())
                                            .setAudience(Collections.singleton(googleClientId))
                                            .build();
            GoogleIdToken idToken = verifier.verify(idGGToken);
            if (idToken == null) throw new BadRequestException("Invalid Token!");

            GoogleIdToken.Payload payload = idToken.getPayload();

            String email = payload.getEmail();

            Optional<UserDTO> user = userService.getUserByEmail(email);
            UserDTO userRes;
            if (user.isEmpty()) {
                userRes = userService.createUser(email);
            }
            else {
                userRes = user.get();
            }
            UsernamePasswordAuthenticationToken 
            authentication = new UsernamePasswordAuthenticationToken(
                userRes.getUid(), null, Collections.emptyList()
            );

            String jwtToken = jwtTokenProvider.generateToken(authentication);
            
            return ResponseEntity.ok(new AccessTokenDTO(jwtToken));
        } catch (Exception e) {
            throw new BadRequestException("Authentication failed!");
        }
    }
}
