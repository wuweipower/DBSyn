package com.scut.turing.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;

import java.util.List;

@Service
public class RedisService {

    @Autowired
    private RedisTemplate<String,String> redisTemplate;

    private final int max_size = 1000;


    public void SqlHandler(String sql)
    {
        ListOperations<String, String> listOperations = redisTemplate.opsForList();
        listOperations.rightPush("SqlList",sql);
    }

    public void SqlHandler_1(String sql)
    {
        ListOperations<String, String> listOperations = redisTemplate.opsForList();
        listOperations.rightPush("batch_1",sql);
    }

    public void SqlHandler_2(String sql)
    {
        ListOperations<String, String> listOperations = redisTemplate.opsForList();
        listOperations.rightPush("batch_2",sql);
    }

    public void SqlHandler_3(String sql)
    {
        ListOperations<String, String> listOperations = redisTemplate.opsForList();
        listOperations.rightPush("batch_3",sql);
    }



}
