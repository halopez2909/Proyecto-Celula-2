package com.reto.order.messaging;

import java.time.LocalDateTime;
import java.util.List;

public class OrderCancelledEvent {

    private String eventId;
    private String correlationId;
    private Long orderId;
    private List<ItemEvent> items;
    private LocalDateTime timestamp;

    public static class ItemEvent {
        private Long productId;
        private Integer quantity;

        public ItemEvent(Long productId, Integer quantity) {
            this.productId = productId;
            this.quantity = quantity;
        }

        public ItemEvent() {}

        public Long getProductId() { return productId; }
        public void setProductId(Long productId) { this.productId = productId; }
        public Integer getQuantity() { return quantity; }
        public void setQuantity(Integer quantity) { this.quantity = quantity; }
    }

    public OrderCancelledEvent(String eventId, String correlationId, Long orderId, List<ItemEvent> items) {
        this.eventId = eventId;
        this.correlationId = correlationId;
        this.orderId = orderId;
        this.items = items;
        this.timestamp = LocalDateTime.now();
    }

    public OrderCancelledEvent() {}

    public String getEventId() { return eventId; }
    public void setEventId(String eventId) { this.eventId = eventId; }
    public String getCorrelationId() { return correlationId; }
    public void setCorrelationId(String correlationId) { this.correlationId = correlationId; }
    public Long getOrderId() { return orderId; }
    public void setOrderId(Long orderId) { this.orderId = orderId; }
    public List<ItemEvent> getItems() { return items; }
    public void setItems(List<ItemEvent> items) { this.items = items; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
}