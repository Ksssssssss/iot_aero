//package com.aero.ops.utils.zk;
//
///**
// * @author 罗涛
// * @title ZkKafkaGroupDeleteTest
// * @date 2020/8/24 18:42
// */
//public class ZkKafkaGroupDeleteTest {
//    public static final String topic = "elk-bchd-acceptor";
//    public static final String group = "startup-monitor";
//
//    public static void main(String[] args) throws Exception{
//        ResetOffsetOperator.resetOffset(topic, group, null, whichTime);
//        ZookeeperOperator zookeeperOperator = new ZookeeperOperator();
//        zookeeperOperator.deleteUselessConsumer(group);
//    }
//}
