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
@ApiModel(value = "AppLogQueryDTO-微服务日志查询条件")
public class AppLogQueryDTO extends PageDTO {

    @ApiModelProperty("服务名称")
    private String appName;

    @ApiModelProperty("服务IP")
    private String serverIp;

    @ApiModelProperty("类名")
    private String className;

    @ApiModelProperty("线程名")
    private String threadName;

    @ApiModelProperty("消息关键字")
    private String keyword;

    @ApiModelProperty("日志级别")
    private String logLevel;

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

    @ApiModelProperty("原生查询语句")
    private String kql;
}
