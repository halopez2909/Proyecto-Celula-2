package com.reto.order.messaging;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
@Component
public class OrderEventPublisher {
    private static final Logger log = LoggerFactory.getLogger(OrderEventPublisher.class);
    @Autowired
    private RabbitTemplate rabbitTemplate;
    public void publishOrderCreated(OrderCreatedEvent event) {
        log.info("[DEV7] Publicando evento order.created - orderId: {} | correlationId: {}",
                event.getOrderId(), event.getCorrelationId());
        rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE, RabbitMQConfig.ROUTING_KEY, event);
        log.info("[DEV7] Evento publicado exitosamente en exchange: {}", RabbitMQConfig.EXCHANGE);
    }
}