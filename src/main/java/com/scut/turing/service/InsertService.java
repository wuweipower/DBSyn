package com.scut.turing.service;

import java.sql.Timestamp;

public class InsertService {
    public static String dataConvert(Object value)
    {
        String res;
        if(value==null) res = "NULL";
        else if(value instanceof String) res = "'" + ((String) value).replaceAll("'", "''") + "'";
        else if(value instanceof Timestamp) res = "'" + ((Timestamp) value).toString() + "'";
        else if(value instanceof byte[]) res = "'" + new String((byte[]) value) + "'";
        else res = value.toString();
        return res;
    }

    public static String insertHandler(String tableName, Object[] row)
    {
        StringBuilder builder = new StringBuilder();
        builder.append("insert into ");
        builder.append(tableName);
        builder.append( " values (");

        //String sql = "insert into "+tableName+" VALUES (";
        for(int i =0;i<row.length;i++)
        {
            Object value = row[i];
            builder.append(dataConvert(value));
            if(i< row.length-1)
            {
                builder.append(", ");
            }
        }
        builder.append(")");

        return builder.toString();
    }
}
