package com.reto.catalog.messaging;
import com.reto.catalog.entity.ProcessedEvent;
import com.reto.catalog.repository.ProcessedEventRepository;
import com.reto.catalog.service.ProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
@Component
public class OrderEventListener {
    private static final Logger log = LoggerFactory.getLogger(OrderEventListener.class);
    @Autowired
    private ProcessedEventRepository processedEventRepository;
    @Autowired
    private ProductService productService;
    @RabbitListener(queues = RabbitMQConfig.QUEUE)
    @Transactional
    public void handleOrderCreated(OrderCreatedEvent event) {
        log.info("[DEV8] Evento recibido - eventId: {} | correlationId: {}", event.getEventId(), event.getCorrelationId());
        if (processedEventRepository.existsByEventId(event.getEventId())) {
            log.warn("[DEV8] Evento duplicado ignorado - eventId: {}", event.getEventId());
            return;
        }
        for (OrderCreatedEvent.ItemEvent item : event.getItems()) {
            log.info("[DEV8] Descontando stock - productId: {} | quantity: {}", item.getProductId(), item.getQuantity());
            productService.descontarStock(item.getProductId(), item.getQuantity());
        }
        ProcessedEvent processed = new ProcessedEvent();
        processed.setEventId(event.getEventId());
        processedEventRepository.save(processed);
        log.info("[DEV8] Evento procesado y stock descontado - eventId: {}", event.getEventId());
    }
}