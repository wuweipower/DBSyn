package com.scut.turing.service;

import java.io.Serializable;

public class UpdateService {

    public static String updateHandler(String tableName, Serializable[] before, Serializable[] after){
        StringBuilder builder = new StringBuilder();
        builder.append("update ");
        builder.append(tableName);
        builder.append(" set ");


        return builder.toString();
    }
}
