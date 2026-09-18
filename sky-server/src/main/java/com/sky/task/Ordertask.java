package com.sky.task;

import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import lombok.extern.slf4j.Slf4j;
import net.bytebuddy.asm.Advice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@Slf4j
public class Ordertask {
    @Autowired
    private OrderMapper orderMapper;

    @Scheduled(cron = " 0 * * * * *")
    public void processTimeOutOrder(){
//        log.info("每分钟处理一次：{}", LocalDateTime.now());
        LocalDateTime time = LocalDateTime.now().minusMinutes(15);
        List<Orders> list = orderMapper.getStatusAndOrderTime(Orders.PENDING_PAYMENT , time);
        for (Orders orders : list) {
            orders.setStatus(Orders.CANCELLED);
            orders.setCancelReason("用户未支付，订单超时");
            orders.setCancelTime(LocalDateTime.now());
            orderMapper.update(orders);
        }
    }
//    @Scheduled(cron = " 0 0 1 * * ?")
    public void processDeliveryOrder(){
//        log.info("处理一直显示派送中的订单：{}", LocalDateTime.now());
        LocalDateTime time = LocalDateTime.now().minusMinutes(60);
        List<Orders> list = orderMapper.getStatusAndOrderTime(Orders.DELIVERY_IN_PROGRESS , time);
        if (list != null || list.size() != 0) {
            for (Orders orders : list) {
                orders.setStatus(Orders.COMPLETED);
                orderMapper.update(orders);
            }
        }
    }
}
