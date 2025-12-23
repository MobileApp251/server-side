package mobile.jira.clonejira.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import mobile.jira.clonejira.enums.ProjectRole;
import mobile.jira.clonejira.enums.converter.ProjectRoleConverter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UuidGenerator;
import org.hibernate.type.SqlTypes;

import java.util.Set;
import java.util.UUID;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProjectMember {
    @Id
    @JdbcTypeCode(SqlTypes.VARCHAR)
    private UUID uid;

    @Column(nullable = false)
    private String email;

    private String username;

    @Convert(converter = ProjectRoleConverter.class)
    @Column(nullable = false)
    private ProjectRole role;
}
