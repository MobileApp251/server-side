package mobile.jira.clonejira.dto.project;

import java.time.Instant;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProjectCreateDTO {
    @NotBlank(message = "Project name is required!")
    private String proj_name;

    private String description;

    private Instant startAt;

    private Instant endAt;
}
