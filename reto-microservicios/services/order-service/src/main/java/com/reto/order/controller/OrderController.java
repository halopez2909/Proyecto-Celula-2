package com.reto.order.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.reto.order.dto.CreateOrderRequest;
import com.reto.order.entity.OrderEntity;
import com.reto.order.service.OrderService;

@RestController
@RequestMapping("/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping
    public ResponseEntity<OrderEntity> create(
            @RequestBody CreateOrderRequest request,
            @RequestHeader(value = "X-Correlation-Id", required = false) String correlationId) {
        
        // Si no viene correlationId (por si pruebas directo), le ponemos uno genérico
        String cid = (correlationId != null) ? correlationId : "manual-test";
        
        OrderEntity newOrder = orderService.createOrder(request, cid);
        return ResponseEntity.ok(newOrder);
    }
}