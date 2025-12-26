package mobile.jira.clonejira.dto.task;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import mobile.jira.clonejira.dto.auth.UserDTO;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TaskAssigneeDTO {
    TaskDTO task;
    UserDTO member;
}
