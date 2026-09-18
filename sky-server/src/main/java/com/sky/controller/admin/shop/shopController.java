package com.sky.controller.admin.shop;

import com.sky.result.Result;
import com.sky.service.ShopService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

@RestController("adminshopController")
@Slf4j
@RequestMapping("/admin/shop")
public class shopController {
    @Autowired
    private ShopService shopService;
    @Autowired
    private RedisTemplate redisTemplate;



    private static final String KEY = "Shop_Status";



    /**
     * 设置店铺的营业状态
     * @param status
     * @return
     */
    @PutMapping("/{status}")
    public Result setStatus(@PathVariable Integer status){
        log.info("设置营业状态：{}",status == 1 ? "营业中" : "打烊中");
        redisTemplate.opsForValue().set(KEY,status);
        return Result.success();
    }

    /**
     * 查询店铺的营业状态
     * @return
     */
    @GetMapping("/status")
    public Result<Integer> getStatus(){
        Integer status = (Integer) redisTemplate.opsForValue().get(KEY);
        log.info("获取到的店铺营业状态：{}",status == 1 ? "营业中" : "打烊中");
        return Result.success(status);
    }
}
