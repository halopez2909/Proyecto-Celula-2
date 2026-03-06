package com.reto.order.dto;
 
public class StockResponse {
    private boolean available;
 
    // Constructor vacío
    public StockResponse() {}
 
    // Getter y Setter
    public boolean isAvailable() {
        return available;
    }
 
    public void setAvailable(boolean available) {
        this.available = available;
    }
}
 