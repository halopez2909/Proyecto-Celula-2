package com.reto.catalog.messaging;
import java.time.LocalDateTime;
import java.util.List;
public class OrderCreatedEvent {
    private String eventId;
    private String correlationId;
    private Long orderId;
    private Long userId;
    private List<ItemEvent> items;
    private LocalDateTime timestamp;
    public OrderCreatedEvent() {}
    public static class ItemEvent {
        private Long productId;
        private Integer quantity;
        public ItemEvent() {}
        public Long getProductId() { return productId; }
        public void setProductId(Long productId) { this.productId = productId; }
        public Integer getQuantity() { return quantity; }
        public void setQuantity(Integer quantity) { this.quantity = quantity; }
    }
    public String getEventId() { return eventId; }
    public void setEventId(String eventId) { this.eventId = eventId; }
    public String getCorrelationId() { return correlationId; }
    public void setCorrelationId(String correlationId) { this.correlationId = correlationId; }
    public Long getOrderId() { return orderId; }
    public void setOrderId(Long orderId) { this.orderId = orderId; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public List<ItemEvent> getItems() { return items; }
    public void setItems(List<ItemEvent> items) { this.items = items; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
}