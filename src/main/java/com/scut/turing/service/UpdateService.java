package com.scut.turing.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
@Service
@Slf4j
public class UpdateService {

    @Autowired
    InsertService insertService;
    public  StringBuilder updateHandler(String tableName, Map.Entry<Serializable[], Serializable[]> row, List<String> cols){
        StringBuilder builder = new StringBuilder();
        builder.append("update ");
        builder.append(tableName);
        builder.append(" set ");
        Serializable[] before = row.getKey();
        Serializable[] after = row.getValue();
        for(int i=0;i<after.length;i++)
        {
            builder.append(cols.get(i).toString() + " = "+insertService.dataConvert(after[i]));
            if(i!=after.length-1)
            {
                builder.append(",");
            }
        }
        builder.append(" where ");
        for(int i=0;i<before.length;i++)
        {
            if(i==1)
            {
                builder.append(cols.get(i).toString()+" = "+insertService.dataConvert(before[i]));
            }
            else
            {
                builder.append(cols.get(i).toString()+" = "+insertService.dataConvert(before[i]));
            }
            if(i!= before.length-1)
            {
                builder.append(" and ");
            }

        }
        builder.append(";");
        //log.info(builder.toString());
        return builder;
    }
}
