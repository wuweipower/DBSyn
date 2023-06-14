package com.scut.turing.service;

import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.sql.Time;
import java.sql.Timestamp;
import java.sql.Date;

@Service
public class InsertService {
    public  String dataConvert(Object value)
    {
        String res;
        if(value==null) res = "NULL";
        else if(value instanceof String) res = "'" + ((String) value).replaceAll("'", "''") + "'";
        else if(value instanceof Timestamp) res = "'" + ((Timestamp) value).toString() + "'";
        else if(value instanceof byte[]) res = "'" + new String((byte[]) value) + "'";
        else if(value instanceof Date) res = "'" + ((Date) value).toString() + "'";
        else if(value instanceof Time) res = "'" + ((Time) value).toString() +"'";
        else if(value instanceof java.util.Date) res = "'" + ((java.util.Date) value).toString() + "'";
        else res = value.toString();
        return res;
    }

    public  StringBuilder insertHandler(String tableName, Object[] row)
    {
        StringBuilder builder = new StringBuilder();
        builder.append("insert into ");
        builder.append(tableName);

        builder.append( " values (");
        for(int i =0;i<row.length;i++)
        {
            Object value = row[i];
            builder.append(dataConvert(value));
            if(i< row.length-1)
            {
                builder.append(", ");
            }
        }
        builder.append(");");

        return builder;
    }
}
