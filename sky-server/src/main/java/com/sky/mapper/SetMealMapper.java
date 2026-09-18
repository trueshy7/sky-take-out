package com.sky.mapper;


import com.github.pagehelper.Page;
import com.sky.annotation.Autofill;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Setmeal;
import com.sky.enumeration.OperationType;
import com.sky.vo.SetmealVO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Mapper
public interface SetMealMapper {

    /**
     * 根据分类ID查询套餐中菜品个数
     * @param categoryId
     * @return
     */
    @Select("select count(id) from setmeal where category_id = #{categoryId}")
    public Integer select(Long categoryId);

//    @Insert("insert into setmeal(category_id, name, price, description, image, status) " +
//            "values (#{categoryId},#{name},#{price},#{description},#{image},#{status})")
    @Autofill(OperationType.INSERT)
    void insert(Setmeal setmeal);

    List<SetmealVO> selectByPage(SetmealPageQueryDTO setmealPageQueryDTO);

    void delete(Long[] ids);

    /**
     * 根据套餐ID查询套餐
     * @param id
     * @return
     */
    @Select("select sm.* ,c.name categoryName from setmeal sm left join category c on sm.category_id = c.id where sm.id =#{id}")
    SetmealVO selectByID(Long id);

    @Autofill(OperationType.UPDATE)
    void update(Setmeal setmeal);

    @Select("select * from setmeal where category_id = #{categoryId} and status = #{status}")
    List<Setmeal> list(Setmeal setmeal);
}
