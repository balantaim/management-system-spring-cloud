package com.martinatanasov.management.system.roles;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends CrudRepository<Role, Long> {

    Role findByName(RoleName name);

}
