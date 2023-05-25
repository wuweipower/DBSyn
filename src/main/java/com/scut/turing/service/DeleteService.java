package com.scut.turing.service;

import org.springframework.stereotype.Service;

import java.io.Serializable;

@Service
public class DeleteService {

    public static String deleteHandler(String tableName, Serializable[] keys,Serializable[] values){

        StringBuilder builder = new StringBuilder();
        builder.append("delete from ");
        builder.append(tableName);
        builder.append(" where ");

        return builder.toString();

    }
}
