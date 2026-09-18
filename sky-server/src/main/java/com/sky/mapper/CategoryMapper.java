package com.sky.mapper;


import com.sky.annotation.Autofill;
import com.sky.dto.CategoryDTO;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.entity.Category;
import com.sky.enumeration.OperationType;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;
import java.util.Locale;

@Mapper
public interface CategoryMapper {

    @Autofill(value = OperationType.UPDATE)
    public void update(Category category);


    public List<Category> selectByPage(CategoryPageQueryDTO categoryPageQueryDTO);

    @Insert("insert into category(type, name, sort, status, create_time, update_time, create_user, update_user) " +
            "values(#{type},#{name},#{sort},#{status},#{createTime},#{updateTime},#{createUser},#{updateUser})")
    @Autofill(value = OperationType.INSERT)
    void insert(Category category);

    @Select("delete from category where id =#{id}")
    void deleteById(Long id);

//    @Select("select * from category where type = #{type}")
    Category[] selectByType(String type);
}
