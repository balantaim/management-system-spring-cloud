package com.martinatanasov.management.system.user;


import com.martinatanasov.management.system.analytics.AnalyticsService;
import com.martinatanasov.management.system.analytics.event.RegisterEvent;
import jakarta.validation.Valid;
import lombok.AccessLevel;
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
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
@RestController
@RequestMapping("/api/users")
class UserController {

    private final UserService userService;
    private final AnalyticsService analyticsService;
    private static final int DEFAULT_PAGE_SIZE = 24;
    private static final int MAX_PAGE_SIZE = 60;
    private static final int MIN_PAGE_SIZE = 0;

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<Page<UserDetailsDto>> getAllUsers(
            @PageableDefault(size = DEFAULT_PAGE_SIZE, sort = "createdDate", direction = Sort.Direction.DESC) Pageable pageable) {
        if (validatePageable(pageable)) {
            return new ResponseEntity<>(userService.findAll(pageable), HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
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
    public ResponseEntity<UserDetailsDto> register(@RequestHeader(value = "Client-Platform-Id") String clientId,
            @Valid @RequestBody UserRegisterDto userRegisterDto) {
        UserDetailsDto registeredUser = userService.createUser(userRegisterDto);
        //Send registration message to analytics service
        analyticsService.sendRegisterMessage(new RegisterEvent(
                registeredUser.email(), clientId, registeredUser.createdDate()
        ));
        return new ResponseEntity<>(registeredUser, HttpStatus.CREATED);
    }

    @PreAuthorize("(hasRole('CUSTOMER') && #userId == authentication.principal) || hasRole('ADMIN')")
    @PostMapping("/update-password/{userId}")
    public ResponseEntity<UserDetailsDto> changeUserPassword(@PathVariable String userId, @Valid @RequestBody UserChangePasswordDto userChangePasswordDto) {
        if (userId != null) {
            return new ResponseEntity<>(userService.changeUserPassword(userId, userChangePasswordDto), HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @PreAuthorize("(hasRole('CUSTOMER') && #userId == authentication.principal) || hasRole('ADMIN')")
    @PutMapping("/update-fullname/{userId}")
    public ResponseEntity<UserDetailsDto> changeUserFullName(@PathVariable String userId, @Valid @RequestBody UserChangeFullNameDto userChangeFullNameDto) {
        if (userId != null) {
            return new ResponseEntity<>(userService.changeUserFullName(userId, userChangeFullNameDto.fullName()), HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    private static boolean validatePageable(Pageable pageable) {
        return pageable.getPageSize() <= MAX_PAGE_SIZE &&
                pageable.getPageNumber() >= MIN_PAGE_SIZE;
    }

}
