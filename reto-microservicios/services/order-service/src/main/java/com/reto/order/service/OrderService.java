package com.reto.order.service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.reto.order.client.CatalogClient;
import com.reto.order.dto.CreateOrderRequest;
import com.reto.order.dto.StockResponse;
import com.reto.order.entity.OrderEntity;
import com.reto.order.entity.OrderItem;
import com.reto.order.entity.OrderStatus;
import com.reto.order.exception.StockConflictException;
import com.reto.order.messaging.OrderCancelledEvent;
import com.reto.order.messaging.OrderCreatedEvent;
import com.reto.order.messaging.OrderEventPublisher;
import com.reto.order.repository.OrderRepository;

@Service
public class OrderService {

    private static final Logger log = LoggerFactory.getLogger(OrderService.class);

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private CatalogClient catalogClient;

    @Autowired
    private OrderEventPublisher eventPublisher;

    public OrderEntity createOrder(CreateOrderRequest request) {

        String correlationId = MDC.get("correlationId");

        for (var item : request.getItems()) {
            Long productId = item.getProductId();
            Integer quantity = item.getQuantity();

            StockResponse stock = catalogClient.checkStock(productId, quantity, correlationId);

            if (stock == null || !stock.isAvailable()) {
                throw new StockConflictException("No hay stock disponible para el producto: " + productId);
            }
        }

        OrderEntity order = new OrderEntity();
        order.setUserId(request.getUserId());
        order.setCorrelationId(correlationId);

        List<OrderItem> items = request.getItems().stream()
                .map(i -> new OrderItem(i.getProductId(), i.getQuantity()))
                .collect(Collectors.toList());
        order.setItems(items);

        OrderEntity saved = orderRepository.save(order);

        List<OrderCreatedEvent.ItemEvent> eventItems = saved.getItems().stream()
                .map(i -> new OrderCreatedEvent.ItemEvent(i.getProductId(), i.getQuantity()))
                .collect(Collectors.toList());

        OrderCreatedEvent event = new OrderCreatedEvent(
                UUID.randomUUID().toString(),
                correlationId,
                saved.getId(),
                saved.getUserId(),
                eventItems
        );

        log.info("[DEV7] Publicando evento order.created - orderId: {} | correlationId: {}", saved.getId(), correlationId);
        eventPublisher.publishOrderCreated(event);
        log.info("[DEV7] Evento publicado exitosamente en exchange: orders.exchange");

        return saved;
    }

    public List<OrderEntity> getOrdersByUserId(Long userId) {
        return orderRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    public OrderEntity getOrderById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Pedido no encontrado con id: " + id));
    }

    public OrderEntity cancelOrder(Long id) {

        String correlationId = MDC.get("correlationId");

        OrderEntity order = orderRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Pedido no encontrado con id: " + id));

        if (order.getStatus() == OrderStatus.CANCELLED) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El pedido ya esta CANCELADO");
        }

        if (order.getStatus() != OrderStatus.CREATED) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Solo se pueden cancelar pedidos en estado CREATED");
        }

        order.setStatus(OrderStatus.CANCELLED);
        OrderEntity saved = orderRepository.save(order);

        List<OrderCancelledEvent.ItemEvent> cancelledItems = saved.getItems().stream()
                .map(i -> new OrderCancelledEvent.ItemEvent(i.getProductId(), i.getQuantity()))
                .collect(Collectors.toList());

        OrderCancelledEvent event = new OrderCancelledEvent(
                UUID.randomUUID().toString(),
                correlationId,
                saved.getId(),
                cancelledItems
        );

        log.info("[DEV1] Publicando evento order.cancelled - orderId: {} | correlationId: {}", saved.getId(), correlationId);
        eventPublisher.publishOrderCancelled(event);
        log.info("[DEV1] Evento order.cancelled publicado exitosamente");

        return saved;
    }
}