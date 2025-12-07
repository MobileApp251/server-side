package mobile.jira.clonejira.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // Tắt CSRF nếu bạn không dùng Token CSRF (phổ biến khi dùng REST APIs)
            .csrf(csrf -> csrf.disable())
            
            // Cấu hình Authorization
            .authorizeHttpRequests(authorize -> authorize
                // **TẠM TẮT AUTHENTICATION BẰNG CÁCH NÀY**
                // Cho phép TẤT CẢ các request truy cập mà KHÔNG cần xác thực
                

                .requestMatchers(
                    "/api/tasks",
                    "/api/tasks/**",
                    "/v3/api-docs/**",
                    "/swagger-ui/**",
                    "/swagger-ui.html"
                ).permitAll()

                .anyRequest().authenticated()
            )
            
            // Tắt form login mặc định nếu không cần
            .formLogin(form -> form.disable())
            
            // Tắt http basic mặc định nếu không cần
            .httpBasic(httpBasic -> httpBasic.disable());
            
        return http.build();
    }
}