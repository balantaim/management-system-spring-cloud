package com.martinatanasov.management.system.users;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends CrudRepository<User, Long> {

    Optional<User> findByEmail(String email);

    Optional<User> findByUserId(String email);

    Optional<User> findByEmailAndEnabledTrue(String email);

    Optional<User> findByEmailAndEnabledTrueAndAccountNonExpiredTrueAndCredentialsNonExpiredTrueAndAccountNonLockedTrue(String email);

}