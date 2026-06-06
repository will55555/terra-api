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