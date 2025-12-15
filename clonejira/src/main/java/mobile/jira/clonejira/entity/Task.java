package mobile.jira.clonejira.entity;

import java.time.Instant;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.*;
import lombok.*;
import mobile.jira.clonejira.entity.key.ProjectTaskId;
import mobile.jira.clonejira.enums.TaskStatus;
import mobile.jira.clonejira.enums.converter.TaskStatusConverter;

@Entity
@Data
@Table(name = "tasks")
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Task {
    
    @EmbeddedId
    private ProjectTaskId id;

    @Column(nullable = false)
    private String task_name;

    @Column(nullable = false)
    private String content;

    @CreatedDate
    @Column(name = "createAt", updatable = false)
    private Instant createAt;

    @LastModifiedDate
    @Column(name = "updateAt")
    private Instant updateAt;

    @Column(name = "startAt")
    private Instant startAt;

    @Column(name = "endAt")
    private Instant endAt;

    @Convert(converter = TaskStatusConverter.class)
    @Column(nullable = false)
    private TaskStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "proj_id", insertable = false, updatable = false)
    private Project project;
}
