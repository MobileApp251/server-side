package mobile.jira.clonejira.entity;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UuidGenerator;
import org.hibernate.type.SqlTypes;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Getter
@Setter
@Table(name = "projects")
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Project {
    @Id
    @UuidGenerator(style = UuidGenerator.Style.TIME)
    @JdbcTypeCode(SqlTypes.VARCHAR)
    private UUID proj_id;

    @Column(nullable = false)
    private String proj_name;

    @Column(nullable = true)
    private String description;

    @CreatedDate
    @Column(name = "createAt", nullable = false, updatable = false)
    private Instant createAt;

    @LastModifiedDate
    @Column(name = "updateAt")
    private Instant updateAt;

    @Column(name = "startAt")
    private Instant startAt;

    @Column(name = "endAt")
    private Instant endAt;

    @Column(name = "isDone")
    private boolean isDone;

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Task> tasks;

    @OneToMany(mappedBy = "project",  cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Participate> members;
}
