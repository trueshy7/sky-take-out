package com.sky.service;


import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Setmeal;
import com.sky.result.PageResult;
import com.sky.vo.DishItemVO;
import com.sky.vo.SetmealVO;
import io.swagger.models.auth.In;

import java.util.List;

public interface SetmealService {

    void insert(SetmealDTO setmealDTO);

    PageResult selectByPage(SetmealPageQueryDTO setmealPageQueryDTO);

    void deleteBatchById(Long[] ids);

    SetmealVO selectById(Long id);

    void updateWithDish(SetmealDTO setmealDTO);

    void updateStatus(Integer status, Long id);

    List<Setmeal> list(Setmeal setmeal);

    List<DishItemVO> getDishItemById(Long id);
}
