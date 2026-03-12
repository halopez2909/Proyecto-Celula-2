package com.reto.order.messaging;

import java.time.LocalDateTime;

public class OrderCancelledEvent {

    private String eventId;
    private String correlationId;
    private Long orderId;
    private LocalDateTime timestamp;

    public OrderCancelledEvent(String eventId, String correlationId, Long orderId) {
        this.eventId = eventId;
        this.correlationId = correlationId;
        this.orderId = orderId;
        this.timestamp = LocalDateTime.now();
    }

    public OrderCancelledEvent() {}

    public String getEventId() { return eventId; }
    public void setEventId(String eventId) { this.eventId = eventId; }
    public String getCorrelationId() { return correlationId; }
    public void setCorrelationId(String correlationId) { this.correlationId = correlationId; }
    public Long getOrderId() { return orderId; }
    public void setOrderId(Long orderId) { this.orderId = orderId; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
}