package com.martinatanasov.management.system.authorities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.martinatanasov.management.system.roles.Role;
import jakarta.persistence.*;
import lombok.*;

import java.util.Collection;

@Entity
@Table(name = "authorities")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class Authority {

    /// Uses Long/BIGSERIAL for compatibility with the development database setup.
    /// PostgreSQL supports SMALLSERIAL, but H2 does not. If the application is
    /// deployed exclusively on PostgreSQL and the expected number of roles is small,
    /// this field could be changed to Short with a Short/SMALLSERIAL column.
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /// H2 doesn't support JdbcTypeCode(SqlTypes.NAMED_ENUM) and Enum type. Use it only on PostgreSQL
    @Enumerated(value = EnumType.STRING)
//    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(nullable = false, length = 20)
    private AuthorityName name;

    @JsonIgnore
    @ManyToMany(mappedBy = "authorities")
    private Collection<Role> roles;

    public Authority(AuthorityName name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Authority: " +
                "id: " + id +
                " name: " + name;
    }

}
