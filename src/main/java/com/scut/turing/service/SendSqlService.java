package com.scut.turing.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;

import java.lang.reflect.Method;

@Slf4j
public class SendSqlService {
    @Autowired
    private RabbitTemplate rabbitTemplate;

    //1.定义交换机
    private String exchangeName = "exchange_sql";
    //2.路由key
    private String routeKey = "sql";

    public void sendSql(String sql)
    {
        //System.out.println("sql to be send: "+sql);
        log.info("In method "+ Method.class+"  "+sql);
        rabbitTemplate.convertAndSend(exchangeName,routeKey,sql);
    }

}
