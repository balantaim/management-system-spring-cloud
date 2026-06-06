package com.martinatanasov.management.system.users;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RefreshScope
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<Page<UserDetailsDto>> getAllUsers(
            @PageableDefault(size = 24, sort = "createdDate", direction = Sort.Direction.DESC) Pageable pageable) {
        return new ResponseEntity<>(userService.findAll(pageable), HttpStatus.OK);
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

    @PreAuthorize("(hasRole('CUSTOMER') && #userId == authentication.principal) || hasRole('ADMIN')")
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

    @PreAuthorize("hasAnyRole('CUSTOMER', 'ADMIN') && #userId == authentication.principal")
    @PostMapping("/update-password/{userId}")
    public ResponseEntity<Void> changeUserPassword(@PathVariable String userId, @Valid @RequestBody UserChangePasswordDto userChangePasswordDto) {
        if (userId != null) {
            userService.changeUserPassword(userChangePasswordDto);
            return new ResponseEntity<>(HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @PreAuthorize("(hasRole('CUSTOMER') && #userId == authentication.principal) || hasRole('ADMIN')")
    @PutMapping("/update-fullname/{userId}")
    public ResponseEntity<Void> changeUserFullName(@PathVariable String userId, @Valid @RequestBody UserChangeFullNameDto userChangeFullNameDto) {
        if (userId != null) {
            userService.changeUserFullName(userChangeFullNameDto.email(), userChangeFullNameDto.fullName());
            return new ResponseEntity<>(HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

}
