package mobile.jira.clonejira.dto;

import lombok.Data;

import java.time.Instant;

@Data
public class ProjectUpdateDTO {
    private String proj_name;
    private String description;
    private Instant startAt;
    private Instant endAt;
    private boolean isDone;
}
