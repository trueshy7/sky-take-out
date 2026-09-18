package com.sky.mapper;

import com.sky.dto.OrdersPageQueryDTO;
import com.sky.entity.OrderDetail;
import com.sky.entity.ShoppingCart;
import com.sky.vo.OrderVO;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.web.bind.annotation.DeleteMapping;

import java.util.List;

@Mapper
public interface OrderDetailMapper {

    @Insert("insert into order_detail (name, image, order_id, dish_id, setmeal_id, dish_flavor, amount) " +
            "values (#{name},#{image},#{orderId},#{dishId},#{setmealId},#{dishFlavor},#{amount});")
    public void insert(OrderDetail orderDetail);

    void insertBatch(List<OrderDetail> orderDetaillist);

    @Select("select * from order_detail where order_id = #{id}")
    List<OrderDetail> getByOrderId(Long orderId);

//    @Delete("delete from order_detail where order_id = #{id}")
//    void deleteOrder(Long orderId);
}
