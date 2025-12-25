package mobile.jira.clonejira.dto.task;

import java.time.Instant;

import lombok.Data;
import mobile.jira.clonejira.enums.TaskPriority;
import mobile.jira.clonejira.enums.TaskStatus;

@Data
public class TaskCreateDTO {
    private String task_name;
    private String content;
    private TaskStatus status = TaskStatus.OPEN;
    private TaskPriority priority;
    private Instant startAt;
    private Instant endAt;    
}
