package com.martinatanasov.management.system.users;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RefreshScope
@RequiredArgsConstructor
@RestController
//@RequestMapping("/api/users")
public class UserController {

    private final Environment environment;
    private final UserService userService;

    @GetMapping("/info")
    public String getUsers() {
        String value = environment.getProperty("private.key");
        return "Lallala: " + value;
    }

    @GetMapping
    public ResponseEntity<List<UserDetailsDto>> getAllUsers() {
        return new ResponseEntity<>(userService.findAll(), HttpStatus.OK);
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<UserDetailsDto> getUserByEmail(@PathVariable String email) {
        if (email != null && !email.isEmpty()) {
            return new ResponseEntity<>(userService.findByEmail(email), HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @PostMapping("/login")
    public UserDetailsDto login(@Valid @RequestBody UserLoginDto userLoginDto) {
        UserDetailsDto user = userService.findByEmailAndEnabledTrue(userLoginDto.email());
        return user;
    }

    @PostMapping("/register")
    public ResponseEntity<UserDetailsDto> register(@Valid @RequestBody UserRegisterDto userRegisterDto) {
        if (!userRegisterDto.password().equals(userRegisterDto.passwordRepeat())) {
            //todo
        }
        UserDetailsDto registeredUser = userService.createUser(userRegisterDto);
        return new ResponseEntity<>(registeredUser, HttpStatus.CREATED);
    }

}
