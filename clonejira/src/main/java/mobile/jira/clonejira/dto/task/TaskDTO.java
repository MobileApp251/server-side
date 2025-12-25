package mobile.jira.clonejira.dto.task;

import java.time.Instant;

import lombok.*;

import mobile.jira.clonejira.enums.*;

@Data
public class TaskDTO {
    private Integer task_id;
    private String task_name;
    private String proj_id;
    private String content;
    private Instant createAt;
    private Instant updateAt;
    private Instant startAt;
    private Instant endAt;
    private TaskStatus status;
    private TaskPriority priority;
}
