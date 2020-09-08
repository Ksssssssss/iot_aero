package com.aero.ops.entity.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * @author 罗涛
 * @title KafkaLagInfoVO
 * @date 2020/8/14 15:19
 */

@Data
public class KafkaMetricVO {
    private String consumerGroup;
    private String topic;
    private Integer partition;
    private Integer offset;
    private Integer lag;
    private String brokerAddr;
    private String clientHost;

    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date lastMetricTime;
}
