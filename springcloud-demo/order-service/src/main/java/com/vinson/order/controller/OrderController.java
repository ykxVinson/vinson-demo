package com.vinson.order.controller;

import com.vinson.order.domain.Order;
import com.vinson.order.service.IOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("order")
public class OrderController {

    @Autowired
    private IOrderService iOrderService;

    @GetMapping("{orderId}")
    public Order getOrderById(@PathVariable Long orderId){
        return iOrderService.getOrderById(orderId);
    }
}
