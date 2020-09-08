package com.aero.ops.entity.dto;

import lombok.Data;

/**
 * @author 罗涛
 * @title MetricQueryDTO
 * @date 2020/8/20 14:43
 */
@Data
public class MetricQueryDTO {
    String serviceType;
    String metricSet;
    String coreParaKey;
    String hostName;
    Integer rangeHours;
}
