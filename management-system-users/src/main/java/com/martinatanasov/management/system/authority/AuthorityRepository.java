package com.martinatanasov.management.system.authority;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
interface AuthorityRepository extends CrudRepository<Authority, Long> {

    Optional<Authority> findByName(AuthorityName name);

}
