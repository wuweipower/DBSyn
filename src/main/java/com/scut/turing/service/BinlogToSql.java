package com.scut.turing.service;

import com.github.shyiko.mysql.binlog.BinaryLogClient;
import com.github.shyiko.mysql.binlog.event.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class BinlogToSql implements ApplicationRunner {

    @Value("${mysql.host}")
    private String host;

    @Value("${mysql.port}")
    private Integer port;

    @Value("${mysql.username}")
    private String username;

    @Value("${mysql.password}")
    private String password;

    private Map<Long,String>tableMap;

    @Autowired
    private SendSqlService sendSqlService;

    @Autowired
    private InsertService insertService;

    @Autowired
    private UpdateService updateService;

    @Autowired
    private DeleteService deleteService;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        new Thread(()->{
            connectMysqlBinlog();
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
            StringBuilder sql = null;//存储要发送的sql

            //存tableId和对应的名字
            if(data instanceof TableMapEventData)
            {
                TableMapEventData tableMapEventData = (TableMapEventData) data;
                log.info(tableMapEventData.toString());
                Long tableID = tableMapEventData.getTableId();
                String tableName = tableMapEventData.getTable();
                tableMap.put(tableID,tableName);
            }
            /**
             * 分别对insert update delete进行sql转化
             *
             * */
            if (data instanceof WriteRowsEventData)
            {
                WriteRowsEventData eventData = (WriteRowsEventData) data;
                String table = tableMap.get(eventData.getTableId());

                for(Object[] row : eventData.getRows())
                {
                    sql= insertService.insertHandler(table, row);
                }
            }
            else if(data instanceof UpdateRowsEventData)
            {
                UpdateRowsEventData eventData = (UpdateRowsEventData) data;
                String table = tableMap.get(eventData.getTableId());
                List<Map.Entry<Serializable[], Serializable[]>> rows = eventData.getRows();
                for(Map.Entry<Serializable[], Serializable[]> row : rows )
                {
                    sql= updateService.updateHandler(table, row);
                }
            }
            else if (data instanceof DeleteRowsEventData) {
                DeleteRowsEventData eventData = (DeleteRowsEventData) data;
                String table = tableMap.get(eventData.getTableId());
                List<Serializable[]> rows = eventData.getRows();
                for (Serializable[] row:rows) {

                }
            }
            //最后发送出去
            sendSqlService.sendSql(sql.toString());
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
