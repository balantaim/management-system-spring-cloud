package com.martinatanasov.management.system.analytics;


import com.martinatanasov.management.system.analytics.events.LoginEvent;
import com.martinatanasov.management.system.analytics.events.RegisterEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.streams.kstream.KStream;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.function.Consumer;


@Slf4j
@RequiredArgsConstructor
@Configuration
public class AnalyticsKafkaConsumers {

    private final AnalyticsService analyticsService;
    private static final String USER_LOGIN = "user-login";
    private static final String USER_REGISTRATION = "user-registration";

    //We do not send `eventId` from the producer's side, the default value is null
    @Bean
    public Consumer<KStream<String, RegisterEvent>> userRegisteredConsumer() {
        return stream -> stream.foreach((eventId, event) -> {
            log.info("\n\tReceived UserRegisteredEvent: email: {}, platformId: {}, createdDate: {}",
                    event.email(), event.platformId(), event.createdDate());

            analyticsService.storeRegisterEvent(USER_REGISTRATION, event);
        });
    }

    @Bean
    public Consumer<KStream<String, LoginEvent>> userLoginConsumer() {
        return stream -> stream.foreach((eventId, event) -> {
            log.info("\n\tReceived UserLoginEvent: email: {}, platformId: {}, createdDate: {}",
                    event.email(), event.platformId(), event.createdDate());

            analyticsService.storeLoginEvent(USER_LOGIN, event);
        });
    }

}
