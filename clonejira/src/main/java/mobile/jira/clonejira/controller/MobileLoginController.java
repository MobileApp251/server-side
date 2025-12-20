package mobile.jira.clonejira.controller;

import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import lombok.RequiredArgsConstructor;
import mobile.jira.clonejira.dto.AccessTokenDTO;
import mobile.jira.clonejira.dto.UserDTO;
import mobile.jira.clonejira.security.JwtTokenProvider;
import mobile.jira.clonejira.service.UserService;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;

import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;

@RestController
@RequestMapping("auth")
@RequiredArgsConstructor
public class MobileLoginController {
    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String googleClientId;

    private final UserService userService;

    private final JwtTokenProvider jwtTokenProvider;

    @PostMapping("/login")
    @SecurityRequirements(value = {})
    public ResponseEntity<?> login(
        @RequestBody Map<String, String> requestBody
    ) throws BadRequestException {
        System.out.println(googleClientId);
        String idGGToken = requestBody.get("token");
        System.out.println(idGGToken);

        try {
            System.out.println("-------- idToken verify ----------");
            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), new GsonFactory())
                                            .setAudience(Collections.singleton(googleClientId))
                                            .build();
            GoogleIdToken idToken = verifier.verify(idGGToken);

            System.out.println("-------- idToken ----------");
            System.out.println(idToken);
            System.out.println("-----------------");

            if (idToken == null) return ResponseEntity.status(401).body("Wrong Google Login Token!");

            GoogleIdToken.Payload payload = idToken.getPayload();
            System.out.println("----- Payload ---------");
            System.out.println(payload);

            String email = payload.getEmail();

            System.out.println(email);
            Optional<UserDTO> user = userService.getUserByEmail(email);
            System.out.println(user);
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
            return ResponseEntity.status(401).body("Invalid Token!");
        }
    }

    @GetMapping("/google")
    @SecurityRequirement(name = "")
    public AccessTokenDTO user(
            @AuthenticationPrincipal OAuth2User principal
    ) throws BadRequestException{
        if (principal == null) {
            // Trường hợp này hiếm khi xảy ra nếu config security đúng
            throw new BadRequestException("Không tìm thấy thông tin người dùng Google");
        }

        System.out.println("Login with Google Success");
        System.out.println(principal.getAttributes());

        // 1. Lấy thông tin từ OAuth2User
        String email = principal.getAttribute("email");
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
