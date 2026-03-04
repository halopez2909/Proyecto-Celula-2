package com.reto.order.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.reto.order.dto.CreateOrderRequest;
import com.reto.order.entity.OrderEntity;
import com.reto.order.entity.OrderItem;
import com.reto.order.repository.OrderRepository;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    public OrderEntity createOrder(CreateOrderRequest request, String correlationId) {
        OrderEntity order = new OrderEntity();
        order.setUserId(request.getUserId());
        order.setCorrelationId(correlationId); // Guardamos el ID de trazabilidad

        // Convertimos los items del DTO a entidades OrderItem
        List<OrderItem> items = request.getItems().stream()
                .map(itemDto -> new OrderItem(itemDto.getProductId(), itemDto.getQuantity()))
                .collect(Collectors.toList());

        order.setItems(items);

        // Al guardar la orden, se guardan los items por el CascadeType.ALL
        return orderRepository.save(order);
    }
}