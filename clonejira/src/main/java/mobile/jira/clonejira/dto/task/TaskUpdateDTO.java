package mobile.jira.clonejira.dto.task;

import lombok.Data;
import mobile.jira.clonejira.enums.TaskPriority;
import mobile.jira.clonejira.enums.TaskStatus;

import java.time.Instant;

@Data
public class TaskUpdateDTO {
    private String task_name;
    private String content;
    private Instant startAt;
    private Instant endAt;
    private TaskStatus status;
    private TaskPriority priority;
}
