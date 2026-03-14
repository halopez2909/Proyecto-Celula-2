package com.reto.catalog.messaging;

import java.util.List;

public class OrderCancelledEvent {
    private String eventId;
    private String correlationId;
    private Long orderId;
    private List<ItemEvent> items;

    public static class ItemEvent {
        private Long productId;
        private Integer quantity;

        public Long getProductId() {
            return productId;
        }

        public void setProductId(Long productId) {
            this.productId = productId;
        }

        public Integer getQuantity() {
            return quantity;
        }

        public void setQuantity(Integer quantity) {
            this.quantity = quantity;
        }
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getCorrelationId() {
        return correlationId;
    }

    public void setCorrelationId(String correlationId) {
        this.correlationId = correlationId;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public List<ItemEvent> getItems() {
        return items;
    }

    public void setItems(List<ItemEvent> items) {
        this.items = items;
    }
}