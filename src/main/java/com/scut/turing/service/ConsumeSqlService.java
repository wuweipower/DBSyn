package com.scut.turing.service;



import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

// bindings其实就是用来确定队列和交换机绑定关系

@Service
@Slf4j
public class ConsumeSqlService {

    @Autowired
    private RedisService redisService;
    @RabbitListener(bindings = @QueueBinding(
            //sqlQueue是队列名字
            value= @Queue (value = "sql",autoDelete = "false"),
//        //sql_exchange是交换机的名字 必须和生产者保持一致
//        // 这里是确定的rabbitmq模式是：fanout 是以广播模式 、 发布订阅模式
            exchange = @Exchange(value = "sql_exchange",type = ExchangeTypes.DIRECT),
            key = {"sql"}
    ))
    public void msgReceive(String msg)
    {
        //log.info("sql receive: "+msg);
        redisService.SqlHandler(msg);
    }
}
