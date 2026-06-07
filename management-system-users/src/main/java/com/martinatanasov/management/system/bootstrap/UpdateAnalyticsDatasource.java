package com.martinatanasov.management.system.bootstrap;

import com.martinatanasov.management.system.users.UserDetailsDto;
import com.martinatanasov.management.system.users.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

/// Update Analytics Datasource only for local profile

@Slf4j
@Profile("local")
@RequiredArgsConstructor
@Component
public class UpdateAnalyticsDatasource implements CommandLineRunner {

    private final UserService userService;

    @Override
    public void run(String[] args) {
        Page<UserDetailsDto> users = userService.findAll(Pageable.ofSize(10));
        users.forEach(user -> log.info("User: {}", user));
        //TODO Kafka Streams
        log.info("\n\tUpdating analytics datasource...");
    }

}
