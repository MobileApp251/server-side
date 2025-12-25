package mobile.jira.clonejira.dto.project;

import lombok.Data;

import java.util.List;

@Data
public class ProjectGetDTO {
    private ProjectDTO project;
    private List<ProjectMemberDTO> members;
}
