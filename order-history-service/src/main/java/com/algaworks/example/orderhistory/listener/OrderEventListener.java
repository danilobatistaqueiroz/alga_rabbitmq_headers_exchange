package com.algaworks.example.orderhistory.listener;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import com.algaworks.example.orderhistory.config.RabbitMQConfig;
import com.algaworks.example.orderhistory.event.OrderHistoryEvent;

@Component
public class OrderEventListener {
    @RabbitListener(queues = RabbitMQConfig.HISTORY_ORDER_QUEUE_NAME)
    public void onOrderHistory(OrderHistoryEvent event) {
        System.out.println("Histórico de ordem gravado de id: " + event.getOrder().getId());
    }
}
