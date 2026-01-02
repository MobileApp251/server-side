package mobile.jira.clonejira.entity;

import jakarta.persistence.*;
import lombok.*;
import mobile.jira.clonejira.enums.NotifyType;
import mobile.jira.clonejira.enums.converter.NotifyTypeConverter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UuidGenerator;
import org.hibernate.type.SqlTypes;
import org.springframework.data.annotation.CreatedDate;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "notifications")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notifications {
    @Id
    @UuidGenerator(style = UuidGenerator.Style.TIME)
    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(nullable = false, name = "noti_id")
    private UUID noti_id;

    private String title;

    private String content;

    @CreatedDate
    @Column(nullable = false, name = "createAt")
    private Instant createdAt;

    @Column(nullable = false, name = "notifyAt")
    private Instant notifyAt;

    @Convert(converter = NotifyTypeConverter.class)
    @Column(nullable = false, name = "notifyType")
    private NotifyType notifyType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "uid")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id")
    private Project project;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "task_id")
    private Task task;
}
