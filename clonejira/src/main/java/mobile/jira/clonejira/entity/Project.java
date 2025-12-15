package mobile.jira.clonejira.entity;

import java.time.Instant;
import java.util.Date;
import java.util.Set;
import java.util.UUID;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UuidGenerator;
import org.hibernate.type.SqlTypes;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
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

    @OneToMany(mappedBy = "project")
    private Set<Task> tasks;

    @OneToMany(mappedBy = "project")
    private Set<Participate> members;
}
