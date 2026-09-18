package com.sky.controller.admin.setmeal;


import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.SetmealService;
import com.sky.vo.SetmealVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequestMapping("/admin/setmeal")
public class SetMealController {
    @Autowired
    private SetmealService setmealService;

    /**
     * 新增套餐
     *
     * @param setmealDTO
     * @return
     */
    @PostMapping()
    @CachePut(cacheNames = "setmealCache",key = "#setmealDTO.categoryId")
    public Result insert(@RequestBody SetmealDTO setmealDTO) {
        setmealService.insert(setmealDTO);
        return Result.success();
    }

    /**
     * 分页查询
     *
     * @param setmealPageQueryDTO
     * @return
     */
    @GetMapping("/page")
    public Result<PageResult> selectByPage(SetmealPageQueryDTO setmealPageQueryDTO) {
        log.info("菜品分页查询：{}", setmealPageQueryDTO);
        PageResult result = setmealService.selectByPage(setmealPageQueryDTO);
        return Result.success(result);
    }

    /**
     * 根据ID批量删除套餐
     *
     * @param ids
     * @return
     */
    @DeleteMapping()
    @CacheEvict(cacheNames = "setmealCache",allEntries = true)
    public Result deleteBatchById(Long[] ids) {
        setmealService.deleteBatchById(ids);
        return Result.success();
    }

    /**
     * 根据套餐ID查询套餐相关信息
     *
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public Result<SetmealVO> selectByID(@PathVariable Long id) {
        log.info("根据id查询套餐：{}", id);
        SetmealVO setmealVO = setmealService.selectById(id);
        return Result.success(setmealVO);
    }

    /**
     * 修改套餐
     *
     * @param setmealDTO
     * @return
     */
    @PutMapping()
    @CacheEvict(cacheNames = "setmealCache",allEntries = true)
    public Result updateWithDish(@RequestBody SetmealDTO setmealDTO) {
        log.info("修改套餐：{}", setmealDTO);
        setmealService.updateWithDish(setmealDTO);
        return Result.success();
    }

    @PostMapping("status/{status}")
    @CacheEvict(cacheNames = "setmealCache",allEntries = true)
    public Result updateStatus(@PathVariable Integer status,Long id){
        log.info("起售/停售套餐ID,状态：{},{}",id,status);
        setmealService.updateStatus(status,id);
        return Result.success();
    }
}

