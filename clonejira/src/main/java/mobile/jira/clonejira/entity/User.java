package mobile.jira.clonejira.entity;

import lombok.*;

import java.util.Set;
import java.util.UUID;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UuidGenerator;
import org.hibernate.type.SqlTypes;

import jakarta.persistence.*;

@Entity
@Table(name= "users")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class User {
    @Id
    @UuidGenerator(style = UuidGenerator.Style.TIME)
    @JdbcTypeCode(SqlTypes.VARCHAR)
    private UUID uid;
    
    @Column(nullable = false)
    private String email;

    private String username;

    private String password;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Participate> participates;

    @OneToMany(mappedBy = "assignee", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Assign> tasks;

    @PrePersist
    private void usernameGenerator() {
        if (uid.toString() == "" || email == "") return;
        String baseName = email.split("@")[0];
        String randomId = uid.toString().substring(0, 8);
        username = baseName + "_" + randomId;
    }
}
