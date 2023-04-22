package com.scut.turing.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@EnableScheduling
@Slf4j
public class RedisService {


    @Autowired
    private PgService pgService;

    @Autowired
    private RedisTemplate<String,String> redisTemplate;

    private final int max_size = 1000;

    private final int scheduledTime = 1000*10;

    public void SqlHandler(String sql)
    {
        ListOperations<String, String> listOperations = redisTemplate.opsForList();
        listOperations.rightPush("SqlList",sql);
        log.info(sql);
    }

    @Scheduled(fixedDelay = scheduledTime)
    @Async("taskExecutor")
    public void sqlTask()
    {
        ListOperations<String, String> listOperations = redisTemplate.opsForList();
        List<String> sqlList = listOperations.range("SqlList",0,-1);
        listOperations.trim("SqlList",-1,0);

        assert sqlList != null;
        if(sqlList.size()>0)
        {
            pgService.executeSql(sqlList);
        }


    }


}
