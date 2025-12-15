package mobile.jira.clonejira.entity.key;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Embeddable
@NoArgsConstructor
@AllArgsConstructor
public class TaskAssigneeId {
    private String uid;
    private String proj_id;
    private Integer task_id;
}
