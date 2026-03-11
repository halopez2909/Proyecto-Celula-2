package com.reto.order.service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.reto.order.client.CatalogClient;
import com.reto.order.dto.CreateOrderRequest;
import com.reto.order.dto.StockResponse;
import com.reto.order.entity.OrderEntity;
import com.reto.order.entity.OrderItem;
import com.reto.order.exception.StockConflictException;
import com.reto.order.messaging.OrderCreatedEvent;
import com.reto.order.messaging.OrderEventPublisher;
import com.reto.order.repository.OrderRepository;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private CatalogClient catalogClient;

    @Autowired
    private OrderEventPublisher eventPublisher;

    public OrderEntity createOrder(CreateOrderRequest request) {

        // Obtener correlationId desde MDC
        String correlationId = MDC.get("correlationId");

        // Verificar stock en Catalog Service
        for (var item : request.getItems()) {
            Long productId = item.getProductId();
            Integer quantity = item.getQuantity();

            StockResponse stock = catalogClient.checkStock(productId, quantity, correlationId);

            if (stock == null || !stock.isAvailable()) {
                throw new StockConflictException(
                        "No hay stock disponible para el producto: " + productId);
            }
        }

        // Crear orden
        OrderEntity order = new OrderEntity();
        order.setUserId(request.getUserId());
        order.setCorrelationId(correlationId);

        List<OrderItem> items = request.getItems().stream()
                .map(i -> new OrderItem(i.getProductId(), i.getQuantity()))
                .collect(Collectors.toList());

        order.setItems(items);

        OrderEntity saved = orderRepository.save(order);

        // Crear items para el evento
        List<OrderCreatedEvent.ItemEvent> eventItems = saved.getItems().stream()
                .map(i -> new OrderCreatedEvent.ItemEvent(
                        i.getProductId(),
                        i.getQuantity()))
                .collect(Collectors.toList());

        // Crear evento
        OrderCreatedEvent event = new OrderCreatedEvent(
                UUID.randomUUID().toString(),
                correlationId,
                saved.getId(),
                saved.getUserId(),
                eventItems
        );

        // Publicar evento
        eventPublisher.publishOrderCreated(event);

        return saved;
    }
}