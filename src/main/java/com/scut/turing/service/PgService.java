package com.scut.turing.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PgService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    //优化 一次执行多次sql
    public void executeSql(List<String> sql)
    {
        jdbcTemplate.batchUpdate(sql.toArray(new String[0]));
    }
}
