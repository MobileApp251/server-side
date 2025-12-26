package mobile.jira.clonejira.dto.project;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import mobile.jira.clonejira.dto.auth.UserDTO;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProjectParticipantDTO {
    private ProjectDTO project;
    private UserDTO member;
}
