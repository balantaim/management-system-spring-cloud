package com.martinatanasov.management.system.users;

import java.util.List;

public interface UserService {

    List<UserDetailsDto> findAll();

    UserDetailsDto createUser(UserRegisterDto userRegisterDto);

    UserDetailsDto findByEmail(String email);

    UserDetailsDto findByEmailAndEnabledTrue(String email);

}
