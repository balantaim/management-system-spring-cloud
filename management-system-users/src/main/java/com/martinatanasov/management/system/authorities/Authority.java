package com.martinatanasov.management.system.authorities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.martinatanasov.management.system.base.BaseEntity;
import com.martinatanasov.management.system.roles.Role;
import jakarta.persistence.*;
import lombok.*;

import java.util.Collection;
import java.util.Objects;

@Entity
@Table(name = "authorities")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class Authority extends BaseEntity {

    /// The current #id value is inherited from the superclass BaseEntity.
    /// Uses Long/BIGSERIAL for compatibility with the development database setup.
    /// PostgreSQL supports SMALLSERIAL, but H2 does not. If the application is
    /// deployed exclusively on PostgreSQL and the expected number of roles is small,
    /// this field could be changed to Short with a Short/SMALLSERIAL column.

    /// H2 doesn't support JdbcTypeCode(SqlTypes.NAMED_ENUM) and Enum type. Use it only on PostgreSQL
    @Enumerated(value = EnumType.STRING)
//    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(nullable = false, length = 20)
    private AuthorityName name;

    @Version
    @Column(nullable = false)
    private Long version;

    @JsonIgnore
    @ManyToMany(mappedBy = "authorities")
    private Collection<Role> roles;

    public Authority(AuthorityName name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        Authority authority = (Authority) o;
        return name == authority.name && Objects.equals(version, authority.version) && Objects.equals(roles, authority.roles);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + Objects.hashCode(name);
        result = 31 * result + Objects.hashCode(version);
        result = 31 * result + Objects.hashCode(roles);
        return result;
    }

    @Override
    public String toString() {
        return "Authority{" +
                "id=" + getId() +
                ", name=" + name +
                '}';
    }

}
