package com.aero.ops.service;

import com.aero.ops.entity.dto.KafkaMetricQueryDTO;
import com.aero.ops.entity.dto.MetricQueryDTO;
import com.aero.ops.entity.vo.EchartVO;
import com.aero.ops.entity.vo.KafkaMetricVO;
import com.aero.ops.entity.vo.MultiEchartVO;
import com.aero.ops.entity.vo.ServerInfoVO;

import java.util.List;
import java.util.Set;

/**
 * @author 罗涛
 * @title IMetricService
 * @date 2020/8/12 10:16
 */
public interface IMetricService {
    List<EchartVO> getRealtimeCpu();

    List<EchartVO> getRealtimeMemory();

    MultiEchartVO getRealtimeMultiCpu();

    MultiEchartVO getRealtimeMultiMemory();

    MultiEchartVO getRealtimeMultiSocket();

    MultiEchartVO getRealtimeChart(MetricQueryDTO queryDTO);

    List<ServerInfoVO> getLastCpuMetric();

    List<ServerInfoVO> getLastMemoryMetric();

    List<KafkaMetricVO> getLastKafkaMetric();

    List<KafkaMetricVO> getKafkaMetric(KafkaMetricQueryDTO queryDTO);

    MultiEchartVO getRealtimeMultiKafka(KafkaMetricQueryDTO queryDTO);

    List<Object> getClassifyField(String fieldKey);

    List<String> getSystemHosts(String serviceType, String metricSet, String classifyField);

    Set<String> getAllFields(String serviceType, String metricSet);
}
