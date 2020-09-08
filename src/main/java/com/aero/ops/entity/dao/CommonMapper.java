package com.aero.ops.entity.dao;

import com.aero.ops.entity.po.BaseTablePO;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * CommonMapper
 *
 * @Author: taomee
 * @Date: 2020/7/4 0004 13:15
 * @Description:
 */
public interface CommonMapper {

    /**
     * 使用information_schema检查表是否存在
     * @param tableSchema
     * @param tableName
     * @return
     */
    Integer checkTableExistsWithSchema(@Param("tableSchema") String tableSchema, @Param("tableName") String tableName);

    /**
     * 使用show tables检查表是否存在
     * @param tableName
     * @return
     */
    Map<String, String> checkTableExistsWithShow(@Param("tableName") String tableName);

    /**
     * 查询所有表
     * @return
     */
    List<BaseTablePO> showTables();


    List<BaseTablePO> getTables(@Param("schemaTable") String schemaTable, @Param("database") String database);


    List<String> showDatabases();


    List<String> getColumns(@Param("database") String database, @Param("table") String table);
}
