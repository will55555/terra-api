package com.terra.api.service;

import com.terra.api.model.DashboardSummary;
import com.terra.api.model.NotionQueryResponse;
import lombok.RequiredArgsConstructor;
    import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.Collections;
import java.util.List;

// service/NotionService.java
@Service
@RequiredArgsConstructor
@Slf4j
public class NotionService {

    private final WebClient notionWebClient;

    @Cacheable(value = "dashboardSummary", key = "'summary'")
    public DashboardSummary getDashboardSummary() {
        try {
            // Fire parallel calls with Mono.zip
            Mono<List<Object>> tasksMono = fetchDatabase("6002b34b-8bff-456a-aa43-4eed8f643dcd");
            Mono<List<Object>> goalsMono = fetchDatabase("4b271d70-2037-4974-b573-ce0813bc414b");
            Mono<List<Object>> projectsMono = fetchDatabase("cf8f7353-f469-44bf-bbf2-56e1dfa280f3");

            return Mono.zip(tasksMono, goalsMono, projectsMono)
                    .map(tuple -> DashboardSummary.builder()
                            .tasks(tuple.getT1())
                            .goals(tuple.getT2())
                            .projects(tuple.getT3())
                            .cachedAt(Instant.now())
                            .build()
                    )
                    .onErrorResume(e -> {
                        log.error("Error fetching dashboard data from Notion", e);
                        return Mono.empty();
                    })
                    .block();  // block() is correct here — we're in Spring MVC context
        } catch (Exception e) {
            log.error("Failed to fetch dashboard summary", e);
            throw e;
        }
    }

    private Mono<List<Object>> fetchDatabase(String databaseId) {
        return notionWebClient.post()
                .uri("/databases/{id}/query", databaseId)
                .retrieve()
                .onStatus(status -> !status.is2xxSuccessful(), 
                    response -> logErrorResponse(response, databaseId))
                .bodyToMono(NotionQueryResponse.class)
                .map(NotionQueryResponse::getResults)
                .onErrorResume(e -> {
                    log.warn("Failed to fetch database {}: {}", databaseId, e.getMessage());
                    return Mono.just(Collections.emptyList());
                });
    }

    private Mono<? extends Throwable> logErrorResponse(ClientResponse response, String databaseId) {
        return response.bodyToMono(String.class)
                .defaultIfEmpty("")
                .flatMap(body -> {
                    String errorMsg = String.format(
                        "Notion API error for database %s: status=%d, body=%s",
                        databaseId, response.statusCode().value(), body
                    );
                    log.error(errorMsg);
                    return Mono.error(new RuntimeException(errorMsg));
                });
    }
}