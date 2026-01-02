package com.martinatanasov.management.system.users;

import lombok.RequiredArgsConstructor;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RefreshScope
@RequiredArgsConstructor
@RestController
public class UserController {

    private final Environment environment;

    @GetMapping("/users")
    public String getUsers() {
        String value = environment.getProperty("private.key");
        return "Lallala: " + value;
    }

}
