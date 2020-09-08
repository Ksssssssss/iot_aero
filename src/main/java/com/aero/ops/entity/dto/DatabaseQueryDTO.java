package com.aero.ops.entity.dto;

import com.aero.common.model.PageDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
@ApiModel("DataBaseQueryDTO-设备查询条件")
public class DatabaseQueryDTO extends PageDTO {
    @ApiModelProperty("Database关键字")
    private String database;

    @ApiModelProperty("Datatable关键字")
    private String datatable;

    @ApiModelProperty("Query关键字")
    private String query;
}
