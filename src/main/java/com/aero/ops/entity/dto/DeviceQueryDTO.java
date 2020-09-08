package com.aero.ops.entity.dto;

import com.aero.common.model.PageDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
@ApiModel("DeviceQueryDTO-设备查询条件")
public class DeviceQueryDTO extends PageDTO {

    @ApiModelProperty("Mac关键字")
    private String mac;

    @ApiModelProperty("设备名称-sensorName关键字")
    private String sensorName;

    @ApiModelProperty("在线状态（0-所有 1-在线 2-离线）")
    private Integer status;

    @ApiModelProperty("设备类型")
    private Integer sensorType;
}
