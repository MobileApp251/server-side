package mobile.jira.clonejira.dto.task;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import mobile.jira.clonejira.dto.auth.UserDTO;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskAssigneeGroupDTO {
    TaskDTO task;
    List<UserDTO> members;
}
