package com.scut.turing.repository;

import com.scut.turing.entity.ColumnMetadata;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ColumnMetadataRepository extends JpaRepository<ColumnMetadata,Long> {
    //    List<ColumnMetadata> findByTableSchemaAndTableName(String tableSchema, String tableName);
//
//    List<ColumnMetadata> findByTableSchemaAndTableNameOrderByOrdinalPosition(String tableSchema, String tableName);
    @Query(value =
            "SELECT COLUMN_NAME FROM information_schema.COLUMNS WHERE TABLE_NAME = ?1 ORDER BY ORDINAL_POSITION",
            nativeQuery = true)
    List<String> findAllCols(String table);

}