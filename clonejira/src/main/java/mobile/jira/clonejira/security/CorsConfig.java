package mobile.jira.clonejira.security;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // Áp dụng cho tất cả các endpoint
                .allowedOriginPatterns("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH") // Cho phép các phương thức này
                .allowedHeaders("*") // Cho phép tất cả các header
                .allowCredentials(true) // Cho phép Cookie/Authorization Header (nếu dùng)
                .maxAge(3600);
    }
}