package com.vinson.order.service.impl;

import com.vinson.feign.clients.UserClient;
import com.vinson.feign.domain.User;
import com.vinson.order.dao.OrderDao;
import com.vinson.order.domain.Order;
import com.vinson.order.service.IOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OrderService implements IOrderService {

    @Autowired
    private OrderDao orderDao;

    @Autowired
    private UserClient userClient;

    @Override
    public Order getOrderById(long orderId) {
        //1. 查询订单
        Order order = orderDao.selectById(orderId);
        //2. 发送http请求，实现远程调用
        User user =userClient.findById(order.getUserId());
        //3. 封装user到order
        order.setUser(user);
        return order;
    }
}
