package com.terra.api.controller;

import com.terra.api.service.CacheInvalidationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/webhooks")
@RequiredArgsConstructor
public class WebhookController {

    private final CacheInvalidationService cacheInvalidationService;

    @PostMapping("/notion")
    public ResponseEntity<Map<String, String>> handleNotionWebhook(@RequestBody Map<String, Object> payload) {
        // TODO: Validate webhook signature from X-Notion-Signature header
        cacheInvalidationService.invalidateDashboardCache();
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(Map.of("status", "received"));
    }
}