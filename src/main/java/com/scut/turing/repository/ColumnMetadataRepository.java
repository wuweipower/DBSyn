package com.scut.turing.repository;

import com.scut.turing.entity.ColumnMetadata;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ColumnMetadataRepository extends JpaRepository<ColumnMetadata,Long> {
    List<ColumnMetadata> findByTableSchemaAndTableName(String tableSchema, String tableName);

    List<ColumnMetadata> findByTableSchemaAndTableNameOrderByOrdinalPosition(String tableSchema, String tableName);

}
