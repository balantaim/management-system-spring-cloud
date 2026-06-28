package com.martinatanasov.management.system.analytics;

import com.martinatanasov.management.system.users.UserAnalyticsDetailsDto;
import com.martinatanasov.management.system.users.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.ResourceAccessException;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
public class AnalyticsController {

    private final AnalyticsService analyticsService;
    private final UserService userService;

    @GetMapping("/api/analytics/{email}")
    public ResponseEntity<UserAndAnalyticsDTO> analytics(@PathVariable String email) {
        if (email == null || email.length() <= 2 || email.length() > 150) {
            return ResponseEntity.badRequest().build();
        }
        UserAnalyticsDetailsDto user = userService.findByEmailAnalyticsUser(email);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        try {
            List<AnalyticsDTO> analytics = analyticsService.getUserMetrics(email);
            UserAndAnalyticsDTO userDTO = newAnalyticsUserDetailsDTO(user, analytics);
            return ResponseEntity.ok(userDTO);
        } catch (ResourceAccessException ex) {
            log.error("Analytics service is not available right now");
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
        } catch (Exception e) {
            log.error("Internal Server Error");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    private UserAndAnalyticsDTO newAnalyticsUserDetailsDTO(UserAnalyticsDetailsDto user, List<AnalyticsDTO> analytics) {
        return new UserAndAnalyticsDTO(
                user.email(),
                user.fullName(),
                user.userId(),
                user.accountNonLocked(),
                user.accountNonExpired(),
                user.credentialsNonExpired(),
                user.enabled(),
                analytics);
    }

}
