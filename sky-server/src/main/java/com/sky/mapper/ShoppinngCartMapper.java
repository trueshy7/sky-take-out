package com.sky.mapper;

import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.ShoppingCart;
import org.apache.ibatis.annotations.*;
import org.springframework.web.bind.annotation.DeleteMapping;

import java.util.List;

@Mapper
public interface ShoppinngCartMapper {
    /**
     * 插入购物车
     * @param shoppingCart
     */
    @Insert("insert into shopping_cart(name, image, user_id, dish_id, setmeal_id, dish_flavor, amount, create_time) " +
            "VALUES(#{name},#{image},#{userId},#{dishId},#{setmealId},#{dishFlavor},#{amount},#{createTime}) ")
    void insert(ShoppingCart shoppingCart);

    /**
     * 查询购物车全部数据
     * @param userId
     * @return
     */
    @Select("select * from shopping_cart where user_id = #{userId} order by create_time desc")
    List<ShoppingCart> list(Long userId);

    void delete(ShoppingCart shoppingCart);

    //@Select("select * from shopping_cart where user_id = #{userId} and dish_id =#{dishId}  and dish_flavor = #{dishFlavor}")
    ShoppingCart getById(ShoppingCart shoppingCart);

    @Update("update shopping_cart set number = #{number} where id = #{id}")
    void update(ShoppingCart shoppingCart);

}
