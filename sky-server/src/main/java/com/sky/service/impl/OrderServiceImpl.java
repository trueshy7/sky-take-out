package com.sky.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.google.gson.JsonObject;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.context.BaseContext;
import com.sky.dto.*;
import com.sky.entity.*;
import com.sky.exception.AddressBookBusinessException;
import com.sky.exception.OrderBusinessException;
import com.sky.exception.ShoppingCartBusinessException;
import com.sky.mapper.*;
import com.sky.result.PageResult;
import com.sky.service.OrderService;
import com.sky.utils.WeChatPayUtil;
import com.sky.vo.OrderPaymentVO;
import com.sky.vo.OrderStatisticsVO;
import com.sky.vo.OrderSubmitVO;
import com.sky.vo.OrderVO;
import com.sky.websocket.WebSocketServer;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.graphql.GraphQlProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private OrderDetailMapper orderDetailMapper;
    @Autowired
    private AddressBookMapper addressBookMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private ShoppinngCartMapper shoppinngCartMapper;
    @Autowired
    private WeChatPayUtil weChatPayUtil;
    @Autowired
    private WebSocketServer webSocketServer;


    /**
     * 用户下单
     * @param ordersSubmitDTO
     * @return
     */
    public OrderSubmitVO submit(OrdersSubmitDTO ordersSubmitDTO) {
        AddressBook addressBook = addressBookMapper.getById(ordersSubmitDTO.getAddressBookId());
        if(addressBook == null) {
            throw new AddressBookBusinessException(MessageConstant.ADDRESS_BOOK_IS_NULL);
        }
        List<ShoppingCart> list = shoppinngCartMapper.list(BaseContext.getCurrentId());
        if(list == null || list.size() == 0){
            throw new ShoppingCartBusinessException(MessageConstant.SHOPPING_CART_IS_NULL);
        }
        Orders order = new Orders();
        BeanUtils.copyProperties(ordersSubmitDTO,order);
        order.setOrderTime(LocalDateTime.now());
        order.setAddress(addressBook.getDetail());
        order.setUserId(BaseContext.getCurrentId());
        order.setStatus(Orders.PENDING_PAYMENT);
        order.setPayStatus(Orders.UN_PAID);
        order.setNumber(String.valueOf(System.currentTimeMillis()));
        order.setPhone(addressBook.getPhone());
        order.setConsignee(addressBook.getConsignee());
        orderMapper.insert(order);
        Long orderId = order.getId();
        ArrayList<OrderDetail>  orderDetaillist = new ArrayList<OrderDetail>();
        for (ShoppingCart shoppingCart : list) {
            OrderDetail orderDetail = new OrderDetail();
            BeanUtils.copyProperties(shoppingCart,orderDetail);
            orderDetail.setOrderId(orderId);
            orderDetaillist.add(orderDetail);
        }
        orderDetailMapper.insertBatch(orderDetaillist);
        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setUserId(BaseContext.getCurrentId());
        shoppinngCartMapper.delete(shoppingCart);
        OrderSubmitVO orderSubmitVO =  OrderSubmitVO.builder()
                                                  .id(orderId)
                                                  .orderAmount(ordersSubmitDTO.getAmount())
                                                  .orderNumber(order.getNumber())
                                                  .orderTime(LocalDateTime.now())
                                                  .build();
        return orderSubmitVO;
    }
    /**
     * 订单支付
     *
     * @param ordersPaymentDTO
     * @return
     */
    public OrderPaymentVO payment(OrdersPaymentDTO ordersPaymentDTO) throws Exception {
        // 当前登录用户id
        Long userId = BaseContext.getCurrentId();
        User user = userMapper.getById(userId);

        //调用微信支付接口，生成预支付交易单
//        JSONObject jsonObject = weChatPayUtil.pay(
//                ordersPaymentDTO.getOrderNumber(), //商户订单号
//                new BigDecimal(0.01), //支付金额，单位 元
//                "苍穹外卖订单", //商品描述
//                user.getOpenid() //微信用户的openid
//        );
        JSONObject jsonObject = new JSONObject();
        if (jsonObject.getString("code") != null && jsonObject.getString("code").equals("ORDERPAID")) {
            throw new OrderBusinessException("该订单已支付");
        }

        OrderPaymentVO vo = jsonObject.toJavaObject(OrderPaymentVO.class);
        vo.setPackageStr(jsonObject.getString("package"));

        return vo;
    }

    /**
     * 支付成功，修改订单状态
     *
     * @param outTradeNo
     */
    public void paySuccess(String outTradeNo) {

        // 根据订单号查询订单
        Orders ordersDB = orderMapper.getByNumber(outTradeNo);

        // 根据订单id更新订单的状态、支付方式、支付状态、结账时间
        Orders orders = Orders.builder()
                .id(ordersDB.getId())
                .status(Orders.TO_BE_CONFIRMED)
                .payStatus(Orders.PAID)
                .checkoutTime(LocalDateTime.now())
                .build();

        orderMapper.update(orders);
        Map map = new HashMap();
        map.put("type",1);
        map.put("orderId",ordersDB.getId());
        map.put("content","订单号" + outTradeNo);
        String json = JSON.toJSONString(map);
        webSocketServer.sendToAllClient(json) ;
    }

    /**
     * 查询订单详情
     * @param id
     * @return
     */
    public OrderVO getOrder(Long id) {
        Orders order = orderMapper.getByOrderId(id);
        List<OrderDetail> list = orderDetailMapper.getByOrderId(id);
        OrderVO orderVO = new OrderVO();
        BeanUtils.copyProperties(order,orderVO);
        orderVO.setOrderDetailList(list);
        return orderVO;
    }

    /**
     * 历史订单查询
     * @param ordersPageQueryDTO
     * @return
     */
    public PageResult getHistoryOrders(OrdersPageQueryDTO ordersPageQueryDTO) {
        PageHelper.startPage(ordersPageQueryDTO.getPage(),ordersPageQueryDTO.getPageSize());
        ordersPageQueryDTO.setUserId(BaseContext.getCurrentId());
        
        Page<Orders> page  = (Page)orderMapper.getHistoryOrders(ordersPageQueryDTO);
        List<Orders> orders = page.getResult();
        List<OrderVO> orderVOList = getOrderVOList(orders);
        return new PageResult(page.getTotal(),orderVOList);
    }

    /**
     * 取消订单
     * @param id
     */
    @Override
    public void cacelOrder(Long id) {
        Orders orders = orderMapper.getByOrderId(id);
        if(orders == null){
            throw new OrderBusinessException(MessageConstant.ORDER_NOT_FOUND);
        }
        if(orders.getStatus() > 2){
            throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
        }
        if(orders.getStatus() == Orders.TO_BE_CONFIRMED){
            orders.setPayStatus(Orders.REFUND);
        }
        orders.setStatus(Orders.CANCELLED);
        orders.setCancelReason("用户取消");
        orders.setCancelTime(LocalDateTime.now());
        orderMapper.update(orders);
    }

    /**
     * 再来一单
     * @param id
     */
    public void repeteOrder(Long id) {
        List<OrderDetail> OrderDetaillist = orderDetailMapper.getByOrderId(id);
        for (OrderDetail orderDetail : OrderDetaillist) {
            ShoppingCart shoppingCart = new ShoppingCart();
            shoppingCart.setCreateTime(LocalDateTime.now());
            shoppingCart.setUserId(BaseContext.getCurrentId());
            BeanUtils.copyProperties(orderDetail,shoppingCart);
            shoppinngCartMapper.insert(shoppingCart);
        }
    }

    /**
     * 各个状态的订单数量统计
     * @return
     */
    public OrderStatisticsVO orderstatics() {
        OrderStatisticsVO orderStatisticsVO = new OrderStatisticsVO();
        orderStatisticsVO.setConfirmed(orderMapper.status_count(Orders.CONFIRMED));
        orderStatisticsVO.setDeliveryInProgress(orderMapper.status_count(Orders.DELIVERY_IN_PROGRESS));
        orderStatisticsVO.setToBeConfirmed(orderMapper.status_count(Orders.TO_BE_CONFIRMED));
        return orderStatisticsVO;
    }
    /**
     * 订单搜索
     */
    public PageResult orderCondition(OrdersPageQueryDTO ordersPageQueryDTO) {
        PageHelper.startPage(ordersPageQueryDTO.getPage(),ordersPageQueryDTO.getPageSize());
//        ordersPageQueryDTO.setUserId(BaseContext.getCurrentId());

        Page<Orders> page  = (Page)orderMapper.getHistoryOrders(ordersPageQueryDTO);
        List<Orders> orders = page.getResult();
        List<OrderVO> orderVOList = getOrderVOList(orders);
        return new PageResult(page.getTotal(),orderVOList);
    }

    /**
     * 商家接单
     * @param ordersConfirmDTO
     */
    public void confirm(OrdersConfirmDTO ordersConfirmDTO) {
        Orders orders = orderMapper.getByOrderId(ordersConfirmDTO.getId());
        orders.setStatus(Orders.CONFIRMED);
        orderMapper.update(orders);
    }

    /**
     * 拒单
     * @param ordersRejectionDTO
     */
    public void rejectOrder(OrdersRejectionDTO ordersRejectionDTO) {
        Orders orders = orderMapper.getByOrderId(ordersRejectionDTO.getId());
        orders.setStatus(Orders.CANCELLED);
        orders.setPayStatus(Orders.REFUND);
        orders.setRejectionReason(ordersRejectionDTO.getRejectionReason());
        orders.setCancelTime(LocalDateTime.now());
        orderMapper.update(orders);
    }

    /**
     * 商家取消订单
     * @param ordersCancelDTO
     */
    public void admincacelOrder(OrdersCancelDTO ordersCancelDTO) {
        Orders orders = orderMapper.getByOrderId(ordersCancelDTO.getId());
        orders.setStatus(Orders.CANCELLED);
        if(orders.getStatus() >= 2){
            orders.setPayStatus(Orders.REFUND);
        }
        orders.setCancelReason(ordersCancelDTO.getCancelReason());
        orders.setCancelTime(LocalDateTime.now());
        orderMapper.update(orders);
    }

    /**
     * 派送订单
     * @param id
     */
    public void deliverOrder(Long id) {
        Orders orders = orderMapper.getByOrderId(id);
        orders.setStatus(Orders.DELIVERY_IN_PROGRESS);
        orderMapper.update(orders);
    }
    
    public void completeOrder(Long id) {
        Orders orders = orderMapper.getByOrderId(id);
        if(orders.getStatus() == null || orders.getStatus() != Orders.DELIVERY_IN_PROGRESS){
            throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
        }
        orders.setStatus(Orders.COMPLETED);
        orders.setDeliveryTime(LocalDateTime.now());
        orderMapper.update(orders);
    }

    public void remindOrder(Long id) {
        Orders orders = orderMapper.getByOrderId(id);
        if (orders.getStatus() != Orders.TO_BE_CONFIRMED) {
            throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
        }
        Map map = new HashMap();
        map.put("type",2);
        map.put("orderId",id);
        map.put("content","订单号" + orders.getNumber());
        String json = JSON.toJSONString(map);
        webSocketServer.sendToAllClient(json) ;

    }

    /**
     * 获得订单及菜品信息
     * @param orders
     * @return
     */
    private List<OrderVO> getOrderVOList(List<Orders> orders) {
        List<OrderVO> orderVOList = new ArrayList<>();
        for (Orders order : orders) {
            Long orderId = order.getId();
            List<OrderDetail> orderDetailList = orderDetailMapper.getByOrderId(orderId);
            String OrderDish = "";
            for (OrderDetail orderDetail : orderDetailList) {
                OrderDish = orderDetail.getName() + "*" + orderDetail.getNumber() + ";";
            }
            OrderVO orderVO = new OrderVO();
            BeanUtils.copyProperties(order, orderVO);
            orderVO.setOrderDetailList(orderDetailList);
            orderVO.setOrderDishes(OrderDish);
            orderVOList.add(orderVO);
        }
        return orderVOList;
    }
}
