package com.martinatanasov.management.system.roles;

import com.martinatanasov.management.system.authorities.Authority;
import com.martinatanasov.management.system.users.User;
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
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(value = EnumType.STRING)
    @Column(nullable = false, length = 20)
    private RoleName name;

    @ManyToMany(mappedBy = "roles")
    private Collection<User> users;

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

    public String toString() {
        return "Role: " +
                "Id: " + id +
                ", Name: '" + name.name() + '\'';
    }

}
