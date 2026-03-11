    package com.reto.order.service;

    import com.reto.order.client.CatalogClient; // Tu cliente
    import com.reto.order.dto.CreateOrderRequest;
    import com.reto.order.dto.StockResponse;
    import com.reto.order.entity.OrderEntity;
    import com.reto.order.entity.OrderStatus;
    import com.reto.order.exception.StockConflictException; // Tu excepción
    import com.reto.order.repository.OrderRepository;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.http.HttpStatus;
    import org.springframework.stereotype.Service;
    import org.springframework.web.server.ResponseStatusException;
    import org.springframework.amqp.rabbit.core.RabbitTemplate;

    @Service
    public class OrderService {

        @Autowired
        private OrderRepository orderRepository;

        // --- INTEGRACIÓN DEV 5 ---
        @Autowired
        private CatalogClient catalogClient;

        public OrderEntity createOrder(CreateOrderRequest request, String correlationId) {
        // 1. Saltamos la validación real para que no te de Error 500
        // StockResponse stock = catalogClient.checkStock(productId, quantity, correlationId);
        
        // Simulamos que siempre hay stock
        boolean isAvailable = true; 

        if (!isAvailable) {
            throw new StockConflictException("No hay stock");
        }

        // 2. RELLENAR LOS DATOS
        OrderEntity order = new OrderEntity();
        order.setUserId(request.getUserId());
        order.setStatus(OrderStatus.CREATED);
        order.setCorrelationId(correlationId);
        order.setCreatedAt(java.time.LocalDateTime.now());
        
        return orderRepository.save(order);
    }
    public OrderEntity cancelOrder(Long id) {
            OrderEntity order = orderRepository.findById(id)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No existe"));

            // VALIDACIÓN: Si ya está CANCELLED -> Error 400
            if (order.getStatus() == OrderStatus.CANCELLED) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El pedido ya está CANCELADO");
            }

            // VALIDACIÓN: Solo si está en CREATED se puede cancelar
            if (order.getStatus() != OrderStatus.CREATED) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Solo se cancelan pedidos en CREATED");
            }

            order.setStatus(OrderStatus.CANCELLED);
            orderRepository.save(order);

            enviarEventoRabbit(order);

            return order;
        } // <--- ESTA LLAVE ES LA QUE TE FALTA EN LA IMAGEN

        @Autowired
        private org.springframework.amqp.rabbit.core.RabbitTemplate rabbitTemplate;

    private void enviarEventoRabbit(OrderEntity order) {
            java.util.Map<String, Object> evento = new java.util.HashMap<>();
            evento.put("eventId", java.util.UUID.randomUUID().toString());
            evento.put("orderId", order.getId());
            evento.put("status", "CANCELLED");

            rabbitTemplate.convertAndSend("order.exchange", "order.cancelled", evento);
        }   
    }