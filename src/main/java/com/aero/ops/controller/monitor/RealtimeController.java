package com.aero.ops.controller.monitor;

import com.aero.common.model.ResponseModel;
import com.aero.common.model.ResultState;
import com.aero.ops.entity.dto.KafkaMetricQueryDTO;
import com.aero.ops.entity.dto.MetricQueryDTO;
import com.aero.ops.entity.vo.KafkaMetricVO;
import com.aero.ops.entity.vo.ServerInfoVO;
import com.aero.ops.entity.vo.EchartVO;
import com.aero.ops.entity.vo.MultiEchartVO;
import com.aero.ops.service.IMetricService;
import com.aero.ops.utils.ResultUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Slf4j
@RestController
@RequestMapping(value = "/realtime")
@Api(tags={"实时核心参数监控"}, value = "RealtimeController")
public class RealtimeController {

    @Autowired
    IMetricService metricService;

//    @RequestMapping(value = "/cpu", method = RequestMethod.GET)
//    public ResponseModel<List<EchartVO>> getCpuInfo(){
//        List<EchartVO> realtimeCpu = metricService.getRealtimeCpu();
//        return ResultUtils.me.success(realtimeCpu);
//    }
//
//    @RequestMapping(value = "/memory", method = RequestMethod.GET)
//    public ResponseModel<List<EchartVO>> getMemoryInfo(){
//        List<EchartVO> realtimeMemory = metricService.getRealtimeMemory();
//        return ResultUtils.me.success(realtimeMemory);
//    }

    @RequestMapping(value = "/cpu", method = RequestMethod.GET)
    public ResponseModel<MultiEchartVO> getCpuInfo(){
        MetricQueryDTO queryDTO = new MetricQueryDTO();
        queryDTO.setServiceType("system");
        queryDTO.setMetricSet("cpu");
        queryDTO.setCoreParaKey("system.cpu.user.norm.pct");
        queryDTO.setRangeHours(1);
        MultiEchartVO realtimeCpu = metricService.getRealtimeChart(queryDTO);
        return ResultUtils.me.success(realtimeCpu);
    }

    @RequestMapping(value = "/memory", method = RequestMethod.GET)
    public ResponseModel<MultiEchartVO> getMemoryInfo(){
        MetricQueryDTO queryDTO = new MetricQueryDTO();
        queryDTO.setServiceType("system");
        queryDTO.setMetricSet("memory");
        queryDTO.setCoreParaKey("system.memory.used.pct");
        queryDTO.setRangeHours(1);
        MultiEchartVO realtimeMemory  = metricService.getRealtimeChart(queryDTO);
        return ResultUtils.me.success(realtimeMemory);
    }

    @RequestMapping(value = "/tcp", method = RequestMethod.GET)
    public ResponseModel<MultiEchartVO> getTcpInfo(){
        MetricQueryDTO queryDTO = new MetricQueryDTO();
        queryDTO.setServiceType("system");
        queryDTO.setMetricSet("socket_summary");
        queryDTO.setCoreParaKey("system.socket.summary.tcp.all.established");
        queryDTO.setRangeHours(1);
        MultiEchartVO realtimeTcp  = metricService.getRealtimeChart(queryDTO);
        return ResultUtils.me.success(realtimeTcp);
    }

    @RequestMapping(value = "/udp", method = RequestMethod.GET)
    public ResponseModel<MultiEchartVO> getUdpInfo(){
        MetricQueryDTO queryDTO = new MetricQueryDTO();
        queryDTO.setServiceType("system");
        queryDTO.setMetricSet("socket_summary");
        queryDTO.setCoreParaKey("system.socket.summary.udp.all.count");
        queryDTO.setRangeHours(1);
        MultiEchartVO realtimeUdp  = metricService.getRealtimeChart(queryDTO);
        return ResultUtils.me.success(realtimeUdp);
    }

    @RequestMapping(value = "/custom", method = RequestMethod.POST)
    public ResponseModel<MultiEchartVO> customMetric(@RequestBody @Validated MetricQueryDTO queryDTO, BindingResult bindingResult){
        if(bindingResult.hasErrors()) {
            String tipMsg = bindingResult.getAllErrors().get(0).getDefaultMessage();
            log.error("查询参数错误：{}", tipMsg);
            return ResultUtils.error(ResultState.PARAM_ERROR, tipMsg);
        }
        MultiEchartVO realtimeMetric  = metricService.getRealtimeChart(queryDTO);
        return ResultUtils.me.success(realtimeMetric);
    }

    @RequestMapping(value = "/getAllServers", method = RequestMethod.GET)
    public ResponseModel<List<String>> getAllServers(@RequestParam(value = "serviceType") String serviceType, @RequestParam(value = "metricSet")  String metricSet){
        List<String> hosts = metricService.getSystemHosts(serviceType, metricSet, "host.name");
        return ResultUtils.me.success(hosts);
    }

    @RequestMapping(value = "/getAllFields", method = RequestMethod.GET)
    public ResponseModel<Set<String>> getAllFields(@RequestParam(value = "serviceType") String serviceType, @RequestParam(value = "metricSet")  String metricSet){
        Set<String> fields = metricService.getAllFields(serviceType, metricSet);
        return ResultUtils.me.success(fields);
    }



    public List<EchartVO> convertCpu(List<ServerInfoVO> cpuInfos){
        List<EchartVO> cpus = new ArrayList<>();
        for(ServerInfoVO cpu:cpuInfos){
            String name = cpu.getServer();
            Object[] value = new Object[]{cpu.getTime(), cpu.getPercent()};
            cpus.add(new EchartVO(name,value));
        }
        return cpus;
    }
}
