package com.reto.order.service;
import com.reto.order.client.CatalogClient;
import com.reto.order.dto.CreateOrderRequest;
import com.reto.order.dto.StockResponse;
import com.reto.order.entity.OrderEntity;
import com.reto.order.entity.OrderItem;
import com.reto.order.exception.StockConflictException;
import com.reto.order.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;
@Service
public class OrderService {
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private CatalogClient catalogClient;
    public OrderEntity createOrder(CreateOrderRequest request, String correlationId) {
        for (var item : request.getItems()) {
            Long productId = item.getProductId();
            Integer quantity = item.getQuantity();
            StockResponse stock = catalogClient.checkStock(productId, quantity, correlationId);
            if (stock == null || !stock.isAvailable()) {
                throw new StockConflictException("No hay stock disponible para el producto: " + productId);
            }
        }
        OrderEntity order = new OrderEntity();
        order.setUserId(request.getUserId());
        order.setCorrelationId(correlationId);
        List<OrderItem> items = request.getItems().stream()
                .map(i -> new OrderItem(i.getProductId(), i.getQuantity()))
                .collect(Collectors.toList());
        order.setItems(items);
        return orderRepository.save(order);
    }
}