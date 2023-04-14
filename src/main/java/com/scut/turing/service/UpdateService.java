package com.scut.turing.service;

import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.util.Map;

@Slf4j
public class UpdateService {

    public  StringBuilder updateHandler(String tableName, Map.Entry<Serializable[], Serializable[]> row){
        StringBuilder builder = new StringBuilder();
        builder.append("update ");
        builder.append(tableName);
        builder.append(" set ");

        Serializable[] keys = row.getKey();
        Serializable[] values = row.getValue();
        for(int i=0;i< keys.length;i++)
        {
            builder.append("`"+keys[i].toString()+"`="+values[i].toString()+"'");
            if(i!= keys.length-1)
            {
                builder.append(",");
            }
        }
        log.info(builder.toString());

        return builder;
    }
}
