package com.reto.order.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
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
    public ResponseEntity<OrderEntity> create(@RequestBody CreateOrderRequest request) {

        // El CorrelationId ya fue generado por el CorrelationFilter
        // y guardado en MDC automáticamente

        OrderEntity newOrder = orderService.createOrder(request);

        return ResponseEntity.ok(newOrder);
    }
}