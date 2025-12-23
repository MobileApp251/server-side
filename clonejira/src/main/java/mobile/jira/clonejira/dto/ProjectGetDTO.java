package mobile.jira.clonejira.dto;

import lombok.Data;
import mobile.jira.clonejira.entity.ProjectMember;

import java.util.List;

@Data
public class ProjectGetDTO {
    private ProjectDTO project;
    private List<ProjectMemberDTO> members;
}
