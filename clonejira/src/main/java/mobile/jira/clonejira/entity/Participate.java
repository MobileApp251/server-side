package mobile.jira.clonejira.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.*;
import mobile.jira.clonejira.entity.key.ProjectMemberId;
import mobile.jira.clonejira.enums.ProjectRole;
import mobile.jira.clonejira.enums.converter.ProjectRoleConverter;

@Entity
@Getter
@Setter
@Table(name = "participate")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Participate {
    @EmbeddedId
    private ProjectMemberId id;

    @Convert(converter = ProjectRoleConverter.class)
    @Column(nullable = false)
    private ProjectRole role;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "proj_id", insertable = false, updatable = false)
    private Project project;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "uid", insertable = false, updatable = false)
    private User member;

    public Participate(ProjectMemberId projectMemberId, ProjectRole projectRole) {
        this.id = projectMemberId;
        this.role = projectRole;
    }
}
