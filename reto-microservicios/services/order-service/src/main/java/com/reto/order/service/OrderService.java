package com.reto.order.service;

import com.reto.order.client.CatalogClient; // Tu cliente
import com.reto.order.dto.CreateOrderRequest;
import com.reto.order.dto.StockResponse;
import com.reto.order.entity.OrderEntity;
import com.reto.order.exception.StockConflictException; // Tu excepción
import com.reto.order.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    // --- INTEGRACIÓN DEV 5 ---
    @Autowired
    private CatalogClient catalogClient;

    public OrderEntity createOrder(CreateOrderRequest request, String correlationId) {
        // 1. Extraemos los datos del primer producto para validar (simplificado)
        Long productId = request.getItems().get(0).getProductId();
        Integer quantity = request.getItems().get(0).getQuantity();

        // 2. REQUISITO: Llamada REST y Logs
        StockResponse stock = catalogClient.checkStock(productId, quantity, correlationId);

        // 3. REQUISITO: Si available = false -> 409
        if (stock == null || !stock.isAvailable()) {
            throw new StockConflictException("No hay stock disponible para el producto: " + productId);
        }

        // 4. REQUISITO: Solo se guarda si hay stock
        OrderEntity order = new OrderEntity();
        // ... (aquí va el código de creación que hizo Juanse - DEV 4)
        
        return orderRepository.save(order);
    }
}