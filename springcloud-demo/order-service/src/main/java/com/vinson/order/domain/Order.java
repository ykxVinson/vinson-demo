package com.vinson.order.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.vinson.feign.domain.User;
import lombok.Data;


@Data
@TableName("tb_order")
public class Order {
    private Long id;
    private Long price;
    private String name;
    private Integer num;
    private Long userId;
    @TableField(exist = false)
    private User user;
}
