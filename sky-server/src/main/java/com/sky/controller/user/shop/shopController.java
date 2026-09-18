package com.sky.controller.user.shop;

import com.sky.result.Result;
import com.sky.service.ShopService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

@RestController("usershopController")
@Slf4j
@RequestMapping("/user/shop")
public class shopController {
    @Autowired(required=true)
    private ShopService shopService;

    @Autowired
    private RedisTemplate redisTemplate;

    private static final String KEY = "Shop_Status";
    /**
     * 查询店铺的营业状态
     * @return
     */
    @GetMapping("/status")
    public Result<Integer> getStatus(){
        Integer status = (Integer) redisTemplate.opsForValue().get(KEY);
        return Result.success(status);
    }
}
