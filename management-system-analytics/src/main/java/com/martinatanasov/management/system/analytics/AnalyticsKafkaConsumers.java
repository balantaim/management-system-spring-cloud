package com.martinatanasov.management.system.analytics;


import com.martinatanasov.management.system.analytics.events.LoginEvent;
import com.martinatanasov.management.system.analytics.events.RegisterEvent;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.streams.kstream.KStream;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.function.Consumer;


@Slf4j
@Configuration
public class AnalyticsKafkaConsumers {

    @Bean
    public Consumer<KStream<String, RegisterEvent>> userRegisteredConsumer() {
        return stream -> stream.foreach((eventId, event) -> {
            log.info("\n\tReceived UserRegisteredEvent: email: {}, createdDate: {}",
                    event.email(), event.createdDate());

            // TODO: add to DB
        });
    }

    @Bean
    public Consumer<KStream<String, LoginEvent>> userLoginConsumer() {
        return stream -> stream.foreach((eventId, event) -> {
            log.info("\n\tReceived UserLoginEvent: email: {}, createdDate: {}",
                    event.email(), event.createdDate());

            // TODO: add to DB
        });
    }

}
