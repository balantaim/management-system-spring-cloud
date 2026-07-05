package com.martinatanasov.management.system.roles;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.martinatanasov.management.system.authorities.Authority;
import com.martinatanasov.management.system.base.BaseEntity;
import com.martinatanasov.management.system.users.User;
import jakarta.persistence.*;
import lombok.*;

import java.util.Collection;
import java.util.Objects;

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
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        Role role = (Role) o;
        return name == role.name && Objects.equals(version, role.version) && Objects.equals(users, role.users) && Objects.equals(authorities, role.authorities);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + Objects.hashCode(name);
        result = 31 * result + Objects.hashCode(version);
        result = 31 * result + Objects.hashCode(users);
        result = 31 * result + Objects.hashCode(authorities);
        return result;
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
