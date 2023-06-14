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
public class TaskService {
    private final int scheduledTime = 1000*3;

    @Autowired
    private RedisTemplate<String,String> redisTemplate;

    @Autowired
    private PgService pgService;

    @Scheduled(fixedDelay = scheduledTime)
    @Async()
    public void sqlTask()
    {
        long begin = System.currentTimeMillis();
        ListOperations<String, String> listOperations = redisTemplate.opsForList();

        List<String> sqlList = listOperations.range("SqlList",0,-1);
        for(int i=0;i< sqlList.size();++i)
        {
            listOperations.leftPop("SqlList");
        }

        //assert sqlList != null;
        if(sqlList.size()>0)
        {
            long start = System.currentTimeMillis();
            pgService.executeSql(sqlList);
            long stop = System.currentTimeMillis();
            log.info("Sql size: " + sqlList.size());
            log.info("JDBC execute time(millis): " + (stop-start));
        }
        long end = System.currentTimeMillis();
        log.info("SqlTask total executing time(millis) "+(end-begin));
    }

    @Scheduled(fixedDelay = scheduledTime)
    @Async()
    public synchronized void batch_1()
    {
        long begin = System.currentTimeMillis();
        ListOperations<String, String> listOperations = redisTemplate.opsForList();

        List<String> sqlList = listOperations.range("batch_1",0,-1);
        for(int i=0;i< sqlList.size();++i)
        {
            listOperations.leftPop("batch_1");
        }
        if(sqlList.size()>0)
        {
            long start = System.currentTimeMillis();
            pgService.executeSql(sqlList);
            long stop = System.currentTimeMillis();
            log.info("sql size: " + sqlList.size());
            log.info("Batch_1 JDBC execute time(millis): " + (stop-start));
        }
        long end = System.currentTimeMillis();
        log.info("Batch_1 total executing time(millis) "+(end-begin));
    }

    @Scheduled(fixedDelay = scheduledTime)
    @Async()
    public synchronized void batch_2()
    {
        long begin = System.currentTimeMillis();
        ListOperations<String, String> listOperations = redisTemplate.opsForList();

        List<String> sqlList = listOperations.range("batch_2",0,-1);
        for(int i=0;i< sqlList.size();++i)
        {
            listOperations.leftPop("batch_2");
        }
        if(sqlList.size()>0)
        {
            long start = System.currentTimeMillis();
            pgService.executeSql(sqlList);
            long stop = System.currentTimeMillis();
            log.info("sql size: " + sqlList.size());
            log.info("Batch_2 JDBC execute time(millis): " + (stop-start));
        }
        long end = System.currentTimeMillis();
        log.info("Batch_2 total executing time(millis) "+(end-begin));
    }

    @Scheduled(fixedDelay = scheduledTime)
    @Async()
    public synchronized void batch_3()
    {
        long begin = System.currentTimeMillis();
        ListOperations<String, String> listOperations = redisTemplate.opsForList();

        List<String> sqlList = listOperations.range("batch_3",0,-1);
        for(int i=0;i< sqlList.size();++i)
        {
            listOperations.leftPop("batch_3");
        }
        if(sqlList.size()>0)
        {
            long start = System.currentTimeMillis();
            pgService.executeSql(sqlList);
            long stop = System.currentTimeMillis();
            log.info("sql size: " + sqlList.size());
            log.info("Batch_3 execute time(millis): " + (stop-start));
        }
        long end = System.currentTimeMillis();
        log.info("Batch_3 total executing time(millis) "+(end-begin));
    }
}
