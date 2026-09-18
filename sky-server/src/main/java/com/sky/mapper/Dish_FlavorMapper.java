package com.sky.mapper;

import com.sky.entity.DishFlavor;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.web.bind.annotation.DeleteMapping;

import java.util.List;

@Mapper
public interface Dish_FlavorMapper {


    void update(DishFlavor dishFlavor);

    void insertflavor(List<DishFlavor> flavors);

    @Select("select * from dish_flavor where dish_id = #{id}")
    List<DishFlavor> selectByDishID(Long id);

    @Delete("delete from dish_flavor where dish_id = #{dishId}")
    void deleteByDishId(Long dishId);


    void deleteFlavorById(Long[] ids);
}
