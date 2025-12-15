package mobile.jira.clonejira.entity;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import mobile.jira.clonejira.entity.key.TaskAssigneeId;

@Entity
@Table(name = "assign")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Assign {
    @EmbeddedId
    private TaskAssigneeId id;
}
