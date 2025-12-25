package mobile.jira.clonejira.dto.task;

import lombok.Data;
import mobile.jira.clonejira.dto.auth.UserDTO;

import java.util.List;

@Data
public class TaskAssigneeGroupDTO {
    TaskDTO task;
    List<UserDTO> members;
}
