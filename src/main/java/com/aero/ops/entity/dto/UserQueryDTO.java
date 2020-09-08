package com.aero.ops.entity.dto;

import com.aero.common.model.PageDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
@ApiModel("UserQueryDTO-用户查询条件")
public class UserQueryDTO extends PageDTO {

    @ApiModelProperty("真实名字")
    private String realName;

    @ApiModelProperty("是否启用")
    private Boolean enable;

    @ApiModelProperty("角色")
    private Long roleId;
}
