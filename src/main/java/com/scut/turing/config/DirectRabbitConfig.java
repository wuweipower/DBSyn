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
    @Bean
    public Queue sqlQueue()
    {
        return new Queue("sqlQueue",true);
    }

    @Bean
    public DirectExchange directSqlExchange()
    {
        return new DirectExchange("exchange_sql");
    }

    @Bean
    public Binding bindingDirectSql()
    {
        return BindingBuilder.bind(sqlQueue()).to(directSqlExchange()).with(routingKeySql);
    }

}
