package com.martinatanasov.management.system.auth;

import com.martinatanasov.management.system.security.JwtService;
import com.martinatanasov.management.system.users.UserDetailsDto;
import com.martinatanasov.management.system.users.UserService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/auth")
@RestController
public class AuthController {

    private final JwtService jwtService;
    private final UserService userService;

    @PostMapping("/refresh")
    public ResponseEntity<Map<String, Object>> refresh(@RequestBody Map<String, String> body) {
        String refreshToken = body.get("refresh_token");
        try {
            Claims claims = jwtService.extractAllClaims(refreshToken);

            // Reject if it's not a refresh token
            if (!jwtService.isRefreshToken(claims)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Invalid token type"));
            }

            // Reject if expired
            if (jwtService.isTokenExpired(claims)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Refresh token expired, please log in again"));
            }

            String subject = jwtService.extractSubjectFromClaims(claims);

            // Re-fetch current roles from DB - never reuse old claims
            UserDetailsDto userDetailsDto = userService.findByUserIdAndFullEnabled(subject);
            List<String> permissions = userDetailsDto.roles()
                    .stream()
                    .flatMap(role -> Stream.concat(
                            Stream.of("ROLE_" + role.getName().name()),
                            role.getAuthorities().stream().map(a -> a.getName().name())
                    ))
                    .toList();

            // Issue new access token only - refresh token stays the same until it expires
            String newAccessToken = jwtService.generateAccessToken(subject, permissions);

            return ResponseEntity.ok(Map.of("access_token", newAccessToken));

        } catch (JwtException e) {
            log.error("Invalid refresh token");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Invalid refresh token"));
        }
    }

}
