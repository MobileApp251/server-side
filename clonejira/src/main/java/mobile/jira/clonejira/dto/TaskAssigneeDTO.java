package mobile.jira.clonejira.dto;

import lombok.Data;

import java.util.List;

@Data
public class TaskAssigneeDTO {
    TaskDTO task;
    UserDTO member;
}
