package com.algaworks.example.coupon.listener;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import com.algaworks.example.coupon.config.RabbitMQConfig;
import com.algaworks.example.coupon.event.OrderCancelEvent;
import com.algaworks.example.coupon.event.OrderHistoryEvent;
import com.algaworks.example.coupon.event.OrderPaidEvent;

@Component
public class OrderEventListener {

    @RabbitListener(queues = RabbitMQConfig.COUPON_GENERATE_BASIC_QUEUE_NAME)
    public void onOrderPaidBasic(OrderPaidEvent event) {
        System.out.println("Venda de cliente basic paga recebida de id: " + event.getOrder().getId());
    }
    
    @RabbitListener(queues = RabbitMQConfig.COUPON_GENERATE_PREMIUM_QUEUE_NAME)
    public void onOrderPaidPremium(OrderPaidEvent event) {
        System.out.println("Venda de cliente premium paga recebida de id: " + event.getOrder().getId());
    }
    
    @RabbitListener(queues = RabbitMQConfig.COUPON_CANCEL_QUEUE_NAME)
    public void onOrderCancel(OrderCancelEvent event) {
        System.out.println("Venda cancelada recebida de id: " + event.getOrder().getId());
    }
    

}
