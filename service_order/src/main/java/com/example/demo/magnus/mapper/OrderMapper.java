package com.example.demo.magnus.mapper;

import magnus.distributed.entity.Order;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OrderMapper {
    public int insertOrder(Order order);
    public Order selectOrderById(long id);
}
