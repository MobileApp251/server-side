package mobile.jira.clonejira.dto.project;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import mobile.jira.clonejira.dto.auth.UserDTO;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProjectParticipantGroupDTO {
    private ProjectDTO project;
    private List<UserDTO> members;
}
