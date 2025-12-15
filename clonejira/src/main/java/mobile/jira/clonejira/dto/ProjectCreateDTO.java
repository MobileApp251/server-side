package mobile.jira.clonejira.dto;

import java.time.Instant;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ProjectCreateDTO {
    @NotBlank(message = "Project name is required!")
    private String proj_name;

    private String description;

    private Instant startAt;

    private Instant endAt;
}
