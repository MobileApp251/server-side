package mobile.jira.clonejira.dto;

import java.time.Instant;

import lombok.*;

import mobile.jira.clonejira.enums.*;

@Data
public class TaskDTO {
    private Long task_id;
    private String task_name;
    private String proj_id;
    private String content;
    private Instant createAt;
    private Instant updateAt;
    private TaskStatus status;
}
