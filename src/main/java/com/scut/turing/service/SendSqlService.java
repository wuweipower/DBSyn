package com.scut.turing.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.Method;
@Service
@Slf4j
public class SendSqlService {
    @Autowired
    private RabbitTemplate rabbitTemplate;

    //1.定义交换机
    private String exchangeName = "sql_exchange";
    //2.路由key
    private String routeKey = "sql";

    public void sendSql(String sql)
    {
        //System.out.println("sql to be send: "+sql);
        //log.info("In method "+ Method.class+"  "+sql);
        //rabbitTemplate.convertAndSend(exchangeName,routeKey,sql);
        rabbitTemplate.convertAndSend("sql_exchange","sql",sql);
    }

    public void sendSqlBatch(String sql,String routeKey)
    {
        //System.out.println("sql to be send: "+sql);
        //log.info("In method "+ Method.class+"  "+sql);
        //rabbitTemplate.convertAndSend(exchangeName,routeKey,sql);
        rabbitTemplate.convertAndSend("sql_exchange",routeKey,sql);
    }

}
