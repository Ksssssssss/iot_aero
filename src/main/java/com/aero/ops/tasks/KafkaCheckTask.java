package com.aero.ops.tasks;

import com.aero.common.constants.DateFormat;
import com.aero.common.utils.DateTimeUtil;
import com.aero.ops.entity.vo.KafkaMetricVO;
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

import java.util.*;

/**
 * @author 罗涛
 * @title ReachableMonitorTask
 * @date 2020/8/10 17:05
 */

@Slf4j
@Component
public class KafkaCheckTask {

    @Autowired
    IMetricService metricService;

    @Value("${monitor.warning.spec.kafka-lag}")
    Integer kafkaLagSpec;

    @Autowired
    DingRobotUtil dingRobotUtil;


    @Scheduled(initialDelay = 20000L, fixedRate = 300000L)
    public void scheduledServerCheck() {
        log.info("scheduled kafka health check...");
        List<KafkaMetricVO> lastKafkaMetrics = metricService.getLastKafkaMetric();
        if (CollectionUtils.isEmpty(lastKafkaMetrics)) {
            return;
        }
        Set<String> kafkaWarningNodes = new HashSet<>();
        Map<String, Integer> lagMap = new HashMap<>();
        for (KafkaMetricVO metric : lastKafkaMetrics) {
            Integer lag = metric.getLag();
            String topic = metric.getTopic();
            if (lag >= kafkaLagSpec && !"startup-monitor".equalsIgnoreCase(topic)) {
                String brokeSign = StringUtils.joinWith("=", "Broker", metric.getBrokerAddr());
                String clientSign = StringUtils.joinWith("=", "Client", metric.getClientHost());
                String nodeSign = StringUtils.joinWith("::", metric.getConsumerGroup(), topic, metric.getPartition(), brokeSign, clientSign);
                if (lagMap.containsKey(nodeSign) && (lag <= lagMap.get(nodeSign))) {
                    continue;
                }
                lagMap.put(nodeSign, lag);
                String msg = StringUtils.join(nodeSign, "【", lag, "】");
                kafkaWarningNodes.add(msg);
            }
        }

        if (!CollectionUtils.isEmpty(kafkaWarningNodes)) {
            String dateTime = DateTimeUtil.time2Str(new Date(), DateFormat.CT_S);
            String quoteTime = StringUtils.join("【", dateTime, "】");
            String msg = quoteTime + " 以下kafka节点消息积压超过阈值，请及时确认，警戒值：" + kafkaLagSpec + "，节点信息：" + StringUtils.join(kafkaWarningNodes.toArray(), ",");
            dingRobotUtil.sendDingTalkAlarm(msg);
        }
    }
}
