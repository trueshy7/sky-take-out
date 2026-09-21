package com.sky.controller.admin.Dish;


import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/admin/dish")
@Slf4j
@Api(tags = "菜品接口")
public class DishController {
    @Autowired
    private DishService dishService;

    @Autowired
    private RedisTemplate redisTemplate;
    /**
     * 新增菜品
     * @param dishDTO
     * @return
     */
    @PostMapping
    @ApiOperation("新增菜品")
    public Result insert(@RequestBody DishDTO dishDTO){
        dishService.insert(dishDTO);
        redisTemplate.delete(dishDTO.getCategoryId());
        cleanCache("dish_*");
        return Result.success();
    }

    /**
     * 菜品分页查询
     * @param dishPageQueryDTO
     * @return
     */
    @GetMapping("/page")
    public Result<PageResult> selectByPage(DishPageQueryDTO dishPageQueryDTO){
        log.info("菜品分页查询：{}",dishPageQueryDTO);
        PageResult result = dishService.selectBypage(dishPageQueryDTO);
        return Result.success(result);
    }

    /**
     * 根据ID批量删除菜品
     * @param ids
     * @return
     */
    @DeleteMapping()
    public Result batchDeleteById(Long[] ids){
        log.info("批量删除ID：{}",ids);
        dishService.deleteByID(ids);

        cleanCache("dish_*");
        return Result.success();
    }

    /**
     * 根据ID查询菜品
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public Result<DishVO> selectById(@PathVariable Long id){
        log.info("根据ID查询菜品：{}",id);
        DishVO dishVO  = dishService.selectById(id);
        return Result.success(dishVO);
    }

    /**
     * 修改菜品
     * @param dishDTO
     * @return
     */
    @PutMapping()
    public Result update(@RequestBody DishDTO dishDTO){
        log.info("修改菜品：{}",dishDTO);
        dishService.update(dishDTO);
        String key = "dish_" + dishDTO.getCategoryId();
        redisTemplate.delete(key);

        return Result.success();
    }

    /**
     * 起售，停售菜品
     * @param status
     * @param id
     * @return
     */
    @PostMapping("/status/{status}")
    public Result update_status(@PathVariable Integer status ,Long id){
        dishService.updateStatus(status,id);
        cleanCache("dish_*");
        return Result.success();
    }
    @GetMapping("/list")
    public Result<List> seleceBycategoryId(Long  categoryId){
        List<Dish> dishes = dishService.seleceBycategoryId(categoryId);
        return Result.success(dishes);
    }

    private void cleanCache(String patten) {
        Set keys = redisTemplate.keys(patten);
        redisTemplate.delete(keys);
    }

}
