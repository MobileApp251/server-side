package mobile.jira.clonejira.entity;

import jakarta.persistence.*;
import lombok.*;
import mobile.jira.clonejira.entity.key.TaskAssigneeId;

@Entity
@Table(name = "assign")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Assign {
    Assign(TaskAssigneeId id) {
        this.id = id;
    }

    @EmbeddedId
    private TaskAssigneeId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumns({
        @JoinColumn(name = "proj_id", referencedColumnName = "proj_id", insertable = false, updatable = false),
        @JoinColumn(name = "task_id", referencedColumnName = "task_id", insertable = false, updatable = false)
    })
    private Task task;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "uid", insertable = false, updatable = false)
    private User assignee;
}
