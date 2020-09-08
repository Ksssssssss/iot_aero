package com.aero.ops.tasks;

import com.aero.common.constants.DateFormat;
import com.aero.common.utils.DateTimeUtil;
import com.aero.ops.constants.ClusterType;
import com.aero.ops.constants.ServiceType;
import com.aero.ops.entity.po.ClusterPO;
import com.aero.ops.entity.po.ServerPO;
import com.aero.ops.service.IClusterService;
import com.aero.ops.service.IServerService;
import com.aero.ops.utils.DingRobotUtil;
import com.aero.ops.utils.ReStartServiceUtil;
import com.aero.ops.utils.ReachableUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author 罗涛
 * @title ReachableMonitorTask
 * @date 2020/8/10 17:05
 */

@Slf4j
@Component
public class ReachableDetectTask {

    @Autowired
    IClusterService clusterService;

    @Autowired
    DingRobotUtil dingRobotUtil;

    @Autowired
    private IServerService serverService;


    @Scheduled(initialDelay = 10000L, fixedRate = 300000L)
    public void scheduledMonitor() {
        log.info("scheduled cluster health check...");

        Map<String, List<ClusterPO>> nodesMap = getServicesNodeMap();

        Map<String, List<String>> failServiceMap = findFailServices(nodesMap);

        handleFailServices(failServiceMap);

        List<String> failServices = Lists.newArrayList();
        failServiceMap.values().forEach(failServiceList -> failServices.addAll(failServiceList));
        clusterService.batchStatusUpdate(failServices, Boolean.FALSE);
    }

    private Map<String, List<ClusterPO>> getServicesNodeMap() {
        Map<String, List<ClusterPO>> nodesMap = new HashMap<>();
        Map<String, String> clusterTitleMap = new HashMap<>();
        List<ClusterPO> allCluster = clusterService.getAll();
        allCluster.sort(Comparator.comparing(ClusterPO::getId));

        for (ClusterPO cluster : allCluster) {
            if (cluster.getClusterType() == ClusterType.CLUSTER.getType()) {
                nodesMap.put(cluster.getTitle(), new ArrayList<>());
                clusterTitleMap.put(cluster.getId(), cluster.getTitle());
            } else {
                String clusterName = clusterTitleMap.get(cluster.getPid());
                List<ClusterPO> clusterPOS = nodesMap.get(clusterName);
                clusterPOS.add(cluster);
            }
        }
        return nodesMap;
    }

    private void handleFailServices(Map<String, List<String>> failServices) {
        if (!CollectionUtils.isEmpty(failServices)) {
            String dateTime = DateTimeUtil.time2Str(new Date(), DateFormat.CT_S);
            String quoteTime = StringUtils.join("【", dateTime, "】");
            String msg = quoteTime + " 以下集群节点无法访问，请及时检查：" + StringUtils.join(failServices.values().toArray(), ",");
            dingRobotUtil.sendDingTalkAlarm(msg);

            for (Map.Entry<String, List<String>> failServicesEntry : failServices.entrySet()) {
                String clusterName = failServicesEntry.getKey();
                List<String> services = failServicesEntry.getValue();

                String serviceName = ServiceType.transformServiceName(clusterName);
                if (serviceName.equals(ServiceType.NONE.serviceName)) {
                    log.warn("没有找到此服务,原服务的名字为 clusterName ：{}", clusterName);
                    return;
                }

                reStartServices(serviceName, services);
            }
        }
    }

    private Map<String, List<String>> findFailServices(Map<String, List<ClusterPO>> nodesMap) {
        Map<String, List<String>> failServiceMap = Maps.newHashMap();
        for (String clusterName : nodesMap.keySet()) {
            List<ClusterPO> clusterNodes = nodesMap.get(clusterName);
            List<String> failUrls = clusterHealthCheck(clusterName, clusterNodes);
            if (!CollectionUtils.isEmpty(failUrls)) {
                failServiceMap.put(clusterName, failUrls);
            }
        }
        return failServiceMap;
    }

    private void reStartServices(String serviceName, List<String> failServices) {
        final String delimiter = ":";
        final int ipPortArrayLen = 2;

        failServices.forEach(failService -> {
            String[] ipAndPortArray = failService.split(delimiter);
            if (ipAndPortArray.length < ipPortArrayLen) {
                log.error("failServices address is illegal,address : {}", failService);
                throw new IllegalArgumentException("failServices address is illegal");
            }

            String remoteIp = ipAndPortArray[0];
            ServerPO serverPo = serverService.getServerByLanIp(remoteIp);
            String localIp = System.getProperty("local-ip");
            String deployBasePath = serverPo.getDeployBasePath();

            if (remoteIp.equals(localIp)) {
                String cmd = ReStartServiceUtil.buildReStartOrder(serviceName);
                ReStartServiceUtil.execBat(deployBasePath, cmd);
                return;
            }

            String cmd = ReStartServiceUtil.buildRemoteReStartOrder(serviceName, serverPo);
            ReStartServiceUtil.execBat(deployBasePath, cmd);
        });
    }


    public List<String> clusterHealthCheck(String clusterName, List<ClusterPO> clusterNodes) {
        try {
            List<String> failUrls = new ArrayList<>();
            int size = clusterNodes.size();
            for (int i = 0; i < size; i++) {
                ClusterPO clusterNode = clusterNodes.get(i);
                String url = clusterNode.getUrl();
                boolean ping = ReachableUtil.ping(url);
                if (!ping) {
                    log.warn("{} 集群的节点:{},无法访问,请检查！", clusterName, url);
                    failUrls.add(url);
                }
            }
            return failUrls;
        } catch (Exception e) {
            log.error("健康检查发生异常：{}", e);
        }
        return Collections.emptyList();
    }
}
