package com.martinatanasov.management.system.authorities;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthorityRepository extends CrudRepository<Authority, Long> {

    Authority findByName(AuthorityName name);

}
