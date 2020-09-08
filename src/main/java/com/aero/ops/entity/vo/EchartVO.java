package com.aero.ops.entity.vo;

import lombok.Data;

import java.util.List;

/**
 * @author 罗涛
 * @title EchartCpuVO
 * @date 2020/8/12 12:02
 */

@Data
public class EchartVO {
    private String name;
    private Object[] value;

    public EchartVO() {
    }

    public EchartVO(String name, Object[] value) {
        this.name = name;
        this.value = value;
    }

}
