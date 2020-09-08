package com.aero.ops.entity.dto;

import com.aero.common.model.PageDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @author 罗涛
 * @title SqlServerQueryDTO
 * @date 2020/8/19 17:10
 */
@Data
@EqualsAndHashCode(callSuper=false)
@ApiModel("SqlServerQueryDTO-MSSERVER查询条件")
public class SqlServerQueryDTO extends PageDTO {
    @ApiModelProperty("数据库名称")
    private String database;

    @ApiModelProperty("表名称")
    private String table;

    @ApiModelProperty("排序字段")
    @NotBlank(message = "排序字段不能为空")
    private String orderField;

    @ApiModelProperty("sql查询语句")
    @NotBlank(message = "sql查询语句不能为空")
    private String query;
}
