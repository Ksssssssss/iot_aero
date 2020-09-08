package com.aero.ops.service;

import com.aero.ops.entity.dto.SqlServerQueryDTO;
import com.aero.ops.entity.po.BaseTablePO;

import java.sql.SQLException;
import java.util.List;

/**
 * ISqlServerService
 *
 * @Author: taomee
 * @Date: 2020/7/4 0004 13:22
 * @Description:
 */
public interface ISqlServerService {
    List<BaseTablePO> getAllTables();

    List<BaseTablePO> getTables(String database);

    List<String> getAllDatabases();

    List<String> getColumns(String database, String table);

    String query(SqlServerQueryDTO queryDTO) throws Exception;
}
