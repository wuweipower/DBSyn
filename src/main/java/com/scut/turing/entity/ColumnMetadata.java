package com.scut.turing.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;


@Entity
@Data
@Table(name = "COLUMNS", schema = "INFORMATION_SCHEMA")
public class ColumnMetadata {
    @Id
    private Long id;

    @Column(name = "TABLE_SCHEMA")
    private String tableSchema;

    @Column(name = "TABLE_NAME")
    private String tableName;

    @Column(name = "COLUMN_NAME")
    private String columnName;

    @Column(name = "DATA_TYPE")
    private String dataType;

    @Column(name = "ORDINAL_POSITION")
    private Integer ordinalPosition;
}
