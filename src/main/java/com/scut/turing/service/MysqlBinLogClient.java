package com.scut.turing.service;

import com.github.shyiko.mysql.binlog.BinaryLogClient;
import com.github.shyiko.mysql.binlog.event.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class MysqlBinLogClient implements ApplicationRunner {
    @Value("${mysql.host")
    private String host;

    @Value("${mysql.port}")
    private Integer port;

    @Value("${mysql.username}")
    private String username;

    @Value("${mysql.password}")
    private String password;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        new Thread(()->{
            while(true)
            {
                threadTest();
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }

        }).start();
    }

    public void threadTest()
    {
        System.out.println("1");
    }
    public void connectMysqlBinlog()
    {
        BinaryLogClient client = new BinaryLogClient(host,port,username,password);
        client.setServerId(2l);

        client.registerEventListener(event -> {
            EventData data = event.getData();

            QueryEventData queryEventData= event.getData();
            String sql = queryEventData.getSql();
            System.out.println(sql);

            //Event e;
            if(data instanceof TableMapEventData)
            {
                System.out.println("Table: ");
                TableMapEventData tableMapEventData = (TableMapEventData) data;
                System.out.println(tableMapEventData.getTableId());
            }
            if(data instanceof UpdateRowsEventData)
            {
                System.out.println("Update: ");
                System.out.println(data.toString());
            } else if (data instanceof WriteRowsEventData)
            {
                System.out.println("Insert: ");
                System.out.println(data.toString());
            } else if (data instanceof DeleteRowsEventData) {
                System.out.println("Delete: ");
                System.out.println(data.toString());
            }
        });
        try {
            client.connect();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
