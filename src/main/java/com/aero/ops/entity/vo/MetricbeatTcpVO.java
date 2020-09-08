package com.aero.ops.entity.vo;

import lombok.Data;

@Data
public class MetricbeatTcpVO {
    private String hostName;
    private String hostId;
    private Integer established;
    private Integer closeWait;
    private Integer timeWait;
    private Integer listening;
}
