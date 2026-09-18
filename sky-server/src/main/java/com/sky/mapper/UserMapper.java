package com.sky.mapper;

import com.sky.entity.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDate;

@Mapper
public interface UserMapper {

    @Select("select * from user where openid = #{openId}")
    User selectByopenId(String openId);

    void insert(User user);

    @Select("select * from user where id = #{userId}")
    User getById(Long userId);

    /**
     * 新增用户统计
     * @param time
     * @return
     */
    @Select("select count(id) from user where DATE(create_time) = #{time}")
    Long getnewUserByTime(LocalDate time);

    /**
     * 总用户统计
     * @param time
     * @return
     */
    @Select("select count(id) from user where DATE(create_time) <= #{time}")
    Long gettotalUserByTime(LocalDate time);
}
