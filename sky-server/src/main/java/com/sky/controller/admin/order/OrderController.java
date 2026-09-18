package com.sky.controller.admin.order;

import com.sky.dto.OrdersCancelDTO;
import com.sky.dto.OrdersConfirmDTO;
import com.sky.dto.OrdersPageQueryDTO;
import com.sky.dto.OrdersRejectionDTO;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.OrderService;
import com.sky.vo.OrderStatisticsVO;
import com.sky.vo.OrderVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/order")
@Slf4j
public class OrderController {
    @Autowired
    private OrderService orderService;

    /**
     * 各个状态的订单数量统计
     * @return
     */
    @GetMapping("statistics")
    public Result<OrderStatisticsVO> orderstatics(){
        OrderStatisticsVO orderStatisticsVO = orderService.orderstatics();
        return Result.success(orderStatisticsVO);
    }

    /**
     * 查询订单详情
     * @param id
     * @return
     */
    @GetMapping("/details/{id}")
    public Result<OrderVO> getOrder(@PathVariable Long id){
        OrderVO orderVO = orderService.getOrder(id);
        return Result.success(orderVO);
    }

    /**
     * 订单搜索
     * @param ordersPageQueryDTO
     * @return
     */
    @GetMapping("/conditionSearch")
    public Result<PageResult> getHistoryOrders(OrdersPageQueryDTO ordersPageQueryDTO){
        PageResult pageResult = orderService.orderCondition(ordersPageQueryDTO);
        return Result.success(pageResult);
    }

    /**
     * 接单
     * @param ordersConfirmDTO
     * @return
     */
    @PutMapping("/confirm")
    public Result orderConfirm(@RequestBody OrdersConfirmDTO ordersConfirmDTO){
        log.info("接单中，订单信息为：{}",ordersConfirmDTO);
        orderService.confirm(ordersConfirmDTO);
        return Result.success();
    }

    /**
     * 拒单
     * @param ordersRejectionDTO
     * @return
     */
    @PutMapping("/rejection")
    public Result orderReject(@RequestBody OrdersRejectionDTO ordersRejectionDTO){
        orderService.rejectOrder(ordersRejectionDTO);
        return Result.success();
    }

    /**
     * 取消订单
     * @param ordersCancelDTO
     * @return
     */
    @PutMapping("/cancel")
    public Result orderCancel(@RequestBody OrdersCancelDTO ordersCancelDTO){
        orderService.admincacelOrder(ordersCancelDTO);
        return Result.success();
    }

    /**
     * 派送订单
     * @param id
     * @return
     */
    @PutMapping("delivery/{id}")
    public Result orderdeliver(@PathVariable Long id){
        log.info("派送中,订单id为：{}",id);
        orderService.deliverOrder(id);
        return Result.success();
    }
    @PutMapping("/complete/{id}")
    public Result orderComplete(@PathVariable Long id){
        log.info("已完成,订单id为：{}",id);
        orderService.completeOrder(id);
        return Result.success();
    }

}
