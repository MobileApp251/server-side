package mobile.jira.clonejira.dto.auth;

import lombok.Data;

@Data
public class UserDTO {
    private String uid;
    private String username;
    private String email;
    private String password;
    private boolean is_google_login;
}
