package com.sky.mapper;

import com.sky.entity.SetmealDish;
import com.sky.vo.DishItemVO;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SetMealDishMapper {
    /**
     * 根据菜品ID查询套餐中菜品数量
     * @param dishId
     * @return
     */
    @Select("select count(id) from setmeal_dish where dish_id = #{dishId}")
    public Integer selectByDishid(Long dishId);

    void insert(List<SetmealDish> setmealDishes);

    void delete(Long[] ids);

    @Select("select * from setmeal_dish where setmeal_id = #{setmealId}")
    List<SetmealDish> selectByID(Long setmealId);

    @Delete("delete from setmeal_dish where setmeal_id = #{setmealId}")
    void deleteBysetmealId(Long setmealId);

    @Select("select sd.copies copies, d.description,d.image,d.name from setmeal_dish sd  left join dish d on sd.dish_id = d.id where sd.setmeal_id =#{id}")
    List<DishItemVO> getDishItemById(Long id);
}
