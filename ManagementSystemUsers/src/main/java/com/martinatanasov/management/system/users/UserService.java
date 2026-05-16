package com.martinatanasov.management.system.users;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService extends UserDetailsService {

    Page<UserDetailsDto> findAll(Pageable pageable);

    UserDetailsDto createUser(UserRegisterDto userRegisterDto);

    UserDetailsDto findByEmail(String email);

    UserDetailsDto findByUserId(String userId);

    UserDetailsDto findByEmailAndFullEnabled(String email);

    void changeUserPassword(UserChangePasswordDto userChangePasswordDto);

    void changeUserFullName(String email, String newFullName);

}
