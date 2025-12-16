package mobile.jira.clonejira;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@OpenAPIDefinition(
    servers = {
       @Server(url = "http://localhost:8081", description = "Local Server"),
       @Server(url = "https://server-side-production-79cc.up.railway.app", description = "Production Server")
    }
)
@SecurityScheme(
    name = "Bearer Authentication", // 1. Tên của Security Scheme
    type = SecuritySchemeType.HTTP,  // 2. Loại là HTTP
    bearerFormat = "JWT",           // 3. Format là JWT (tùy chọn)
    scheme = "bearer",              // 4. Cơ chế là bearer (quan trọng)
    description = "Cần có JWT Token hợp lệ để truy cập endpoint này. Thêm 'Bearer ' trước token."
)
@SpringBootApplication
@EnableJpaAuditing
public class ClonejiraApplication {

	public static void main(String[] args) {
		SpringApplication.run(ClonejiraApplication.class, args);
	}

}
