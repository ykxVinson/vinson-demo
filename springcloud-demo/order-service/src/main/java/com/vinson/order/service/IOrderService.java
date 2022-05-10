package com.vinson.order.service;

import com.vinson.order.domain.Order;

public interface IOrderService {
    Order getOrderById(long orderId);
}
