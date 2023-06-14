package com.scut.turing.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.config.annotation.CorsRegistry;

@RestController
@Slf4j
public class SendSqlController {

    int cnt = 0;
    @GetMapping("/send")
    void revSql()
    {
        cnt++;
        //System.out.println("sss");
    }

//    @Async()
//    @Scheduled(fixedDelay = 10)
//    void printCount()
//    {
//        if(cnt%1000==0)
//        log.info("current mills "+System.currentTimeMillis()+" msg count rev: ="+cnt);
//    }

}
