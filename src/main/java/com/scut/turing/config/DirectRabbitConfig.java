package com.scut.turing.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DirectRabbitConfig {

    private String routingKeySql = "sql";

    private String routingKeyBatch_1 = "batch_1";

    private String routingKeyBatch_2 = "batch_2";

    private String routingKeyBatch_3 = "batch_3";
    @Bean
    public Queue sqlQueue()
    {
        return new Queue("sqlQueue",true);
    }

    @Bean
    public DirectExchange directSqlExchange()
    {
        return new DirectExchange("sql_exchange");
    }

    @Bean
    public Binding bindingDirectSql()
    {
        return BindingBuilder.bind(sqlQueue()).to(directSqlExchange()).with(routingKeySql);
    }

    @Bean
    public Queue tableBatch_1()
    {
        return new Queue("tableBatch_1",true);
    }

    @Bean Queue tableBatch_2()
    {
        return new Queue("tableBatch_2",true);
    }

    @Bean Queue tableBatch_3()
    {
        return new Queue("tableBatch_3",true);
    }
    @Bean
    public Binding bindingDirectBatch_1()
    {
        return BindingBuilder.bind(tableBatch_1()).to(directSqlExchange()).with(routingKeyBatch_1);
    }

    @Bean
    public Binding bindingDirectBatch_2()
    {
        return BindingBuilder.bind(tableBatch_2()).to(directSqlExchange()).with(routingKeyBatch_2);
    }

    @Bean
    public Binding bindingDirectBatch_3()
    {
        return BindingBuilder.bind(tableBatch_3()).to(directSqlExchange()).with(routingKeyBatch_3);
    }


}