package com.aero.ops.entity.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * @author 罗涛
 * @title CpuInfo
 * @date 2020/8/12 10:48
 */

@Data
public class ServerInfoVO {

    private String server;

    private double percent;

    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date time;
}
