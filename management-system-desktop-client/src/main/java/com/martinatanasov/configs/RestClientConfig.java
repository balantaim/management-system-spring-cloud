package com.martinatanasov.configs;

import io.micronaut.context.annotation.Factory;
import io.micronaut.context.annotation.Property;
import io.micronaut.http.client.DefaultHttpClientConfiguration;
import io.micronaut.http.client.HttpClient;
import jakarta.inject.Named;
import jakarta.inject.Singleton;

import java.net.MalformedURLException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

@Factory
public class RestClientConfig {

    public static final String CLIENT_PLATFORM_ID = "1001";
    public static final String CLIENT_PLATFORM_ID_HEADER = "Client-Platform-Id";

    @Singleton
    // Set id of the client
    @Named("rest-client")
    public HttpClient httpClient(@Property(name = "app.base-url") String baseUrl) throws MalformedURLException {
        if(baseUrl == null || baseUrl.isEmpty()) {
            throw new RuntimeException("Base URL is not set");
        }

        DefaultHttpClientConfiguration config = new DefaultHttpClientConfiguration();
        config.setDefaultCharset(StandardCharsets.UTF_8);
        config.setReadTimeout(Duration.ofSeconds(30));
        config.setConnectTimeout(Duration.ofSeconds(5));

        return HttpClient.create(URI.create(baseUrl).toURL(), config);
    }

}
