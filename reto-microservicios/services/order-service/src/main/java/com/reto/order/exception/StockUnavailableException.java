package com.reto.order.exception;
public class StockUnavailableException extends RuntimeException {
    public StockUnavailableException(Long productId) {
        super("Stock insuficiente para el producto: " + productId);
    }
}