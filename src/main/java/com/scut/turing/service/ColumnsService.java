package com.scut.turing.service;

import com.scut.turing.entity.ColumnMetadata;
import com.scut.turing.repository.ColumnMetadataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ColumnsService {
    @Autowired
    private ColumnMetadataRepository columnMetadataRepository;

    public List<String> getColumnNames(String databaseName, String tableName) {
        List<ColumnMetadata> columns = columnMetadataRepository.findByTableSchemaAndTableName(databaseName, tableName);
        // 或者
        // List<ColumnMetadata> columns = columnMetadataRepository.findByTableSchemaAndTableNameOrderByOrdinalPosition(databaseName, tableName);
        return columns.stream().map(ColumnMetadata::getColumnName).collect(Collectors.toList());
    }
}
