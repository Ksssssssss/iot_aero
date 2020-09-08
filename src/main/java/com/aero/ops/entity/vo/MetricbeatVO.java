package com.aero.ops.entity.vo;

import lombok.Data;

@Data
public class MetricbeatVO {
    private Integer pid;
    private String name;
    private Long memorySize;
    private Double memoryPct;
    private Double cpuSize;
    private Double cpuPct;
    private String hostName;
    private String hostId;
}