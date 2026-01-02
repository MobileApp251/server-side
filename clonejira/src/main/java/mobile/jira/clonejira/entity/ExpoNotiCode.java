package mobile.jira.clonejira.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "expo_noti_code")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExpoNotiCode {
    @Id
    private String code;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "uid")
    private User user;
}
