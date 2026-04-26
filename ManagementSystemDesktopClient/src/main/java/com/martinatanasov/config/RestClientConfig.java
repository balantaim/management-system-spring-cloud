package com.martinatanasov.config;

import io.micronaut.context.annotation.Factory;
import io.micronaut.context.annotation.Property;
import io.micronaut.http.client.DefaultHttpClientConfiguration;
import io.micronaut.http.client.HttpClient;
import jakarta.inject.Singleton;

import java.net.MalformedURLException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

@Factory
public class RestClientConfig {

    @Singleton
    public HttpClient httpClient(
            @Property(name = "app.base-url") String baseUrl) throws MalformedURLException {

        DefaultHttpClientConfiguration config = new DefaultHttpClientConfiguration();
        config.setDefaultCharset(StandardCharsets.UTF_8);
        config.setReadTimeout(Duration.ofSeconds(30));
        config.setConnectTimeout(Duration.ofSeconds(10));

        return HttpClient.create(URI.create(baseUrl).toURL(), config);
    }

}
