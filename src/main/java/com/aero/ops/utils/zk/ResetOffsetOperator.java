//package com.aero.ops.utils.zk;
//
//import com.aero.ops.utils.GroupOperator;
//import org.apache.commons.lang3.StringUtils;
//
//import java.io.IOException;
//import java.util.Properties;
//
///**
// * @author 罗涛
// * @title ResetOperator
// * @date 2020/8/24 18:40
// */
//public class ResetOffsetOperator {
//    public static boolean resetOffset(final String topic, String group,
//                                      Properties properties, long whichTime) throws IOException {
//        if (StringUtils.isBlank(topic) || StringUtils.isBlank(group)) {
//            System.err.println("topic or group can not be null or empty!");
//            System.exit(2);
//        }
//        if (properties == null) {
//            properties = new Properties();
//            properties.setProperty("zookeeper.connect", KafkaConf.ZOOKEEPERHOST);
//            properties.setProperty("group.id", group);
//            // zookeeper最大超时时间，就是心跳的间隔，若是没有反映，那么认为已经死了，不易过大
//            properties.setProperty("zookeeper.session.timeout.ms", "10000");
//            // ZooKeeper集群中leader和follower之间的同步时间
//            properties.setProperty("zookeeper.sync.time.ms", "2000");
//            // 自动提交offset到zookeeper的时间间隔
//            properties.setProperty("auto.commit.interval.ms", "3000");
//            // 当zookeeper中没有初始的offset时候的处理方式 。smallest ：重置为最小值 largest:重置为最大值 anything else：抛出异常
//            properties.setProperty("auto.offset.reset", "largest");
//        }
//
//        ZookeeperOperator zookeeper = new ZookeeperOperator(
//                properties.getProperty("zookeeper.connect"));
//
//        GroupOperator groupOperator = new GroupOperator(topic, group, whichTime,
//                zookeeper);
//        return groupOperator.retryResetGroupOffset();
//    }
//}
