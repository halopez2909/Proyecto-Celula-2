package com.reto.order.exception;
public class OrderNotFoundException extends RuntimeException {
    public OrderNotFoundException(Long orderId) {
        super("Pedido no encontrado con id: " + orderId);
    }
}