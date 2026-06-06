package com.terra.api.service;

import com.terra.api.model.DashboardSummary;
import com.terra.api.model.NotionQueryResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.List;

// service/NotionService.java
@Service
@RequiredArgsConstructor
public class NotionService {

    private final WebClient notionWebClient;

    @Cacheable(value = "dashboardSummary", key = "'summary'")
    public DashboardSummary getDashboardSummary() {
        // Fire parallel calls with Mono.zip
        Mono<List<Object>> tasksMono = fetchDatabase("tasks-db-id");
        Mono<List<Object>> goalsMono = fetchDatabase("goals-db-id");
        Mono<List<Object>> projectsMono = fetchDatabase("projects-db-id");

        return Mono.zip(tasksMono, goalsMono, projectsMono)
                .map(tuple -> DashboardSummary.builder()
                        .tasks(tuple.getT1())
                        .goals(tuple.getT2())
                        .projects(tuple.getT3())
                        .cachedAt(Instant.now())
                        .build()
                )
                .block();  // block() is correct here — we're in Spring MVC context
    }

    private Mono<List<Object>> fetchDatabase(String databaseId) {
        return notionWebClient.post()
                .uri("/databases/{id}/query", databaseId)
                .retrieve()
                .bodyToMono(NotionQueryResponse.class)
                .map(NotionQueryResponse::getResults);
    }
}