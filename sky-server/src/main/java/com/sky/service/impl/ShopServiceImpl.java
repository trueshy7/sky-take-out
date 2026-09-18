package com.sky.service.impl;

import com.sky.service.ShopService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ShopServiceImpl implements ShopService {
    @Autowired
    private RedisTemplate redisTemplate;

    public void setstatus(Integer status) {
        redisTemplate.opsForValue().set("status",status);
    }

    public Integer getstatus() {
        Integer status = (Integer) redisTemplate.opsForValue().get("status");
        return status;
    }
}
