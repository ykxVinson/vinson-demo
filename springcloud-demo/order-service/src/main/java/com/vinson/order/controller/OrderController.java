package com.vinson.order.controller;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
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

    @SentinelResource("hot")
    @GetMapping("{orderId}")
    public Order getOrderById(@PathVariable Long orderId){
        return iOrderService.getOrderById(orderId);
    }


    @GetMapping("/query")
    public String queryOrder(){
        //查询商品
        iOrderService.queryGoods();
        return "查询订单成功";
    }

    @GetMapping("/save")
    public String saveOrder(){
        //查询商品
        iOrderService.queryGoods();
        return "新增订单成功";
    }

    @GetMapping("/update")
    public String updateOrder(){
        return "更新订单成功";
    }
}
