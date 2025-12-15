package mobile.jira.clonejira.dto;

import java.time.Instant;

import lombok.Data;

@Data
public class ProjectDTO {
    private String proj_id;
    private String proj_name;
    private String description;
    private Instant createAt;
    private Instant updateAt;
    private Instant startAt;
    private Instant endAt;
}
