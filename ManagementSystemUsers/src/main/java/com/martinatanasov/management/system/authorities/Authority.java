package com.martinatanasov.management.system.authorities;

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

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(value = EnumType.STRING)
    @Column(nullable = false, length = 20)
    private AuthorityName name;

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
