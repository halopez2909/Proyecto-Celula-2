package com.reto.order.messaging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class OrderEventPublisher {

    private static final Logger log = LoggerFactory.getLogger(OrderEventPublisher.class);
    private static final String EXCHANGE = "orders.exchange";
    private static final String ROUTING_KEY_CREATED = "order.created";
    private static final String ROUTING_KEY_CANCELLED = "order.cancelled";

    @Autowired
    private RabbitTemplate rabbitTemplate;

    public void publishOrderCreated(OrderCreatedEvent event) {
        rabbitTemplate.convertAndSend(EXCHANGE, ROUTING_KEY_CREATED, event);
        log.info("[DEV7] Evento order.created enviado - eventId: {}", event.getEventId());
    }

    public void publishOrderCancelled(OrderCancelledEvent event) {
        rabbitTemplate.convertAndSend(EXCHANGE, ROUTING_KEY_CANCELLED, event);
        log.info("[DEV1] Evento order.cancelled enviado - eventId: {}", event.getEventId());
    }
}