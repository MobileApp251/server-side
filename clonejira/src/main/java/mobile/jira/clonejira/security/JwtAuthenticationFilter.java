package mobile.jira.clonejira.security;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.util.StringUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.filter.OncePerRequestFilter;

import mobile.jira.clonejira.service.JwtService;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtTokenProvider jwtProvider;

    @Autowired
    private JwtService userDetailsService;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        try{
            // 1. Lấy JWT từ request
            String token = getJwtFromRequest(request);
            if (!StringUtils.hasText(token)) {
                System.out.println("No token found");
                filterChain.doFilter(request, response);

                return;
            }

            // 2. Xác thực và tải thông tin người dùng
            if (StringUtils.hasText(token) && jwtProvider.validateToken(token)) {

                System.out.println("Token found");
                System.out.println(token);
                String uid = jwtProvider.getUid(token);
                System.out.println(uid);

                // Tải thông tin người dùng để tạo đối tượng Authentication
                UserDetails userDetails = userDetailsService.loadUserByUsername(uid);
                System.out.println(userDetails);

                // Tạo đối tượng Authentication và set vào Security Context
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities()
                );
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authentication);
            } else {
                System.out.println("DEBUG: UserDetails is null for uid: " + token);
            }


        } catch(Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
        System.out.println("Done");
        filterChain.doFilter(request, response);
    }

    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        // Kiểm tra xem header Authorization có chứa "Bearer " hay không
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7); // Trích xuất chuỗi JWT
        }
        return null;
    }
}
