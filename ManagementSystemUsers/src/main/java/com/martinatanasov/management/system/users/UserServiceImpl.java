package com.martinatanasov.management.system.users;

import com.martinatanasov.management.system.exception.ResourceNotFoundException;
import com.martinatanasov.management.system.exception.UserAlreadyExistsException;
import com.martinatanasov.management.system.mapper.UserMapper;
import com.martinatanasov.management.system.roles.Role;
import com.martinatanasov.management.system.roles.RoleName;
import com.martinatanasov.management.system.roles.RoleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserMapper userMapper;
    private final BCryptPasswordEncoder passwordEncoder;

    @Override
    public List<UserDetailsDto> findAll() {
        Iterable<User> users = userRepository.findAll();
        for (User user : users) {
            log.info("\n\tUser found: {}", user);
        }
        return StreamSupport.stream(users.spliterator(), false)
                .map(userMapper::userToUserDataDto)
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public UserDetailsDto createUser(UserRegisterDto userRegisterDto) {
        if (userRepository.findByEmail(userRegisterDto.email()).isPresent()) {
            throw new UserAlreadyExistsException("User with email " + userRegisterDto.email() + " already exists");
        }
        //Get base customer role
        Role customerRole = roleRepository.findByName(RoleName.CUSTOMER)
                .orElseThrow(() -> new RuntimeException("Role CUSTOMER not found"));
        //Create set customer's roles to collection
        Collection<Role> customerRoles = new ArrayList<>();
        customerRoles.add(customerRole);
        //Create customer user with roles
        User createUser = User.builder()
                .email(userRegisterDto.email())
                .fullName(userRegisterDto.fullName())
                .password(passwordEncoder.encode(userRegisterDto.password()))
                .enabled(true)
                .roles(customerRoles)
                .build();
        //Get saved user from the database
        User createdUser = userRepository.save(createUser);
        log.info("User registered: {}", createdUser);
        return userMapper.userToUserDataDto(createdUser);
    }

    @Override
    public UserDetailsDto findByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return userMapper.userToUserDataDto(user);
    }

    @Override
    public UserDetailsDto findByUserId(String userId) {
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return userMapper.userToUserDataDto(user);
    }

    @Override
    public UserDetailsDto findByEmailAndFullEnabled(String email) {
        User user = userRepository.findByEmailAndEnabledTrueAndAccountNonExpiredTrueAndCredentialsNonExpiredTrueAndAccountNonLockedTrue(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return userMapper.userToUserDataDto(user);
    }

    @Override
    public UserDetails loadUserByUsername(@NonNull String username) {
        User user = userRepository.findByEmailAndEnabledTrueAndAccountNonExpiredTrueAndCredentialsNonExpiredTrueAndAccountNonLockedTrue(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
        //Get all authorities
        List<GrantedAuthority> authorities = user.getRoles()
                .stream()
                .map(role -> new SimpleGrantedAuthority(role.getName().name()))
                .collect(Collectors.toList());

        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                user.getEnabled(),
                user.getAccountNonExpired(),
                user.getCredentialsNonExpired(),
                user.getAccountNonLocked(),
                authorities);
    }

}
