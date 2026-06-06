package com.terra.api.controller;

import com.terra.api.model.ApiError;
import com.terra.api.model.DashboardSummary;
import com.terra.api.service.NotionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final NotionService notionService;

    @GetMapping
    public ResponseEntity<?> getDashboard() {
        try {
            DashboardSummary summary = notionService.getDashboardSummary();
            return ResponseEntity.ok(summary);
        } catch (Exception e) {
            ApiError error = ApiError.builder()
                    .message("Failed to fetch dashboard data")
                    .code("DASHBOARD_FETCH_ERROR")
                    .timestamp(Instant.now())
                    .build();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
}
