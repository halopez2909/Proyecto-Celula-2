package com.reto.order.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class PingController {

    private static final Logger log = LoggerFactory.getLogger(PingController.class);

    @GetMapping("/ping")
    public ResponseEntity<Map<String, String>> ping(
            @RequestHeader(value = "X-Correlation-Id", required = false) String correlationId) {

        log.info("[Orders] /ping - X-Correlation-Id: {}", correlationId);

        return ResponseEntity.ok(Map.of(
                "service", "order-service",
                "status", "ok",
                "correlationId", correlationId != null ? correlationId : "N/A"
        ));
    }
}
