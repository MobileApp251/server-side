package mobile.jira.clonejira.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import mobile.jira.clonejira.entity.key.ProjectTaskId;
import mobile.jira.clonejira.enums.TaskPriority;
import mobile.jira.clonejira.enums.TaskStatus;
import mobile.jira.clonejira.enums.converter.TaskPriorityConverter;
import mobile.jira.clonejira.enums.converter.TaskStatusConverter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;

@Entity
@Data
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaskAssignee {
    @EmbeddedId
    private ProjectTaskId id;

    @Column(nullable = false)
    private String task_name;

    @Column(nullable = false)
    private String content;

    @Column(name = "startAt")
    private Instant startAt;

    @Column(name = "endAt")
    private Instant endAt;

    @Convert(converter = TaskStatusConverter.class)
    @Column(nullable = false)
    private TaskStatus status;

    @Convert(converter = TaskPriorityConverter.class)
    @Column(nullable = false)
    private TaskPriority priority;
}
