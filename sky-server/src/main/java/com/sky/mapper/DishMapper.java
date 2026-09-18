package com.sky.mapper;

import com.sky.annotation.Autofill;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.enumeration.OperationType;
import com.sky.vo.DishVO;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Service;

import java.util.List;

@Mapper
public interface DishMapper {

    @Autofill(value = OperationType.INSERT)
    void insert(Dish dish);

    /**
     * 查看分类关联的菜品
     * @param categoryId
     * @return
     */
    @Select("select count(id) from dish where category_id = #{categoryId}")
    public Integer select(Long categoryId);

    List<DishVO> selectByPage(DishPageQueryDTO dishPageQueryDTO);

    void deleteDishByID(Long[] ids);
    
    

    @Select("select dish.*,c.name categoryName from dish left join category c on dish.category_id = c.id where dish.id = #{id}")
    DishVO selectById(Long id);



    @Autofill(OperationType.UPDATE)
    void update(Dish dish);

    /**
     * 根据分类ID查询菜品
     * @param
     * @return
     */
    @Select("select * from dish where category_id = #{categoryId}")
    List<Dish> selectBycategoryId(Long categoryId);

    @Select("select * from dish where category_id = #{categoryId} and status = #{status}")
    List<Dish> list(Dish dish);
}
