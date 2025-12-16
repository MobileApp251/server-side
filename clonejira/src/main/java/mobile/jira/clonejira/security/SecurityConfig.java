package mobile.jira.clonejira.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public AuthenticationManager authenticationManager (AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring().requestMatchers(
            "/swagger-ui/**", 
            "/v3/api-docs/**", 
            "/swagger-ui.html"
        );
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // Tắt CSRF
            .csrf(csrf -> csrf.disable())
            
            .authorizeHttpRequests(authorize -> authorize
                // CHO PHÉP CÁC ĐƯỜNG DẪN CÔNG KHAI (WHITELIST)
                .requestMatchers(
                    "/",
                    "/auth/**",
                    
                    // --- CẤU HÌNH CHO SWAGGER (QUAN TRỌNG) ---
                    "/v3/api-docs/**",          // API docs dạng JSON
                    "/swagger-ui/**",           // Giao diện Swagger
                    "/swagger-ui.html",         // Trang chủ Swagger
                    "/swagger-resources/**",    // Tài nguyên cấu hình Swagger
                    "/webjars/**"               // Các file CSS/JS tĩnh của thư viện
                ).permitAll()
                
                // Các request còn lại bắt buộc phải đăng nhập
                .anyRequest().authenticated()
            ).addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);;
            
        // Nếu bạn có dùng JWT Filter, hãy uncomment dòng dưới đây:
        // 

        return http.build();
    }
}