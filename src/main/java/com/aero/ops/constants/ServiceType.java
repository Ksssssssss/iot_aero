package com.aero.ops.constants;

import lombok.extern.slf4j.Slf4j;

/**
 * @author ksssss
 * @Date: 20-9-8 16:59
 * @Description:
 */
@Slf4j
public enum ServiceType {
    NONE("none", "none"),
    ZK("zkCluster", "ZooKeeper"),
    KAFKA("kafkaCluster", "Kafka"),
    ;


    public final String clusterNameConversion;
    public final String serviceName;

    ServiceType(String clusterNameConversion, String serviceName) {
        this.clusterNameConversion = clusterNameConversion;
        this.serviceName = serviceName;
    }

    public static String transformServiceName(String clusterName) {
        final String delimiter = "-";
        String[] clusterNameArray = clusterName.split(delimiter);
        if (clusterNameArray.length < 1) {
            log.error("转换服务名字错误,clusterName is illegal,clusterName:{}", clusterName);
            throw new IllegalArgumentException("clusterName is illegal");
        }

        final String clusterNameConversion = clusterNameArray[0];
        ServiceType serviceType = matchServiceType(clusterNameConversion);
        return serviceType.serviceName;
    }

    public static ServiceType matchServiceType(String clusterNameConversion) {
        for (ServiceType serviceType : values()) {
            if (serviceType.clusterNameConversion.equals(clusterNameConversion)) {
                return serviceType;
            }
        }
        return NONE;
    }
}
