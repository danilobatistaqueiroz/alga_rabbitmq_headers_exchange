package com.algaworks.example.order.api;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.algaworks.example.order.config.RabbitMQConfig;
import com.algaworks.example.order.domain.Order;
import com.algaworks.example.order.domain.OrderRepository;
import com.algaworks.example.order.event.OrderPaidEvent;
import com.algaworks.example.order.model.OrderInputModel;
import com.algaworks.example.order.model.OrderModel;

@RestController
@RequestMapping(value = "/v1/orders")
public class OrderController {

	@Autowired
	private OrderRepository orders;

	@Autowired
	private RabbitTemplate rabbitTemplate;

	private static final String ORDER_EVENT_HEADER_NAME = "order-event-type";
	private static final String CUSTOMER_TYPE_HEADER_NAME = "customer-type";

	private static final String ORDER_PAID_EVENT_HEADER_VALUE = "order-paid";
	private static final String ORDER_CANCEL_EVENT_HEADER_VALUE = "order-cancel";
	private static final String CUSTOMER_BASIC_HEADER_VALUE = "basic";
	private static final String CUSTOMER_PREMIUM_HEADER_VALUE = "premium";

	@PostMapping
	public OrderModel create(@RequestBody OrderInputModel order) {
		return OrderModel.of(orders.save(order.toOrder()));
	}

	@GetMapping
	public List<OrderModel> list() {
		return orders.findAll().stream().map(OrderModel::of).toList();
	}

	@GetMapping("{id}")
	public OrderModel findById(@PathVariable Long id) {
		return OrderModel.of(orders.findById(id).orElseThrow());
	}

	@PostMapping("{id}/pay")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void pay(@PathVariable Long id) {
		Order order = orders.findById(id).orElseThrow();
		order.markAsPaid();
		OrderPaidEvent event = new OrderPaidEvent(OrderModel.of(order));
		MessagePostProcessor messagePostProcessor = message -> {
			MessageProperties messageProperties = message.getMessageProperties();
			messageProperties.setHeader(ORDER_EVENT_HEADER_NAME, ORDER_PAID_EVENT_HEADER_VALUE);
			if(order.getValue().compareTo(BigDecimal.valueOf(100))>=0) {
				messageProperties.setHeader(CUSTOMER_TYPE_HEADER_NAME, CUSTOMER_PREMIUM_HEADER_VALUE);
			} else {
				messageProperties.setHeader(CUSTOMER_TYPE_HEADER_NAME, CUSTOMER_BASIC_HEADER_VALUE);
			}
			return message;
		};
		rabbitTemplate.convertAndSend(RabbitMQConfig.ORDER_EXCHANGE_NAME, "", event, messagePostProcessor);
		orders.save(order);
	}

	@PostMapping("{id}/cancel")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void cancel(@PathVariable Long id) {
		Order order = orders.findById(id).orElseThrow();
		order.cancel();
        OrderPaidEvent event = new OrderPaidEvent(OrderModel.of(order));
        MessagePostProcessor messagePostProcessor = message -> {
            MessageProperties messageProperties = message.getMessageProperties();
            messageProperties.setHeader(ORDER_EVENT_HEADER_NAME, ORDER_CANCEL_EVENT_HEADER_VALUE);
            if(order.getValue().compareTo(BigDecimal.valueOf(100))>=0) {
                messageProperties.setHeader(CUSTOMER_TYPE_HEADER_NAME, CUSTOMER_PREMIUM_HEADER_VALUE);
            } else {
                messageProperties.setHeader(CUSTOMER_TYPE_HEADER_NAME, CUSTOMER_BASIC_HEADER_VALUE);
            }
            return message;
        };
        rabbitTemplate.convertAndSend(RabbitMQConfig.ORDER_EXCHANGE_NAME, "", event, messagePostProcessor);
		orders.save(order);
	}
	
}
