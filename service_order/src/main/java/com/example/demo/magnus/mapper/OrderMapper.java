package com.example.demo.magnus.mapper;

import magnus.distributed.entity.Order;
import org.apache.ibatis.annotations.*;

@Mapper
public interface OrderMapper {
    Integer insertOrder(Order order);

    @Select("select * from managee_order where id=#{id}")
    @Results({
            @Result(property = "id", column = "id", javaType = Long.class),
            @Result(property = "serialNo", column = "serial_no", javaType = String.class),
            @Result(property = "status", column = "status")
    })
    Order selectOrderById(long id);

    @Select("select * from managee_order where id=#{id} for update")
    @Results({
            @Result(property = "id", column = "id", javaType = Long.class),
            @Result(property = "serialNo", column = "serial_no", javaType = String.class),
            @Result(property = "status", column = "status")
    })
    Order selectOrderByIdForUpdate(long id);

    @Update("update managee_order set status=#{statusAfter} where id=#{id} and status=#{statusBefore}")
    void updateOrderStatus(long id, long statusBefore, long statusAfter);
}