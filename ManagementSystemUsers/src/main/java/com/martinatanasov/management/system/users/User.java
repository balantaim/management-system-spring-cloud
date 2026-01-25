package com.martinatanasov.management.system.users;

import com.martinatanasov.management.system.roles.Role;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.UUID;

@Entity
@Table(name = "users")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@EntityListeners(AuditingEntityListener.class)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "email", unique = true, nullable = false)
    private String email;

    @Column(name = "full_name")
    private String fullName;

    @Column(nullable = false)
    private String password;

    @Column(name = "user_id", unique = true, nullable = false, updatable = false, length = 36)
    private String userId;

    private Boolean enabled;

    @ManyToMany(cascade = CascadeType.PERSIST, fetch = FetchType.EAGER)
    @JoinTable(
            name = "users_roles",
            joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id")
    )
    private Collection<Role> roles;

    @Column(name = "created_date", updatable = false, nullable = false)
    @CreatedDate
    private LocalDateTime createdDate;

    @Column(name = "modified_date", nullable = false)
    @LastModifiedDate
    private LocalDateTime modifiedDate;

    //Set UUID on create
    @PrePersist
    protected void onCreate() {
        if (userId == null) {
            userId = UUID.randomUUID().toString();
        }
    }

    public String toString() {
        return "User " + "\n\tID: " + id +
                "\n\temail: " + email +
                "\n\tfull name: " + fullName +
                "\n\tUUID: " + userId +
                "\n\tenabled: " + enabled +
                "\n\tRoles: " + roles;
    }

}