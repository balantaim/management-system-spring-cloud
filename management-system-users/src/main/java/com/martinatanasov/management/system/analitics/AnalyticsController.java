package com.martinatanasov.management.system.analitics;

import com.martinatanasov.management.system.users.User;
import com.martinatanasov.management.system.users.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.ResourceAccessException;

@Slf4j
@RequiredArgsConstructor
@RestController
public class AnalyticsController {

    private final AnalyticsService analyticsService;
    private final UserService userService;

    @GetMapping("/api/analytics/{userId}")
    public ResponseEntity<AnalyticsUserDetailsDTO> analytics(@PathVariable String userId) {
        if (userId == null || userId.length() <= 2 || userId.length() > 150) {
            return ResponseEntity.badRequest().build();
        }
        User user = userService.findEntityByUserId(userId);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        try {
            return analyticsService.getUserMetrics(userId)
                    //TODO Use analytics to fill the DTO with data
                    .map(analytics ->
                            ResponseEntity.ok(newAnalyticsUserDetailsDTO(user))
                    )
                    .orElseGet(() -> ResponseEntity.notFound().build());
        } catch (ResourceAccessException ex) {
            log.error("Analytics service is not available right now");
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
        } catch (Exception e) {
            log.error("Internal Server Error");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    private AnalyticsUserDetailsDTO newAnalyticsUserDetailsDTO(User user) {
        return new AnalyticsUserDetailsDTO(user.getId(),
                user.getEmail(),
                user.getFullName(),
                user.getUserId(),
                user.getAccountNonExpired(),
                user.getAccountNonLocked(),
                user.getCredentialsNonExpired(),
                user.getEnabled());
    }

}
