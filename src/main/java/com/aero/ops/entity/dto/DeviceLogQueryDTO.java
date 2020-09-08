package com.aero.ops.entity.dto;

import com.aero.common.model.PageDTO;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Data
@EqualsAndHashCode(callSuper=false)
@ApiModel(value = "DeviceLogQueryDTO-设备日志查询条件")
public class DeviceLogQueryDTO extends PageDTO {

    @ApiModelProperty("服务IP")
    private String serverIp;

    @ApiModelProperty("传感器类型")
    private Integer sensorType;

    @ApiModelProperty("设备号")
    private String macPrefix;

    @ApiModelProperty("数据方向")
    private String direction;

    @ApiModelProperty("起始时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date startTime;

    @ApiModelProperty("结束时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date endTime;

    @ApiModelProperty("排序")
    private String timeSort;
}
