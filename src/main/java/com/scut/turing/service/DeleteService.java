package com.scut.turing.service;

import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.List;

@Service
public class DeleteService {

    public StringBuilder deleteHandler(String tableName, Serializable[] before, List<String> cols){

        StringBuilder builder = new StringBuilder();
        builder.append("delete from ");
        builder.append(tableName);
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

        return builder;

    }
}
