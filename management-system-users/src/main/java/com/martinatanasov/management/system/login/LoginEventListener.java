package com.martinatanasov.management.system.login;

import com.martinatanasov.management.system.analytics.AnalyticsService;
import com.martinatanasov.management.system.analytics.events.LoginEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@RequiredArgsConstructor
@Component
public class LoginEventListener {

    private final AnalyticsService analyticsService;

    @EventListener
    public void onAuthenticationSuccess(AuthenticationSuccessEvent event) {
        Object principal = event.getAuthentication().getPrincipal();
        String email = extractUserEmail(principal);

        analyticsService.sendLoginMessage(new LoginEvent(email, LocalDateTime.now()));
        log.trace("\n\tSuccess Login event dispatched --->>> email: {}", email);
    }

    private String extractUserEmail(Object principal) {
        if (principal instanceof UserDetails userDetails) {
            return userDetails.getUsername();
        }
        return principal.toString();
    }

}
