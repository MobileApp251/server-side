package mobile.jira.clonejira;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@OpenAPIDefinition(
    servers = {
       @Server(url = "http://localhost:8081", description = "Local Server"),
       @Server(url = "https://server-side-production-0171.up.railway.app", description = "Production Server")
    }
)
@SpringBootApplication
@EnableJpaAuditing
public class ClonejiraApplication {

	public static void main(String[] args) {
		SpringApplication.run(ClonejiraApplication.class, args);
	}

}
