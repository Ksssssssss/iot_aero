package com.aero.ops.entity.vo;

import lombok.Data;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author 罗涛
 * @title EchartCpuVO
 * @date 2020/8/12 12:02
 */

@Data
public class MultiEchartVO {
    private Set<String> legend;
    private Map<String,List<EchartVO>> chartMap;
}
