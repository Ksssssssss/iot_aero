package com.aero.ops.tasks;

import com.aero.common.constants.DateFormat;
import com.aero.common.utils.DateTimeUtil;
import com.aero.ops.entity.vo.ServerInfoVO;
import com.aero.ops.service.IMetricService;
import com.aero.ops.utils.DingRobotUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author 罗涛
 * @title ReachableMonitorTask
 * @date 2020/8/10 17:05
 */

@Slf4j
@Component
public class ServerCheckTask {

    @Autowired
    IMetricService realtimeService;

    @Value("${monitor.warning.spec.cpu}")
    double cpuWarningSpec;

    @Value("${monitor.warning.spec.memory}")
    double memoryWarningSpec;

    @Autowired
    DingRobotUtil dingRobotUtil;


    @Scheduled(initialDelay = 30000L, fixedRate = 300000L)
    public void scheduledServerCheck(){
        log.info("scheduled server health check...");
        Set<String> cpuWarningServers = new HashSet<>();
        Set<String> memoryWarningServers = new HashSet<>();
        List<ServerInfoVO> lastCpuMetrics = realtimeService.getLastCpuMetric();
        List<ServerInfoVO> lastMemoryMetrics = realtimeService.getLastMemoryMetric();
        if (!CollectionUtils.isEmpty(lastCpuMetrics)) {
            for(ServerInfoVO info: lastCpuMetrics){
                if(info.getPercent()>cpuWarningSpec){
                    log.warn("服务器：{}, CPU利用率：{}, 超过警戒值:{}, 发生时间：{}", info.getServer(), info.getPercent(),cpuWarningSpec, info.getTime());
                    cpuWarningServers.add(info.getServer());
                }
            }
        }else {
           log.warn("近5分钟没有服务器CPU数据，请确认MetricBeat集群是否正常工作！");
        }
        if (!CollectionUtils.isEmpty(lastCpuMetrics)) {
            for (ServerInfoVO info : lastMemoryMetrics) {
                if (info.getPercent() > memoryWarningSpec) {
                    log.warn("服务器：{}, 内存占用：{}, 超过警戒值:{}, 发生时间：{}", info.getServer(), info.getPercent(), memoryWarningSpec, info.getTime());
                    memoryWarningServers.add(info.getServer());
                }
            }
        }else {
            log.warn("近5分钟没有服务器内存数据，请确认MetricBeat集群是否正常工作！");
        }

        if (!CollectionUtils.isEmpty(cpuWarningServers)) {
            String dateTime = DateTimeUtil.time2Str(new Date(), DateFormat.CT_S);
            String quoteTime = StringUtils.join("【", dateTime, "】");
            String msg = quoteTime + " 以下服务器CPU超负荷，请及时确认，警戒值：" + cpuWarningSpec + "，服务器：" + StringUtils.join(cpuWarningServers.toArray(),",");
            dingRobotUtil.sendDingTalkAlarm(msg);
        }
        if (!CollectionUtils.isEmpty(memoryWarningServers)) {
            String dateTime = DateTimeUtil.time2Str(new Date(), DateFormat.CT_S);
            String quoteTime = StringUtils.join("【", dateTime, "】");
            String msg = quoteTime + " 以下服务器内存超负荷，请及时确认，警戒值：" + memoryWarningSpec + "，服务器：" + StringUtils.join(memoryWarningServers.toArray(),",");
            dingRobotUtil.sendDingTalkAlarm(msg);
        }
    }
}
