package mobile.jira.clonejira.security;

import java.util.Arrays;
import java.util.Collections;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
//... (Các imports khác)

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    // ... (AuthenticationManager Bean)

    // 💡 1. THÊM BEAN CẤU HÌNH CORS NÀY
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        
        // Cho phép truy cập từ mọi nguồn (thường dùng trong môi trường Dev/Test)
        // Trong Production, nên thay * bằng URL Frontend chính xác
        configuration.setAllowedOrigins(Arrays.asList("*")); 
        
        // Cho phép các phương thức HTTP cơ bản
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "HEAD"));
        
        // Cho phép tất cả các Header, bao gồm Authorization
        configuration.setAllowedHeaders(Arrays.asList("*")); 
        
        // Cho phép gửi các thông tin xác thực (ví dụ: cookies, Authorization header)
        configuration.setAllowCredentials(true); 
        
        // Đăng ký cấu hình CORS này cho tất cả các đường dẫn (/**)
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // 💡 2. THÊM CẤU HÌNH CORS VÀO HTTP SECURITY
            .cors(Customizer.withDefaults()) // Kích hoạt CORS sử dụng CorsConfigurationSource đã định nghĩa

            // Tắt CSRF
            .csrf(csrf -> csrf.disable())
            
            // Cấu hình Authorization
            .authorizeHttpRequests(authorize -> authorize
                // Cho phép truy cập Swagger/Auth mà không cần xác thực
                .requestMatchers(
                    "/",
                    "/auth/**",
                    "/auth/login",
                    "/v3/api-docs/**",
                    "/swagger-ui/**",
                    "/swagger-ui.html"
                ).permitAll()

                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
            .oauth2Login(Customizer.withDefaults());
            
        return http.build();
    }
}