package com.aero.ops.utils;

import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;

import java.lang.reflect.Method;
import java.util.Map;

/**
 * @author 罗涛
 * @title HighLightUtil
 * @date 2020/7/16 14:51
 */
public class HighLightUtil {
    /**
     * 设置高亮
     * @param hit 命中记录
     * @param filed 字段
     * @param object 待赋值对象
     */
    public static void setHighLight(SearchHit hit, String filed, Object object){
        //获取对应的高亮域
        Map<String, HighlightField> highlightFields = hit.getHighlightFields();
        HighlightField highlightField = highlightFields.get(filed);
        if (highlightField != null){
            //取得定义的高亮标签
            String highLightMessage = highlightField.fragments()[0].toString();
            // 反射调用set方法将高亮内容设置进去
            try {
                String setMethodName = parSetMethodName(filed);
                Class<?> Clazz = object.getClass();
                Method setMethod = Clazz.getMethod(setMethodName, String.class);
                setMethod.invoke(object, highLightMessage);
            } catch (Exception e) {
//                log.error("反射报错", e);
                e.printStackTrace();
            }
        }
    }

    /**
     * 根据字段名，获取Set方法名
     * @param fieldName 字段名
     * @return  Set方法名
     */
    private static String parSetMethodName(String fieldName){
        if (StringUtils.isBlank(fieldName)){
            return null;
        }
        int startIndex = 0;
        if (fieldName.charAt(0) == '_'){
            startIndex = 1;
        }
        return "set" + fieldName.substring(startIndex, startIndex + 1).toUpperCase()
                + fieldName.substring(startIndex + 1);
    }
}
