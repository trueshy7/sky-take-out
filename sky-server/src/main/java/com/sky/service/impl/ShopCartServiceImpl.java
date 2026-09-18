package com.sky.service.impl;


import com.sky.context.BaseContext;
import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.ShoppingCart;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetMealMapper;
import com.sky.mapper.ShoppinngCartMapper;
import com.sky.service.ShopService;
import com.sky.service.ShoppingCartService;
import com.sky.vo.DishVO;
import com.sky.vo.SetmealVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ShopCartServiceImpl implements ShoppingCartService {
    @Autowired
    private DishMapper dishMapper;

    @Autowired
    private ShoppinngCartMapper shoppinngCartMapper;

    @Autowired
    private SetMealMapper setMealMapper;
    public void add(ShoppingCartDTO shoppingCartDTO) {
        ShoppingCart shoppingCart = new ShoppingCart();
        BeanUtils.copyProperties(shoppingCartDTO,shoppingCart);
        Long userId = BaseContext.getCurrentId();
        shoppingCart.setUserId(userId);
        ShoppingCart shoppingCart1 = shoppinngCartMapper.getById(shoppingCart);
        if(shoppingCart1 !=null){
            shoppingCart1.setNumber(shoppingCart1.getNumber() + 1);
            shoppinngCartMapper.update(shoppingCart1);
            return;
        }
        Long dishId = shoppingCartDTO.getDishId();
        Long setmealId = shoppingCartDTO.getSetmealId();
        DishVO dishVO = dishMapper.selectById(dishId);
        if(dishVO !=null){
            shoppingCart.setAmount(dishVO.getPrice());
            shoppingCart.setName(dishVO.getName());
            shoppingCart.setImage(dishVO.getImage());
        } else {
            SetmealVO setmealVO = setMealMapper.selectByID(setmealId);
            shoppingCart.setAmount(setmealVO.getPrice());
            shoppingCart.setName(setmealVO.getName());
            shoppingCart.setImage(setmealVO.getImage());
        }
        shoppingCart.setCreateTime(LocalDateTime.now());
        shoppinngCartMapper.insert(shoppingCart);
    }

    public List<ShoppingCart> list() {
        Long userId = BaseContext.getCurrentId();
        return shoppinngCartMapper.list(userId);
    }

    public void sub(ShoppingCartDTO shoppingCartDTO) {
        ShoppingCart shoppingCart = new ShoppingCart();
        BeanUtils.copyProperties(shoppingCartDTO,shoppingCart);
        Long userId = BaseContext.getCurrentId();
        shoppingCart.setUserId(userId);
        ShoppingCart shoppingCart1 = shoppinngCartMapper.getById(shoppingCart);
        Integer number = shoppingCart1.getNumber();
        if(number > 1){
            shoppingCart1.setNumber(number - 1);
            shoppinngCartMapper.update(shoppingCart1);
        }
        else {
            shoppinngCartMapper.delete(shoppingCart1);
        }
    }
    @Override
    public void deleteAll() {
        ShoppingCart shoppingCart = ShoppingCart.builder()
                                    .userId(BaseContext.getCurrentId())
                                    .build();
        shoppinngCartMapper.delete(shoppingCart);
    }
}
