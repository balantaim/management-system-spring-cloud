package com.martinatanasov.management.system.role;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.martinatanasov.management.system.authority.Authority;
import com.martinatanasov.management.system.base.BaseEntity;
import com.martinatanasov.management.system.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.util.Collection;

@Entity
@Table(name = "roles")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class Role extends BaseEntity {

    /// The current #id value is inherited from the superclass BaseEntity.
    /// Uses Long/BIGSERIAL for compatibility with the development database setup.
    /// PostgreSQL supports SMALLSERIAL, but H2 does not. If the application is
    /// deployed exclusively on PostgreSQL and the expected number of roles is small,
    /// this field could be changed to Short with a Short/SMALLSERIAL column.

    /// H2 doesn't support JdbcTypeCode(SqlTypes.NAMED_ENUM) and Enum type. Use it only on PostgreSQL
    @Enumerated(value = EnumType.STRING)
//    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(nullable = false, length = 20)
    private RoleName name;

    @Version
    @Column(nullable = false)
    private Long version;

    @ManyToMany(mappedBy = "roles")
    private Collection<User> users;

    @JsonIgnore
    @ManyToMany(cascade = CascadeType.PERSIST, fetch = FetchType.EAGER)
    @JoinTable(
            name = "roles_authorities",
            joinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "authority_id", referencedColumnName = "id")
    )
    private Collection<Authority> authorities;

    public Role(RoleName name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Role{" +
                "id=" + getId() +
                ", name=" + name +
                ", version=" + version +
                '}';
    }

}
