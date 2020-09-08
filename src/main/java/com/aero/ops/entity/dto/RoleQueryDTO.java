package com.aero.ops.entity.dto;

import com.aero.common.model.PageDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
@ApiModel("RoleQueryDTO-角色查询条件")
public class RoleQueryDTO extends PageDTO {

    @ApiModelProperty("角色名称")
    private String roleName;

    @ApiModelProperty("是否启用")
    private Boolean enable;
}
