package com.martinatanasov.management.system.users;

import com.martinatanasov.management.system.authorities.Authority;
import com.martinatanasov.management.system.authorities.AuthorityName;
import com.martinatanasov.management.system.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
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
            //todo exception handling
        }

        Authority authority = Authority.builder()
                .name(AuthorityName.READ)
                .build();

        User createUser = User.builder()
                .email(userRegisterDto.email())
                .fullName(userRegisterDto.fullName())
                .password(passwordEncoder.encode(userRegisterDto.password()))
                .enabled(true)
                .build();

        User createdUser = userRepository.save(createUser);
        return userMapper.userToUserDataDto(createdUser);
    }

    @Override
    public UserDetailsDto findByEmail(String email) {
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isEmpty()) {
            //todo exception handling
        }
        return userMapper.userToUserDataDto(user.get());
    }

    @Override
    public UserDetailsDto findByEmailAndEnabledTrue(String email) {
        Optional<User> user = userRepository.findByEmailAndEnabledTrue(email);
        if (user.isEmpty()) {
            //todo exception handling
        }
        return userMapper.userToUserDataDto(user.get());
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> user = userRepository.findByEmail(username);
        if (user.isEmpty()) {
            throw new UsernameNotFoundException("User not found: " + username);
        }
        return new org.springframework.security.core.userdetails.User(
                user.get().getEmail(),
                user.get().getPassword(),
                user.get().getEnabled(),
                true,
                true,
                true,
                new ArrayList<>());
    }

}
