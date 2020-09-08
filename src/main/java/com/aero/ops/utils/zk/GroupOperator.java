//package com.aero.ops.utils;
//
//import com.aero.ops.utils.zk.KafkaConf;
//import com.aero.ops.utils.zk.ZookeeperOperator;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.kafka.common.requests.MetadataResponse;
//
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.List;
//
///**
// * @author 罗涛
// * @title GroupOperator
// * @date 2020/8/24 18:31
// */
//
//@Slf4j
//public class GroupOperator {
//
//    private List<String> m_replicaBrokers;
//    private ZookeeperOperator zookeeper;
//    private String topic;
//    private List<String> seeds;
//    private int port;
//    private long whichTime;
//    private int partitionNum;
//    private String group;
//    private int retryNum = 5;
//
//    /**
//     * 初始化
//     *
//     * @param topic
//     * @param whichTime timestamp(13位)/-1(latest)/-2(earliest)
//     * @throws java.io.IOException
//     */
//    public GroupOperator(String topic, String group, long whichTime,  ZookeeperOperator zookeeper)
//            throws IOException {
//        this.topic = topic;
//        this.group = group;
//        this.whichTime = whichTime;
//        m_replicaBrokers = new ArrayList<String>();
//        seeds = KafkaConf.getBrokerHost();
//        port = Integer.parseInt(KafkaConf.BROKERPORT);
//        this.zookeeper = zookeeper;
//        partitionNum = zookeeper.readTopicChildren(topic);
//    }
//
//    /**
//     * 将zookeeper中该group对应该topic下的所有分区的offset恢复为所希望的时间
//     */
//    public boolean resetGroupOffset() {
//        List<Long> offsetList = new ArrayList<Long>();
//        for (int partition = 0; partition < partitionNum; partition++) {
//            long offset = getOffset(partition);
//            if (offset == -1) {
//                log.error("Failed to get offset of " + group + " with partition:"
//                        + partition);
//                return false;
//            } else {
//                offsetList.add(offset);
//            }
//        }
//        for (int partition = 0; partition < partitionNum; partition++) {
//            boolean isSuccess = zookeeper
//                    .setTopicGroupOffset(topic, group, String.valueOf(partition),
//                            String.valueOf(offsetList.get(partition)));
//            if (!isSuccess) {
//                log.error("Failed to reset offset of topic:" + topic + " group:" + group
//                        + " partition" + partition + " value:" + offsetList.get(partition));
//                return false;
//            }
//        }
//        return true;
//    }
//
//    public boolean retryResetGroupOffset() {
//        for (int retry = 0; retry < retryNum; retry++) {
//            if (resetGroupOffset()) {
//                return true;
//            }
//        }
//        return false;
//    }
//
//    /**
//     * 获取该partition要恢复时间的offset
//     *
//     * @param partition
//     * @return
//     */
//    public long getOffset(int partition) {
//        // find the meta data about the topic and partition we are interested in
//        PartitionMetadata metadata = findLeader(partition);
//        if (metadata == null) {
//            log.error("Can't find metadata for Topic and Partition. Exiting");
//            return -1;
//        }
//        if (metadata.leader() == null) {
//            log.error("Can't find Leader for Topic and Partition. Exiting");
//            return -1;
//        }
//        String leadBroker = metadata.leader().host();
//        String clientName = "Client_" + topic + "_" + partition;
//
//        SimpleConsumer consumer = new SimpleConsumer(leadBroker, port, 100000,
//                64 * 1024, clientName);
//        long readOffset = getAssignedOffset(consumer, partition, clientName);
//        if (consumer != null) {
//            consumer.close();
//        }
//        return readOffset;
//    }
//
//    /**
//     * Finding the Lead Broker for a Topic and Partition
//     *
//     * @param a_partition 分区id，从0开始
//     * @return
//     */
//    private MetadataResponse.PartitionMetadata findLeader(int a_partition) {
//        MetadataResponse.PartitionMetadata returnMetaData = null;
//        loop:
//        for (String seed : seeds) {
//            SimpleConsumer consumer = null;
//            try {
//                consumer = new SimpleConsumer(seed, port, 100000, 64 * 1024,
//                        "leaderLookup");
//                List<String> topics = Collections.singletonList(topic);
//                TopicMetadataRequest req = new TopicMetadataRequest(topics);
//                kafka.javaapi.TopicMetadataResponse resp = consumer.send(req);
//
//                List<MetadataResponse.TopicMetadata> metaData = resp.topicsMetadata();
//                for (MetadataResponse.TopicMetadata item : metaData) {
//                    for (MetadataResponse.PartitionMetadata part : item.partitionsMetadata()) {
//                        if (part.partitionId() == a_partition) {
//                            returnMetaData = part;
//                            break loop;
//                        }
//                    }
//                }
//            } catch (Exception e) {
//                log.error("Error communicating with Broker [" + seed
//                        + "] to find Leader for [" + topic + ", " + a_partition
//                        + "] Reason: " + e);
//            } finally {
//                if (consumer != null)
//                    consumer.close();
//            }
//        }
//        if (returnMetaData != null) {
//            m_replicaBrokers.clear();
//            for (kafka.cluster.Broker replica : returnMetaData.replicas()) {
//                m_replicaBrokers.add(replica.host());
//            }
//        }
//        return returnMetaData;
//    }
//
//    public long getAssignedOffset(SimpleConsumer consumer, int partition,
//                                  String clientName) {
//        TopicAndPartition topicAndPartition = new TopicAndPartition(topic,
//                partition);
//        Map<TopicAndPartition, PartitionOffsetRequestInfo> requestInfo = new HashMap<TopicAndPartition, PartitionOffsetRequestInfo>();
//        requestInfo
//                .put(topicAndPartition, new PartitionOffsetRequestInfo(whichTime, 1));
//        kafka.javaapi.OffsetRequest request = new kafka.javaapi.OffsetRequest(
//                requestInfo, kafka.api.OffsetRequest.CurrentVersion(), clientName);
//        OffsetResponse response = consumer.getOffsetsBefore(request);
//
//        if (response.hasError()) {
//            System.out.println(
//                    "Error fetching data Offset Data the Broker. Reason: " + response
//                            .errorCode(topic, partition));
//            return -1;
//        }
//        long[] offsets = response.offsets(topic, partition);
//        return offsets[0];
//    }
//
//}
