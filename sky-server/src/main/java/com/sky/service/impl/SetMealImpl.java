package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Setmeal;
import com.sky.entity.SetmealDish;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.exception.SetmealEnableFailedException;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetMealDishMapper;
import com.sky.mapper.SetMealMapper;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.SetmealService;
import com.sky.vo.DishItemVO;
import com.sky.vo.DishVO;
import com.sky.vo.SetmealVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Service
public class SetMealImpl implements SetmealService {
    @Autowired
    private SetMealMapper setMealMapper;

    @Autowired
    private SetMealDishMapper setMealDishMapper;

    @Autowired
    private DishMapper dishMapper;

    public void insert(SetmealDTO setmealDTO) {
        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDTO,setmeal);
        //修改套餐表和套餐菜品表
        setMealMapper.insert(setmeal);

        Long setmealId = setmeal.getId();
        List<SetmealDish> setmealDishes = setmealDTO.getSetmealDishes();
        setmealDishes.forEach(setmealDish -> setmealDish.setSetmealId(setmealId));
        setMealDishMapper.insert(setmealDishes);

    }

    public PageResult selectByPage(SetmealPageQueryDTO setmealPageQueryDTO) {
        PageHelper.startPage(setmealPageQueryDTO.getPage(),setmealPageQueryDTO.getPageSize());
        List<SetmealVO> list = setMealMapper.selectByPage(setmealPageQueryDTO);
        Page page = (Page)list;
        return new PageResult(page.getTotal(),page.getResult());
    }

    @Override
    public void deleteBatchById(Long[] ids) {
        //同时删除套餐和关联的菜品
        for (Long id: ids) {
            SetmealVO setmealVO = setMealMapper.selectByID(id);
            if(setmealVO.getStatus() == StatusConstant.ENABLE){
                throw new DeletionNotAllowedException(MessageConstant.SETMEAL_ON_SALE);
            }
        }
        setMealMapper.delete(ids);
        setMealDishMapper.delete(ids);
    }

    /**
     * 根据套餐ID查询套餐及菜品相关信息
     * @param id
     * @return
     */
    public SetmealVO selectById(Long id) {
        SetmealVO setmealVO = setMealMapper.selectByID(id);
        List<SetmealDish> setmealDishList = setMealDishMapper.selectByID(id);
        setmealVO.setSetmealDishes(setmealDishList);
        return setmealVO;
    }

    public void updateWithDish(SetmealDTO setmealDTO) {
        //更新套餐表和套餐菜品表
        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDTO,setmeal);
        setMealMapper.update(setmeal);
        //先把菜品全部删除，然后再重新-填充
        Long setmealId = setmeal.getId();
        setMealDishMapper.deleteBysetmealId(setmealId);
        List<SetmealDish> setmealDishes = setmealDTO.getSetmealDishes();
        setmealDishes.forEach(setmealDish -> setmealDish.setSetmealId(setmealId));
        setMealDishMapper.insert(setmealDishes);
    }

    /**
     * 起售，停售套餐
     * @param status
     */
    public void updateStatus(Integer status,Long id) {
        List<SetmealDish> setmealDishList = setMealDishMapper.selectByID(id);
        for (SetmealDish setmealDish: setmealDishList) {
            Long dishId = setmealDish.getDishId();
            DishVO dishVO = dishMapper.selectById(dishId);
            if(dishVO.getStatus() == StatusConstant.DISABLE){
                throw  new SetmealEnableFailedException(MessageConstant.SETMEAL_ENABLE_FAILED);
            }
        }
        SetmealVO setmealVO = setMealMapper.selectByID(id);
        setmealVO.setStatus(status);
        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealVO,setmeal);
        setMealMapper.update(setmeal);
    }

    @Override
    public List<Setmeal> list(Setmeal setmeal) {
        return setMealMapper.list(setmeal);
    }

    @Override
    public List<DishItemVO> getDishItemById(Long id) {
        return setMealDishMapper.getDishItemById(id);
    }
}
