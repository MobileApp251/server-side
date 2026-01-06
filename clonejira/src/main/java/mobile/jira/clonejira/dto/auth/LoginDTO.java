package mobile.jira.clonejira.dto.auth;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class LoginDTO {
    @Email(message = "Invalid Email!")
    @NotBlank(message = "Email is required!")
    private String email;

    @NotBlank(message = "Email is required!")
    private String password;

    private String noti_token;
}
