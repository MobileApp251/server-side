package mobile.jira.clonejira;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

// @OpenAPIDefinition(
//     servers = {
//        @Server(url = "http://localhost:8081", description = "Local Server"),
//        @Server(url = "https://server-side-production-79cc.up.railway.app", description = "Production Server")
//     }
// )
// @SecurityScheme(
//     name = "bearerAuth", // <--- Tên này phải khớp với trong Controller
//     type = SecuritySchemeType.HTTP,
//     bearerFormat = "JWT",
//     scheme = "bearer"
// )
@SpringBootApplication
@EnableJpaAuditing
public class ClonejiraApplication {

	public static void main(String[] args) {
		SpringApplication.run(ClonejiraApplication.class, args);
	}

}
