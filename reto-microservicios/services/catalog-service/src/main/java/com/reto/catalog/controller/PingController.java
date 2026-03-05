package com.reto.catalog.controller;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Map;
@RestController
@RequestMapping("/ping")
public class PingController {
    @GetMapping
    public ResponseEntity<Map<String, String>> ping() {
        return ResponseEntity.ok(Map.of("service", "catalog-service", "status", "ok"));
    }
}