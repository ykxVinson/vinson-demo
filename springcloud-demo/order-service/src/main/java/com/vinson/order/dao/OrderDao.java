package com.vinson.order.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.vinson.order.domain.Order;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OrderDao extends BaseMapper<Order> {
}
