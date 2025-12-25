package mobile.jira.clonejira.dto;

import lombok.Data;

import java.util.List;

@Data
public class TaskAssigneeGroupDTO {
    TaskDTO task;
    List<UserDTO> members;
}
