package com.scut.turing.service;


import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class MultiQueueTestService {
    @Autowired
    private RedisService redisService;

    @RabbitListener(bindings = @QueueBinding(
            //sqlQueue是队列名字
            value= @Queue(value = "tableBatch_1",autoDelete = "false"),
//        //sql_exchange是交换机的名字 必须和生产者保持一致
//        // 这里是确定的rabbitmq模式是：fanout 是以广播模式 、 发布订阅模式
            exchange = @Exchange(value = "sql_exchange",type = ExchangeTypes.DIRECT),
            key = {"batch_1"}
    ))
    public void msgReceive_1(String msg)
    {
        redisService.SqlHandler_1(msg);
    }

    @RabbitListener(bindings = @QueueBinding(
            //sqlQueue是队列名字
            value= @Queue(value = "tableBatch_2",autoDelete = "false"),
//        //sql_exchange是交换机的名字 必须和生产者保持一致
//        // 这里是确定的rabbitmq模式是：fanout 是以广播模式 、 发布订阅模式
            exchange = @Exchange(value = "sql_exchange",type = ExchangeTypes.DIRECT),
            key = {"batch_2"}
    ))
    public void msgReceive_2(String msg)
    {
        redisService.SqlHandler_2(msg);
    }

    @RabbitListener(bindings = @QueueBinding(
            //sqlQueue是队列名字
            value= @Queue(value = "tableBatch_3",autoDelete = "false"),
//        //sql_exchange是交换机的名字 必须和生产者保持一致
//        // 这里是确定的rabbitmq模式是：fanout 是以广播模式 、 发布订阅模式
            exchange = @Exchange(value = "sql_exchange",type = ExchangeTypes.DIRECT),
            key = {"batch_3"}
    ))
    public void msgReceive_3(String msg)
    {
        redisService.SqlHandler_3(msg);
    }

}
