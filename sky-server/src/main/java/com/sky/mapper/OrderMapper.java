package com.sky.mapper;

import com.sky.dto.OrdersPageQueryDTO;
import com.sky.entity.Orders;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface OrderMapper {

    @Select("select sum(od.number)from order_detail od left join orders o on od.order_id = o.id "
            + "where DATE(o.order_time) > #{begin} and DATE(o.order_time) < #{end} and o.status = 5 group by od.name order by sum(od.number) desc limit 10")
    List<String> getSalesTop10Number(LocalDate begin, LocalDate end);

    @Select("select od.name from order_detail od left join orders o on od.order_id = o.id "
            + "where DATE(o.order_time) > #{begin} and DATE(o.order_time) < #{end} and o.status = 5 group by od.name order by sum(od.number) desc limit 10")
    List<String> getSalesTop10Name(LocalDate begin, LocalDate end);

    public void insert(Orders order);
    /**
     * 根据订单号查询订单
     * @param orderNumber
     */
    @Select("select * from orders where number = #{orderNumber}")
    Orders getByNumber(String orderNumber);

    /**
     * 修改订单信息
     * @param orders
     */
    void update(Orders orders);

    /**
     * 查询订单
     * @param id
     * @return
     */
    @Select("select * from orders where id = #{id}")
    Orders getByOrderId(Long id);

    /**
     * 获取历史订单
     * @param ordersPageQueryDTO
     * @return
     */
    List<Orders> getHistoryOrders(OrdersPageQueryDTO ordersPageQueryDTO);

    /**
     * 各个状态的订单数量统计
     * @param status
     * @return
     */
    @Select("select count(*) from orders group by status having status = #{status}")
    Integer status_count(Integer status);

    @Select("select * from orders where status = #{status} and order_time < #{time} ")
    List<Orders> getStatusAndOrderTime(Integer status, LocalDateTime time);

    /**
     * 营业额统计
     * @param time
     * @param status
     * @return
     */
    @Select("select sum(amount) from orders where DATE (delivery_time) = #{time} and status = #{status}")
    Double getSumByTime(LocalDate time,Integer status);

    /**
     *
     * @param begin
     * @return
     */
    Integer getvalidOrderByTime(LocalDate begin,Integer status);

    @Select("select count(id) from orders where DATE(order_time) = #{begin}")
    Integer getorderCountByTime(LocalDate begin);
}
