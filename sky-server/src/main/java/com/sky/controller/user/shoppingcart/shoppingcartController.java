package com.sky.controller.user.shoppingcart;

import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.ShoppingCart;
import com.sky.result.Result;
import com.sky.service.ShoppingCartService;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Delete;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/user/shoppingCart")
public class shoppingcartController {
    @Autowired
    private ShoppingCartService shoppingCartService;

    /**
     * 添加菜品或者套餐到购物车
     * @param shoppingCartDTO
     * @return
     */
    @PostMapping("/add")
    public Result add(@RequestBody ShoppingCartDTO shoppingCartDTO){
        log.info("添加到购物车的商品/套餐为:{}",shoppingCartDTO);
        shoppingCartService.add(shoppingCartDTO);
        return Result.success();
    }

    /**
     * 查询购物车
     * @return
     */
    @GetMapping("/list")
    public Result<List> list(){
        List<ShoppingCart> list = shoppingCartService.list();
        return Result.success(list);
    }
    @PostMapping("/sub")
    public Result sub(@RequestBody ShoppingCartDTO shoppingCartDTO){
        shoppingCartService.sub(shoppingCartDTO);
        return Result.success();
    }

    @DeleteMapping("/clean")
    public Result deleteAll(){
        shoppingCartService.deleteAll();
        return Result.success();
    }
}
