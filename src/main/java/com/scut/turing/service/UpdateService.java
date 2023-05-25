package com.scut.turing.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
@Service
@Slf4j
public class UpdateService {

    public  StringBuilder updateHandler(String tableName, Map.Entry<Serializable[], Serializable[]> row, List<String> cols){
        StringBuilder builder = new StringBuilder();
        builder.append("update ");
        builder.append(tableName);
        builder.append(" set ");

//        Serializable[] keys = row.getKey();
//        Serializable[] values = row.getValue();
//        for(int i=0;i< keys.length;i++)
//        {
//            builder.append("`"+keys[i].toString()+"`="+values[i].toString()+"'");
//            if(i!= keys.length-1)
//            {
//                builder.append(",");
//            }
//        }
        Serializable[] before = row.getKey();
        Serializable[] after = row.getValue();
        for(int i=0;i<after.length;i++)
        {
            builder.append(cols.get(i).toString() + " = "+after[i]);
            if(i!=after.length)
            {
                builder.append(",");
            }
        }
        builder.append(" where ");
        for(int i=0;i<before.length;i++)
        {
            if(i==1)
            {
                builder.append(cols.get(i).toString()+" = "+before[i]);
            }
            else
            {
                builder.append(cols.get(i).toString()+" = "+before[i]+" and ");
            }
            if(i!= before.length)
            {
                builder.append(",");
            }

        }
        //log.info(builder.toString());
        return builder;
    }
}
