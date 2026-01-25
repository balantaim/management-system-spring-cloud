package com.martinatanasov.management.system.users;

import com.martinatanasov.management.system.authorities.Authority;
import com.martinatanasov.management.system.authorities.AuthorityName;
import com.martinatanasov.management.system.mapper.UserMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

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
                .password(userRegisterDto.password())
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

}
