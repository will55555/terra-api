package com.terra.api.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;

// config/WebClientConfig.java
@Configuration
public class WebClientConfig {

    @Bean
    public WebClient notionWebClient(
            @Value("${terra.notion.api-key}") String apiKey,
            @Value("${terra.notion.timeout-ms:5000}") int timeoutMs) {

        return WebClient.builder()
                .baseUrl("https://api.notion.com/v1")
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
                .defaultHeader("Notion-Version", "2022-06-28")
                .clientConnector(new ReactorClientHttpConnector(
                        HttpClient.create()
                                .responseTimeout(Duration.ofMillis(timeoutMs))
                ))
                .build();
    }
}
