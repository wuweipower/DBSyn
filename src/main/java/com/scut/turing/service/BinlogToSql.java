package com.scut.turing.service;

import com.github.shyiko.mysql.binlog.BinaryLogClient;
import com.github.shyiko.mysql.binlog.event.*;

import com.github.shyiko.mysql.binlog.event.deserialization.EventDeserializer;
import com.github.shyiko.mysql.binlog.event.deserialization.UpdateRowsEventDataDeserializer;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.tool.schema.extract.spi.ColumnTypeInformation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.Serializable;
import java.util.BitSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

//    @Autowired
    private Map<Long,String>tableMap;

//    @Autowired
//    private Map<String,List<String>> colMap;

    @Autowired
    private SendSqlService sendSqlService;

    @Autowired
    private InsertService insertService;

    @Autowired
    private UpdateService updateService;

    @Autowired
    private DeleteService deleteService;

    @Autowired
    private ColumnsService columnsService;

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
        tableMap = new HashMap<>();
        client.registerEventListener(event -> {
            EventData data = event.getData();
            StringBuilder sql = new StringBuilder();//存储要发送的sql
            //存tableId和对应的名字
            if(data instanceof TableMapEventData)
            {
                TableMapEventData tableMapEventData = (TableMapEventData) data;
                //log.info(tableMapEventData.toString());
                Long tableID = tableMapEventData.getTableId();
                String tableName = tableMapEventData.getTable();
                tableMap.put(tableID,tableName);
                log.info(columnsService.getColumnNames(tableMapEventData.getDatabase(),tableMapEventData.getTable()).toString());
            }
            /**
             * 分别对insert update delete进行sql转化
             *
             * */
            if (data instanceof WriteRowsEventData)
            {
                WriteRowsEventData eventData = (WriteRowsEventData) data;
                //QueryEventData queryEventData = (QueryEventData) data;
                //log.info(queryEventData.toString());
                String table = tableMap.get(eventData.getTableId());
                log.info(eventData.getRows().toString());
                for(Object[] row : eventData.getRows())
                {
                    sql= insertService.insertHandler(table, row);
                    log.info(sql.toString());
                    sendSqlService.sendSql(sql.toString());
                }
            }
            else if(data instanceof UpdateRowsEventData)
            {
                UpdateRowsEventData eventData = (UpdateRowsEventData) data;
                String table = tableMap.get(eventData.getTableId());

                BitSet includedColumns = eventData.getIncludedColumns();
                BitSet includedColumnsBeforeUpdate = eventData.getIncludedColumnsBeforeUpdate();
                List<Map.Entry<Serializable[], Serializable[]>> rows = eventData.getRows();
                List<String> columnNames = columnsService.getColumnNames("",table);
                for(Map.Entry<Serializable[], Serializable[]> row : rows )
                {
                    sql = updateService.updateHandler(table,row,columnNames);
                    log.info(sql.toString());
                    sendSqlService.sendSql(sql.toString());
                }
            }
            else if (data instanceof DeleteRowsEventData) {
                DeleteRowsEventData eventData = (DeleteRowsEventData) data;
                String table = tableMap.get(eventData.getTableId());
                List<String> columnNames = columnsService.getColumnNames("",table);
                List<Serializable[]> rows = eventData.getRows();
                for (Serializable[] row:rows)
                {
                    sql = deleteService.deleteHandler(table,row,columnNames);
                    log.info(sql.toString());
                    sendSqlService.sendSql(sql.toString());
                }
            }
            else if(data instanceof QueryEventData) {
                QueryEventData queryEventData = (QueryEventData) data;
                String str = queryEventData.getSql();
                System.out.println(str);
                if (!str.equals("BEGIN")) {
                    str = str.toLowerCase();
                    str = str.replaceAll("datetime", "date");
                    str = str.replaceAll("tinyint", "smallint");
                    str = str.replaceAll("blob", "bytea");
                    str = str.replace('`', ' ');
                    if (isFind(str,"auto_increment"))
                    {
                        String[] result = str.split(",");
                        for(int i=0;i< result.length;i++)
                        {
                            if (isFind(result[i],"auto_increment"))
                            {
                                result[i]=result[i].replaceAll("int","serial");
                                result[i]=result[i].replaceAll("unsigned","");
                                result[i] = result[i].replaceAll("auto_increment", "");
                            }
                            if (isFind(result[i],"unsigned"))
                            {
                                result[i]=result[i].replaceAll("int","");
                                result[i]=result[i].replaceAll("unsigned","bigint");
                            }
                        }
                        String newString="";
                        for(int i=0;i< result.length;i++)
                        {
                            newString+=result[i];
                            if(i!=result.length-1)
                                newString+=",";
                        }
                        str=newString;
                    }
                    if (isFind(str,"unsigned"))
                    {
                        String[] result = str.split(",");
                        for(int i=0;i< result.length;i++)
                        {
                            if (isFind(result[i],"unsigned"))
                            {
                                result[i]=result[i].replaceAll("int","");
                                result[i]=result[i].replaceAll("unsigned","bigint");
                            }
                        }
                        String newString="";
                        for(int i=0;i< result.length;i++)
                        {
                            newString+=result[i];
                            if(i!=result.length-1)
                                newString+=",";
                        }
                        str=newString;
                    }
                    str+=";";
                    System.out.println(str);
                    sendSqlService.sendSql(str);
                }
            }
            //最后发送出去
            //log.info("sql: ",sql.toString());
        });
        try {
            client.connect();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
    Boolean isFind(String str,String matchStr)
    {
        String pattern = matchStr;
        Pattern p = Pattern.compile(pattern);
        Matcher m = p.matcher(str);
        return m.find();
    }
}