package mobile.jira.clonejira.dto.project;

import lombok.Data;
import mobile.jira.clonejira.enums.ProjectRole;

@Data
public class ProjectMemberDTO {
    private String uid;
    private String username;
    private String email;
    private ProjectRole role;
}
