package com.aero.ops.utils;


import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author 罗涛
 * @title MapFlatUtil
 * @date 2020/8/12 11:12
 */
public class MapFlatUtil {

    private static final String SEPARATOR = ".";

    /**
     * 深度嵌套map对象转大map（扁平化）
     * @param source 源map
     * @param parentNode 父节点扁平化之后的名字
     * @return map
     */
    public static Map<String, Object> flat(Map<String, Object> source, String parentNode) {
        Map<String, Object> flat = new HashMap<>();
        Set<Map.Entry<String, Object>> set  = source.entrySet();
        String prefix = StringUtils.isNotBlank(parentNode) ? parentNode + SEPARATOR : "";
        set.forEach(entity -> {
            Object value = entity.getValue();
            String key = entity.getKey();
            String newKey = prefix + key;
            if (value instanceof Map) {
                flat.putAll(flat((Map)value, newKey));
            } else {
                flat.put(newKey, value);
            }
        });
        return flat;
    }
}
