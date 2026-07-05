package com.martinatanasov.management.system.users;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.martinatanasov.management.system.base.BaseEntity;
import com.martinatanasov.management.system.roles.Role;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "users")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@EntityListeners(AuditingEntityListener.class)
public class User extends BaseEntity {

    ///#id Value is set from BaseEntity class

    @Column(name = "email", unique = true, nullable = false)
    private String email;

    @Column(name = "full_name", length = 150, nullable = false)
    private String fullName;

    @Column(nullable = false)
    private String password;

    @Column(name = "user_id", unique = true, nullable = false, updatable = false, length = 36)
    private String userId;

    @Builder.Default
    @Column(name = "account_non_expired", nullable = false)
    private Boolean accountNonExpired = true;

    @Builder.Default
    @Column(name = "account_non_locked", nullable = false)
    private Boolean accountNonLocked = true;

    @Builder.Default
    @Column(name = "credentials_non_expired", nullable = false)
    private Boolean credentialsNonExpired = true;

    @Builder.Default
    @Column(name = "enabled", nullable = false)
    private Boolean enabled = true;

    @JsonIgnore
    @ManyToMany(cascade = CascadeType.PERSIST, fetch = FetchType.EAGER)
    @JoinTable(
            name = "users_roles",
            joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id")
    )
    private Collection<Role> roles;

    @Version
    @Column(nullable = false)
    private Long version;

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

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        User user = (User) o;
        return Objects.equals(email, user.email) && Objects.equals(fullName, user.fullName) && Objects.equals(password, user.password) && Objects.equals(userId, user.userId) && Objects.equals(accountNonExpired, user.accountNonExpired) && Objects.equals(accountNonLocked, user.accountNonLocked) && Objects.equals(credentialsNonExpired, user.credentialsNonExpired) && Objects.equals(enabled, user.enabled) && Objects.equals(roles, user.roles) && Objects.equals(version, user.version) && Objects.equals(createdDate, user.createdDate) && Objects.equals(modifiedDate, user.modifiedDate);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + Objects.hashCode(email);
        result = 31 * result + Objects.hashCode(fullName);
        result = 31 * result + Objects.hashCode(password);
        result = 31 * result + Objects.hashCode(userId);
        result = 31 * result + Objects.hashCode(accountNonExpired);
        result = 31 * result + Objects.hashCode(accountNonLocked);
        result = 31 * result + Objects.hashCode(credentialsNonExpired);
        result = 31 * result + Objects.hashCode(enabled);
        result = 31 * result + Objects.hashCode(roles);
        result = 31 * result + Objects.hashCode(version);
        result = 31 * result + Objects.hashCode(createdDate);
        result = 31 * result + Objects.hashCode(modifiedDate);
        return result;
    }

    @Override
    public String toString() {
        return
                "User " + "\n\tID: " + getId() +
                "\n\tEmail: " + email +
                "\n\tFull name: " + fullName +
                "\n\tUUID: " + userId +
                "\n\tEnabled: " + enabled +
                "\n\tPassword: " + password +
                "\n\tRoles: " + roles;
    }

}