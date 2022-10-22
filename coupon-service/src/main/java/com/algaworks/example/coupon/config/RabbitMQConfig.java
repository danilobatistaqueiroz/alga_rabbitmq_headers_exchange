package com.algaworks.example.coupon.config;

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

import com.fasterxml.jackson.databind.ObjectMapper;

@Configuration
public class RabbitMQConfig {
    
    public static final String ORDER_EXCHANGE_NAME = "order.v1.events";
    
    private static final String ORDER_EVENT_HEADER_NAME = "order-event-type";
    private static final String CUSTOMER_TYPE_HEADER_NAME = "customer-type";

    private static final String ORDER_PAID_EVENT_HEADER_VALUE = "order-paid";
    private static final String ORDER_CANCEL_EVENT_HEADER_VALUE = "order-cancel";
    private static final String CUSTOMER_BASIC_HEADER_VALUE = "basic";
    private static final String CUSTOMER_PREMIUM_HEADER_VALUE = "premium";
    
    public static final String COUPON_GENERATE_BASIC_QUEUE_NAME = "coupon.v1.on-order-paid-basic.generate-coupon";
    public static final String COUPON_GENERATE_PREMIUM_QUEUE_NAME = "coupon.v1.on-order-paid-premium.generate-coupon";
    public static final String COUPON_CANCEL_QUEUE_NAME = "coupon.v1.on-order-cancel.cancel-coupon";
    
    
    @Bean
    public Queue queueGenerateCouponBasic() {
        return new Queue(COUPON_GENERATE_BASIC_QUEUE_NAME);
    }
    
    @Bean
    public Queue queueGenerateCouponPremium() {
        return new Queue(COUPON_GENERATE_PREMIUM_QUEUE_NAME);
    }
    
    @Bean
    public Queue queueCancelCoupon() {
        return new Queue(COUPON_CANCEL_QUEUE_NAME);
    }
    
    @Bean
    public Binding bindingOnOrderCancel() {
        Queue queue = new Queue(COUPON_CANCEL_QUEUE_NAME);
        HeadersExchange exchange = new HeadersExchange(ORDER_EXCHANGE_NAME);
        return BindingBuilder.bind(queue).to(exchange)
                .where(ORDER_EVENT_HEADER_NAME)
                .matches(ORDER_CANCEL_EVENT_HEADER_VALUE);
    }
    
    @Bean
    public Binding bindingOnOrderPaidBasic() {
        Queue queue = new Queue(COUPON_GENERATE_BASIC_QUEUE_NAME);
        HeadersExchange exchange = new HeadersExchange(ORDER_EXCHANGE_NAME);
        HashMap<String, Object> headers = new HashMap<>();
        headers.put(ORDER_EVENT_HEADER_NAME, ORDER_PAID_EVENT_HEADER_VALUE);
        headers.put(CUSTOMER_TYPE_HEADER_NAME, CUSTOMER_BASIC_HEADER_VALUE);
        return BindingBuilder.bind(queue).to(exchange)
                .whereAll(headers).match();
    }
    
    @Bean
    public Binding bindingOnOrderPaidPremium() {
        Queue queue = new Queue(COUPON_GENERATE_PREMIUM_QUEUE_NAME);
        HeadersExchange exchange = new HeadersExchange(ORDER_EXCHANGE_NAME);
        HashMap<String, Object> headers = new HashMap<>();
        headers.put(ORDER_EVENT_HEADER_NAME, ORDER_PAID_EVENT_HEADER_VALUE);
        headers.put(CUSTOMER_TYPE_HEADER_NAME, CUSTOMER_PREMIUM_HEADER_VALUE);
        return BindingBuilder.bind(queue).to(exchange)
                .whereAll(headers).match();
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

}
