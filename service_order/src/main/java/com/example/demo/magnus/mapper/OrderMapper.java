package com.example.demo.magnus.mapper;

import magnus.distributed.entity.Order;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface OrderMapper {
    public int insertOrder(Order order);

    @Select("select * from managee_order where id=#{id}")
    @Results({
            @Result(property = "id", column = "id", javaType = Long.class),
            @Result(property = "serialNo", column = "serial_no", javaType = String.class)
    })
    public Order selectOrderById(long id);
}
