package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.annotation.Autofill;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.DishMapper;
import com.sky.mapper.Dish_FlavorMapper;
import com.sky.mapper.SetMealDishMapper;
import com.sky.mapper.SetMealMapper;
import com.sky.properties.AliOssProperties;
import com.sky.result.PageResult;
import com.sky.service.DishService;
import com.sky.utils.AliOssUtil;
import com.sky.vo.DishVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import com.sky.constant.MessageConstant;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class DishServiceImpl implements DishService {
    @Autowired
    private DishMapper dishMapper;

    @Autowired
    private SetMealDishMapper setMealDishMapper;

    @Autowired
    private Dish_FlavorMapper dish_flavorMapper;

    @Override
    public void insert(DishDTO dishDTO) {
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO,dish);
        dishMapper.insert(dish);
        Long dishId = dish.getId();
        List<DishFlavor> flavors = dishDTO.getFlavors();
        flavors.forEach( dishFlavor ->{
                dishFlavor.setDishId(dishId);
        });
        dish_flavorMapper.insertflavor(flavors);

    }

    @Override
    public PageResult selectBypage(DishPageQueryDTO dishPageQueryDTO) {
        PageHelper.startPage(dishPageQueryDTO.getPage(),dishPageQueryDTO.getPageSize());
        Page<DishVO> page=  (Page)dishMapper.selectByPage(dishPageQueryDTO);
        return new PageResult(page.getTotal(),page.getResult());
    }
    @Override
    public void deleteByID(Long[] ids) {
        for (Long id : ids) {
            DishVO dishVO = dishMapper.selectById(id);
            Long dishId = dishVO.getId();
            int count = setMealDishMapper.selectByDishid(dishId);
            if(dishVO.getStatus() == StatusConstant.ENABLE){
                throw  new DeletionNotAllowedException(MessageConstant.DISH_ON_SALE);
            }
            if(count > 0){
                throw new DeletionNotAllowedException(MessageConstant.DISH_BE_RELATED_BY_SETMEAL);
            }
        }
        dishMapper.deleteDishByID(ids);
        dish_flavorMapper.deleteFlavorById(ids);
    }
    @Override
    public DishVO selectById(Long id) {
        DishVO dishVO = new DishVO();
        dishVO = dishMapper.selectById(id);
        dishVO.setFlavors(dish_flavorMapper.selectByDishID(id));
        return dishVO;
    }

    /**
     * 修改菜品
     * @param dishDTO
     */
    public void update(DishDTO dishDTO) {
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO,dish);
        dishMapper.update(dish);
        Long dishId = dish.getId();
        //先删除原有的菜品口味，再填充现在增、删、改后的菜品口味。
        dish_flavorMapper.deleteByDishId(dishId);

        //填充
        List<DishFlavor> flavors = dishDTO.getFlavors();
        if(flavors.size() != 0){
            flavors.forEach(dishFlavor -> dishFlavor.setDishId(dishId));
            log.info("修改菜品口味：{}",flavors);
            dish_flavorMapper.insertflavor(flavors);
        }
    }

    public void updateStatus(Integer status, Long id) {
        Dish dish = new Dish();
        DishVO dishVO = dishMapper.selectById(id);
        BeanUtils.copyProperties(dishVO,dish);
        dish.setStatus(status);
        dishMapper.update(dish);
    }

    public List<Dish> seleceBycategoryId(Long categoryId) {
        return dishMapper.selectBycategoryId(categoryId);
    }

    @Override
    public List<DishVO> listWithFlavor(Dish dish) {
        List<DishVO> dishVO = new ArrayList<>();
        List<Dish> dish1 = dishMapper.list(dish);
        for (Dish d: dish1) {
            DishVO dishVO1 = new DishVO();
            BeanUtils.copyProperties(d,dishVO1);
            List<DishFlavor> dishFlavors = dish_flavorMapper.selectByDishID(dishVO1.getId());
            dishVO1.setFlavors(dishFlavors);
            dishVO.add(dishVO1);
        }
        return dishVO;
    }
}
