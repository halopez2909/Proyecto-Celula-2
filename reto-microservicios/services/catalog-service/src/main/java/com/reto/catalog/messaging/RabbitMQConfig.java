package com.reto.catalog.messaging;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
@Configuration
public class RabbitMQConfig {
    public static final String QUEUE = "catalog.order.created";
    public static final String EXCHANGE = "orders.exchange";
    public static final String ROUTING_KEY = "order.created";
    @Bean
    public Queue catalogQueue() {
        return new Queue(QUEUE, true);
    }
    @Bean
    public TopicExchange ordersExchange() {
        return new TopicExchange(EXCHANGE);
    }
    @Bean
    public Binding binding(Queue catalogQueue, TopicExchange ordersExchange) {
        return BindingBuilder.bind(catalogQueue).to(ordersExchange).with(ROUTING_KEY);
    }
    @Bean
    public Jackson2JsonMessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(messageConverter());
        return template;
    }
}