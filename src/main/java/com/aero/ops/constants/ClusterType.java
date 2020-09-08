package com.aero.ops.constants;

import lombok.Getter;

/**
 * @author 罗涛
 * @title MenuType
 * @date 2020/8/5 14:51
 */

@Getter
public enum ClusterType {
    CLUSTER(1, "集群"),
    NODE(2, "节点");

    int type;
    String desc;

    ClusterType(int type, String desc) {
        this.type = type;
        this.desc = desc;
    }
}
