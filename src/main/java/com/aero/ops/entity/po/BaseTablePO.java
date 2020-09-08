package com.aero.ops.entity.po;

import lombok.Data;

/**
 * BaseTablePO
 *
 * @Author: taomee
 * @Date: 2020/7/4 0004 14:05
 * @Description:
 */
@Data
public class BaseTablePO {
    private String tableCatalog;
    private String tableSchema;
    private String tableName;
    private String tableType;
}
