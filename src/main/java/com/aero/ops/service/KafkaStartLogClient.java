package com.aero.ops.service;

import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.PartitionInfo;
import org.apache.kafka.common.TopicPartition;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * @author 罗涛
 * @title KafkaClient
 * @date 2020/7/21 9:55
 */
@Slf4j
public class KafkaStartLogClient {
    private String monitorIp;
    private String topic;
    private long startTimeOut;
    private String servers;
    private WebSocketSession session;
    private CountDownLatch latch;
//    KafkaConsumer<String, String> consumer;
    Properties props;

    /**
     * 消费完后自动删除消费组：
     *  1. ENABLE_AUTO_COMMIT_CONFIG 设为 false
     *  2. 不设置group.id (ConsumerConfig.GROUP_ID_CONFIG)
     *  3. 不使用subscribe方法，而是采用assign，seek
     */
    public KafkaStartLogClient(String monitorIp, String topic, long startTimeOut, String servers, WebSocketSession session, CountDownLatch latch) {
        this.monitorIp = monitorIp;
        this.topic = topic;
        this.startTimeOut = startTimeOut;
        this.servers = servers;
        this.session = session;
        this.latch = latch;

        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, servers);
        // 设置 enable.auto.commit,偏移量由 auto.commit.interval.ms 控制自动提交的频率。
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);
        props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        this.props = props;
//        consumer = new KafkaConsumer<>(props);
    }


    public void startLogMonitor(){
        try (Consumer<String, String> consumer = new KafkaConsumer<>(props)) {
            List<PartitionInfo> partitionInfos = consumer.partitionsFor(topic);
            Collection<TopicPartition> topicPartitions = getTopicPartitions(partitionInfos);
            consumer.assign(topicPartitions);
            consumer.seekToEnd(topicPartitions);

            long start = System.currentTimeMillis();
            String projectName = topic.substring(4);
            outterLoop:  while (true) {
                try {
                    long now = System.currentTimeMillis();
                    long offset = now - start;
                    if (offset > startTimeOut) {
                        log.warn("服务部署超时，project: {}, ip: {}", projectName, monitorIp);
                        String timeoutMsg = "服务：" + projectName + "在服务器：" + monitorIp + "部署超时(" + startTimeOut + " ms)";
                        String fmtMsg = formatErrorMsg(timeoutMsg);
                        session.sendMessage(new TextMessage(fmtMsg));
                        break outterLoop;
                    }
                    ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100L));
                    for (ConsumerRecord<String, String> record : records) {
                        //超过10分钟以上的消息没必要展示
                        long msgTime = record.timestamp();
                        long lagOffset = now - msgTime;
                        if (lagOffset > 10 * 60 * 1000L) {
                            continue;
                        }
                        String msg = record.value();
                        String monitorIpSign = StringUtils.join("|", monitorIp, "|");
                        if (msg.contains(monitorIpSign)) {
                            synchronized (session) {
                                session.sendMessage(new TextMessage(msg));
                            }
                            String successTip = "seconds (JVM running for ";
                            if (msg.contains(successTip) && offset > 3000) {
                                log.info("服务部署成功，project: {}, ip: {}", projectName, monitorIp);
                                latch.countDown();
                                break outterLoop;
                            }
                        }
                    }
                } catch (IOException e) {
                    log.error("监听启动日志时发生异常：{}", e);
                    break outterLoop;
                }
            }
        }
    }


    /**
     * List<PartitionInfo> 转 Collection<TopicPartition>
     * @param partitionInfos :List<PartitionInfo>
     * @return :Collection<TopicPartition>
     */
    private static Collection<TopicPartition> getTopicPartitions(List<PartitionInfo> partitionInfos) {
        ArrayList<TopicPartition> topicPartitions = Lists.newArrayList();
        for (PartitionInfo partitionInfo : partitionInfos) {
            TopicPartition topicPartition = new TopicPartition(partitionInfo.topic(), partitionInfo.partition());
            topicPartitions.add(topicPartition);
        }
        return topicPartitions;
    }



    private String formatErrorMsg(String error){
        StringBuilder sb = new StringBuilder();
        sb.append("<span style='color:red;font-weight:bold'>");
        sb.append(error);
        sb.append("</span>");
        return sb.toString();
    }
}
