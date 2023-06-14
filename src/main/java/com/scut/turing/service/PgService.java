package com.scut.turing.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

@Service
public class PgService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public void executeSql(List<String> sql)
    {
//        jdbcTemplate.batchUpdate(sql.toArray(sql.toArray(new String[0])));
//        for(String str : sql)
//        {
//            jdbcTemplate.execute(str);
//        }
        StringBuilder temp = new StringBuilder();
        for(int i=0;i<sql.size();++i)
        {
            temp.append(sql.get(i));
            if(i%1000==0)
            {
                jdbcTemplate.execute(temp.toString());
                temp.setLength(0);
            }
        }
        jdbcTemplate.execute(temp.toString());
    }
}
