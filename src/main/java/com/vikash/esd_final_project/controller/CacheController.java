package com.vikash.esd_final_project.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.CacheManager;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/cache")
public class CacheController {
    private final CacheManager cacheManager;

    @GetMapping("/info")
    public ResponseEntity<Map<String, Object>> getCacheInfo() {
        Map<String, Object> cacheInfo = new HashMap<>();
        Collection<String> cacheNames = cacheManager.getCacheNames();
        cacheInfo.put("cache_names", cacheNames);
        cacheInfo.put("total_caches", cacheNames.size());
        return ResponseEntity.ok(cacheInfo);
    }

    @DeleteMapping("/clear")
    public ResponseEntity<Map<String, String>> clearAllCaches() {
        cacheManager.getCacheNames().forEach(cacheName -> {
            cacheManager.getCache(cacheName).clear();
        });
        Map<String, String> response = new HashMap<>();
        response.put("message", "All caches cleared successfully");
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/clear/{cacheName}")
    public ResponseEntity<Map<String, String>> clearSpecificCache(@PathVariable String cacheName) {
        if (cacheManager.getCache(cacheName) != null) {
            cacheManager.getCache(cacheName).clear();
            Map<String, String> response = new HashMap<>();
            response.put("message", "Cache '" + cacheName + "' cleared successfully");
            return ResponseEntity.ok(response);
        } else {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Cache '" + cacheName + "' not found");
            return ResponseEntity.notFound().build();
        }
    }
}
