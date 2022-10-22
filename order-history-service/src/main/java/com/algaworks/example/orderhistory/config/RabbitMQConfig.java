package com.algaworks.example.orderhistory.config;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.HashMap;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.HeadersExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String ORDER_EXCHANGE_NAME = "order.v1.events";
    
    private static final String ORDER_EVENT_HEADER_NAME = "order-event-type";

    private static final String ORDER_PAID_EVENT_HEADER_VALUE = "order-paid";
    private static final String ORDER_CANCEL_EVENT_HEADER_VALUE = "order-cancel";
    
    public static final String HISTORY_ORDER_QUEUE_NAME = "history.v1.on-order-event.generate";
    
    @Bean
    public Queue queueHistoryOrder() {
        return new Queue(HISTORY_ORDER_QUEUE_NAME);
    }
    
    @Bean
    public RabbitAdmin rabbitAdmin(ConnectionFactory connectionFactory) {
        return new RabbitAdmin(connectionFactory);
    }

    @Bean
    public ApplicationListener<ApplicationReadyEvent> applicationReadyEventApplicationListener(RabbitAdmin rabbitAdmin) {
        return event -> rabbitAdmin.initialize();
    }

    @Bean
    public Jackson2JsonMessageConverter messageConverter(ObjectMapper objectMapper) {
        objectMapper.findAndRegisterModules();
        return new Jackson2JsonMessageConverter(objectMapper);
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory,Jackson2JsonMessageConverter messageConverter) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(messageConverter);
        return rabbitTemplate;
    }
    
    @Bean
    public Binding bindingOnPaidOrderHistory() {
        Queue queue = new Queue(HISTORY_ORDER_QUEUE_NAME);
        HeadersExchange exchange = new HeadersExchange(ORDER_EXCHANGE_NAME);
        HashMap<String, Object> headers = new HashMap<>();
        headers.put(ORDER_EVENT_HEADER_NAME, ORDER_PAID_EVENT_HEADER_VALUE);
        return BindingBuilder.bind(queue).to(exchange).whereAny(headers).match();
    }
    
    @Bean
    public Binding bindingOnCancelOrderHistory() {
        Queue queue = new Queue(HISTORY_ORDER_QUEUE_NAME);
        HeadersExchange exchange = new HeadersExchange(ORDER_EXCHANGE_NAME);
        HashMap<String, Object> headers = new HashMap<>();
        headers.put(ORDER_EVENT_HEADER_NAME, ORDER_CANCEL_EVENT_HEADER_VALUE);
        return BindingBuilder.bind(queue).to(exchange).whereAny(headers).match();
    }

}
