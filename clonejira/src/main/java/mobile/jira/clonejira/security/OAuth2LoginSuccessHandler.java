package mobile.jira.clonejira.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collections;
import java.util.Optional;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;
import mobile.jira.clonejira.dto.auth.UserDTO;
import mobile.jira.clonejira.service.UserService;

@Component
public class OAuth2LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    @Autowired
    private JwtTokenProvider jwtTokenProvider; // Service tạo JWT của bạn
    
    @Autowired
    private UserService userService;

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) throws IOException, ServletException {
        
        // 1. Lấy thông tin user từ Google
        OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;
        String email = oauthToken.getPrincipal().getAttribute("email");
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
        // 2. Tạo Access Token hệ thống
        String accessToken = jwtTokenProvider.generateToken(auth);

        HttpSession session = request.getSession(false);
        String targetUrl = null;

        if (session != null) {
            targetUrl = (String) session.getAttribute("APP_REDIRECT_URI");
            System.out.println("client "+targetUrl);
            // Xóa khỏi session sau khi lấy để dọn dẹp
            session.removeAttribute("APP_REDIRECT_URI");
            if (targetUrl == null || targetUrl == "") {
                targetUrl = "/auth/google";
            }
        }

        // 3. Redirect sang trang success (Token nằm trên URL Param)
        // URL này là URL của backend luôn, ví dụ: http://api.backend.com/login-success
        targetUrl = targetUrl + "?accessToken=" + accessToken;
        
        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }
}