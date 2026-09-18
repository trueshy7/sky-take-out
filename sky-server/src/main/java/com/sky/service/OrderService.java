package com.sky.service;

import com.sky.dto.*;
import com.sky.result.PageResult;
import com.sky.vo.OrderPaymentVO;
import com.sky.vo.OrderStatisticsVO;
import com.sky.vo.OrderSubmitVO;
import com.sky.vo.OrderVO;

public interface OrderService {
    OrderSubmitVO submit(OrdersSubmitDTO ordersSubmitDTO);


    /**
     * 订单支付
     * @param ordersPaymentDTO
     * @return
     */
    OrderPaymentVO payment(OrdersPaymentDTO ordersPaymentDTO) throws Exception;

    /**
     * 支付成功，修改订单状态
     * @param outTradeNo
     */
    void paySuccess(String outTradeNo);

    OrderVO getOrder(Long id);

    PageResult getHistoryOrders(OrdersPageQueryDTO ordersPageQueryDTO);

    void cacelOrder(Long id);

    void repeteOrder(Long id);

    OrderStatisticsVO orderstatics();

    PageResult orderCondition(OrdersPageQueryDTO ordersPageQueryDTO);

    void confirm(OrdersConfirmDTO ordersConfirmDTO);

    void rejectOrder(OrdersRejectionDTO ordersRejectionDTO);

    void admincacelOrder(OrdersCancelDTO ordersCancelDTO);

    void deliverOrder(Long id);

    void completeOrder(Long id);

    void remindOrder(Long id);
}
