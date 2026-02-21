package com.martinatanasov.management.system.users;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RefreshScope
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final Environment environment;
    private final UserService userService;

    @GetMapping("/info")
    public String getUsers() {
        String value = environment.getProperty("private.key");
        return "Lallala: " + value;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<List<UserDetailsDto>> getAllUsers() {
        return new ResponseEntity<>(userService.findAll(), HttpStatus.OK);
    }

    // #userId == authentication.principal
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/email/{email}")
    public ResponseEntity<UserDetailsDto> getUserByEmail(@PathVariable String email) {
        if (email != null && !email.isEmpty()) {
            return new ResponseEntity<>(userService.findByEmail(email), HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @PreAuthorize("hasRole('CUSTOMER') && #userId == authentication.principal")
    @GetMapping("/{userId}")
    public ResponseEntity<UserDetailsDto> getUserById(@PathVariable String userId) {
        if (userId != null) {
            return new ResponseEntity<>(userService.findByUserId(userId), HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @PostMapping("/register")
    public ResponseEntity<UserDetailsDto> register(@Valid @RequestBody UserRegisterDto userRegisterDto) {
        UserDetailsDto registeredUser = userService.createUser(userRegisterDto);
        return new ResponseEntity<>(registeredUser, HttpStatus.CREATED);
    }

}
