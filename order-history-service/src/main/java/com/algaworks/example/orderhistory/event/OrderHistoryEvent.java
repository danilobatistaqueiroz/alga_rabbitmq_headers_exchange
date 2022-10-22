package com.algaworks.example.orderhistory.event;

import java.time.OffsetDateTime;

import com.algaworks.example.orderhistory.model.OrderModel;


public class OrderHistoryEvent {
    private OffsetDateTime date = OffsetDateTime.now();
    private OrderModel order;

    public OrderHistoryEvent() {
    }

    public OrderHistoryEvent(OrderModel order) {
        this.order = order;
    }

    public OffsetDateTime getDate() {
        return date;
    }

    public void setDate(OffsetDateTime date) {
        this.date = date;
    }

    public OrderModel getOrder() {
        return order;
    }

    public void setOrder(OrderModel order) {
        this.order = order;
    }
}