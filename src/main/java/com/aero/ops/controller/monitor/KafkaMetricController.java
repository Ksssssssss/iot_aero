package com.aero.ops.controller.monitor;

import com.aero.common.model.ResponseModel;
import com.aero.ops.entity.dto.KafkaMetricQueryDTO;
import com.aero.ops.entity.vo.KafkaMetricVO;
import com.aero.ops.entity.vo.MultiEchartVO;
import com.aero.ops.service.IMetricService;
import com.aero.ops.utils.ResultUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "/kafkaMetric")
@Api(tags={"Kafka核心参数监控"}, value = "KafkaMetricController")
public class KafkaMetricController {

    @Autowired
    IMetricService metricService;


    @RequestMapping(value = "/getAllConsumerGroup", method = RequestMethod.GET)
    public ResponseModel<List<String>> getAllConsumerGroup(){
        List<Object> consumerGroups = metricService.getClassifyField("kafka.consumergroup.id");
        return ResultUtils.me.success(consumerGroups);
    }

    @RequestMapping(value = "/getAllTopics", method = RequestMethod.GET)
    public ResponseModel<List<String>> getAllTopics(){
        List<Object> topics = metricService.getClassifyField("kafka.consumergroup.topic");
        return ResultUtils.me.success(topics);
    }

    @RequestMapping(value = "/chartData", method = RequestMethod.POST)
    @ApiOperation(value = "kafka图表数据")
    public ResponseModel<MultiEchartVO> getKafkaChartData(@RequestBody KafkaMetricQueryDTO queryDTO){
        MultiEchartVO multiChart = metricService.getRealtimeMultiKafka(queryDTO);
        return ResultUtils.me.success(multiChart);
    }

    @RequestMapping(value = "/kafka", method = RequestMethod.POST)
    @ApiOperation(value = "kafka核心数据")
    public ResponseModel<List<KafkaMetricVO>> getKafkaMetric(@RequestBody KafkaMetricQueryDTO queryDTO){
        List<KafkaMetricVO> kafkaMetricVOS = metricService.getKafkaMetric(queryDTO);
        return ResultUtils.me.success(kafkaMetricVOS);
    }

}
