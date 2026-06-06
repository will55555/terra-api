package com.terra.api.service;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CacheInvalidationService {

    private final CacheManager cacheManager;

    public void invalidateDashboardCache() {
        if (cacheManager.getCache("dashboardSummary") != null) {
            cacheManager.getCache("dashboardSummary").clear();
        }
    }
}
