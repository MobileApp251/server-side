package mobile.jira.clonejira.dto.project;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProjectGetDTO {
    private ProjectDTO project;
    private List<ProjectMemberDTO> members;
}
