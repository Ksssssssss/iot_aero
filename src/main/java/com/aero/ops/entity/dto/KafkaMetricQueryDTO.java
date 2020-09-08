package com.aero.ops.entity.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author 罗涛
 * @title KafkaQueryDTO
 * @date 2020/8/14 16:02
 */

@Data
public class KafkaMetricQueryDTO {

    @NotNull(message = "请先选择消费组")
    private String consumerGroup;

    private String topic;

    private Integer partition;

    private Integer rangeHours;
}
