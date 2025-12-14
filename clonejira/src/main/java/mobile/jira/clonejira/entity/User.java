package mobile.jira.clonejira.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UuidGenerator;
import org.hibernate.type.SqlTypes;

import jakarta.persistence.*;

@Entity
@Table(name= "users")
@Data
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

    @PrePersist
    private void usernameGenerator() {
        if (uid.toString() == "" || email == "") return;
        String baseName = email.split("@")[0];
        String randomId = uid.toString().substring(0, 8);
        username = baseName + "_" + randomId;
    }
}
