//package com.aero.ops.utils.zk;
//
//import lombok.extern.slf4j.Slf4j;
//import org.apache.zookeeper.KeeperException;
//import org.apache.zookeeper.WatchedEvent;
//import org.apache.zookeeper.Watcher;
//import org.apache.zookeeper.ZooKeeper;
//
//import java.io.IOException;
//import java.util.List;
//
///**
// * @author 罗涛
// * @title ZookeeperOperator
// * @date 2020/8/24 18:26
// */
//@Slf4j
//public class ZookeeperOperator implements Watcher {
//    public static final String conn = "192.168.1.154:2181,192.168.1.152:2181,192.168.1.151:2181";
//
//    private ZooKeeper zooKeeper = null;
//
//    public ZookeeperOperator() throws IOException {
//        this(conn);
//    }
//
//    public ZookeeperOperator(String zookeeperHost) throws IOException {
//        zooKeeper = new ZooKeeper(zookeeperHost, 5000, ZookeeperOperator.this);
//    }
//
//    public void close() {
//        try {
//            zooKeeper.close();
//        } catch (InterruptedException e) {
//            log.error("Failed to close zookeeper:", e);
//        }
//    }
//
//    /**
//     * 读取指定节点下孩子节点数目
//     *
//     * @param path 节点path
//     * @return
//     */
//    private int readChildren(String path) {
//        try {
//            return zooKeeper.getChildren(path, false, null).size();
//        } catch (KeeperException e) {
//            log.error("Read error,KeeperException:" + path, e);
//            return 0;
//        } catch (InterruptedException e) {
//            log.error("Read error,InterruptedException:" + path, e);
//            return 0;
//        }
//    }
//
//    private List<String> getChildrenList(String path) {
//        try {
//            return zooKeeper.getChildren(path, false, null);
//        } catch (KeeperException e) {
//            log.error("Read error,KeeperException:" + path, e);
//            return null;
//        } catch (InterruptedException e) {
//            log.error("Read error,InterruptedException:" + path, e);
//            return null;
//        }
//    }
//
//    private boolean setData(String path, String data) {
//        try {
//            zooKeeper.setData(path, data.getBytes(), -1);
//        } catch (KeeperException e) {
//            log.error("Set error,KeeperException:" + path + " data:" + data, e);
//            return false;
//        } catch (InterruptedException e) {
//            log.error("Set error,InterruptedException:" + path + " data:" + data, e);
//            return false;
//        }
//        return true;
//    }
//
//    private boolean deleteData(String path) {
//        try {
//            zooKeeper.delete(path, -1);
//        } catch (InterruptedException e) {
//            log.error("delete error,InterruptedException:" + path, e);
//            return false;
//        } catch (KeeperException e) {
//            log.error("delete error,KeeperException:" + path, e);
//            return false;
//        }
//        return true;
//    }
//
//    private boolean recursivelyDeleteData(String path) {
//        List<String> childList = getChildrenList(path);
//        if (childList == null) {
//            return false;
//        } else if (childList.isEmpty()) {
//            deleteData(path);
//        } else {
//            for (String childName : childList) {
//                String childPath = path + "/" + childName;
//                List<String> grandChildList = getChildrenList(childPath);
//                if (grandChildList == null) {
//                    return false;
//                } else if (grandChildList.isEmpty()) {
//                    deleteData(childPath);
//                } else {
//                    recursivelyDeleteData(childPath);
//                }
//            }
//            deleteData(path);
//        }
//        return true;
//    }
//
//    private String getData(String path) {
//        try {
//            return new String(zooKeeper.getData(path, false, null));
//        } catch (KeeperException e) {
//            log.error("Read error,KeeperException:" + path, e);
//            return "";
//        } catch (InterruptedException e) {
//            log.error("Read error,InterruptedException:" + path, e);
//            return "";
//        }
//    }
//
//    /**
//     * 读取指定节点下孩子节点数目
//     *
//     * @param topic kafka topic 名称
//     * @return
//     */
//    public int readTopicChildren(String topic) {
//        StringBuilder sb = new StringBuilder().append("/brokers/topics/")
//                .append(topic).append("/partitions");
//        return readChildren(sb.toString());
//    }
//
//    public boolean setTopicGroupOffset(String topic, String group,
//                                       String partition, String data) {
//        StringBuilder sb = new StringBuilder().append("/consumers/").append(group)
//                .append("/offsets/").append(topic).append("/").append(partition);
//        return setData(sb.toString(), data);
//    }
//
//    public String getTopicGroupOffset(String topic, String group,
//                                      String partition) {
//        StringBuilder sb = new StringBuilder().append("/consumers/").append(group)
//                .append("/offsets/").append(topic).append("/").append(partition);
//        System.out.println(sb.toString());
//        return getData(sb.toString());
//    }
//
//    public boolean deleteUselessConsumer(String topic, String group) {
//        if (topic.endsWith("-1")) {
//            StringBuilder sb = new StringBuilder().append("/consumers/")
//                    .append(group);
//            return recursivelyDeleteData(sb.toString());
//        } else {
//            StringBuilder sb = new StringBuilder().append("/consumers/").append(group)
//                    .append("/offsets/").append(topic);
//            return recursivelyDeleteData(sb.toString());
//        }
//    }
//
//    public boolean deleteUselessLikeConsumer(String topic, String group) {
//        String path = "/consumers";
//        List<String> childList = getChildrenList(path);
//        int success = 0;
//        int count = 0;
//        for (String child : childList) {
//            if (child.startsWith(group)) {
//                count++;
//                if (deleteUselessConsumer(topic, child)) {
//                    success++;
//                }
//            }
//        }
//        if (success == count) {
//            return true;
//        } else {
//            return false;
//        }
//    }
//
//    public boolean deleteUselessLikeConsumer(String group) {
//        return deleteUselessLikeConsumer("-1", group);
//    }
//
//    public boolean deleteUselessConsumer(String group) {
//        return deleteUselessConsumer("-1", group);
//    }
//
//    @Override
//    public void process(WatchedEvent event) {
//        log.info("Receive Event：" + event.getState());
//    }
//}
