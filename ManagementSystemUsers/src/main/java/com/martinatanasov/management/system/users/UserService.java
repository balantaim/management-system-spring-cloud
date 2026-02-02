package com.martinatanasov.management.system.users;

import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;

public interface UserService extends UserDetailsService {

    List<UserDetailsDto> findAll();

    UserDetailsDto createUser(UserRegisterDto userRegisterDto);

    UserDetailsDto findByEmail(String email);

    UserDetailsDto findByEmailAndEnabledTrue(String email);

}
