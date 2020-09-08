package com.aero.ops.service.impl;

import com.aero.common.constants.DateFormat;
import com.aero.common.logger.DeviceLog;
import com.aero.common.logger.LogLevel;
import com.aero.common.model.PageModel;
import com.aero.common.utils.DateTimeUtil;
import com.aero.ops.entity.dto.DeviceLogQueryDTO;
import com.aero.ops.entity.po.AppLog;
import com.aero.ops.entity.vo.AppLogVO;
import com.aero.ops.service.IDevcieLogService;
import com.aero.ops.utils.HighLightUtil;
import com.aero.ops.utils.SpringPageUtil;
import com.alibaba.fastjson.JSON;
import jdk.nashorn.internal.ir.CallNode;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.index.query.*;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.*;

/**
 * @author 罗涛
 * @title DeviceLogServiceImpl
 * @date 2020/7/16 14:39
 */
@Service
public class DeviceLogServiceImpl implements IDevcieLogService {

    @Autowired
    ElasticsearchTemplate elasticsearchTemplate;

    @Override
    public PageModel<List<DeviceLog>> queryDeviceLogs(DeviceLogQueryDTO queryDTO) {
        int pageIndex = queryDTO.getPageIndex();
        int pageSize = queryDTO.getPageSize();
        Pageable pageable = SpringPageUtil.buildSpringPageRequest(pageIndex,pageSize);

        String serverIp = queryDTO.getServerIp();
        String direction = queryDTO.getDirection();
        String macPrefix = queryDTO.getMacPrefix();
        Integer sensorType = queryDTO.getSensorType();

        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();

        if(StringUtils.isNotEmpty(serverIp)){
            QueryBuilder termQuery = QueryBuilders.termQuery("serverIp.keyword", serverIp);
            boolQueryBuilder.must(termQuery);
        }

        if(StringUtils.isNotEmpty(direction)){
            QueryBuilder termQuery = QueryBuilders.termQuery("direction.keyword", direction);
            boolQueryBuilder.must(termQuery);
        }

        if(Objects.nonNull(sensorType)){
            QueryBuilder termQuery = QueryBuilders.termQuery("sensorType", sensorType);
            boolQueryBuilder.filter(termQuery);
        }

        if(StringUtils.isNotEmpty(macPrefix)){
            String pattern = StringUtils.join(macPrefix,"*");
            QueryBuilder wildcard = QueryBuilders.wildcardQuery("deviceId", pattern);
            boolQueryBuilder.filter(wildcard);
        }

        Date startTime = queryDTO.getStartTime();
        Date endTime = queryDTO.getEndTime();
        if(Objects.nonNull(startTime)){
            RangeQueryBuilder rangeQuery = QueryBuilders.rangeQuery("@timestamp");
            boolQueryBuilder.must(rangeQuery.gt(startTime.getTime()));
        }
        if(Objects.nonNull(endTime)){
            RangeQueryBuilder rangeQuery = QueryBuilders.rangeQuery("@timestamp");
            boolQueryBuilder.must(rangeQuery.lt(endTime.getTime()));
        }


        HighlightBuilder.Field contentField = new HighlightBuilder.Field("content").preTags("<span style='color:red'>").postTags("</span>");
        FieldSortBuilder order = SortBuilders.fieldSort("@timestamp").order(SortOrder.DESC);
        if(StringUtils.isNotEmpty(queryDTO.getTimeSort()) && "asc".equalsIgnoreCase(queryDTO.getTimeSort())){
            order = SortBuilders.fieldSort("@timestamp").order(SortOrder.ASC);
        }

        SearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withIndices("device_log")
                .withQuery(boolQueryBuilder)
                .withPageable(pageable)
                .withHighlightFields(contentField)
                .withSort(order)
                .build();

        Long count = elasticsearchTemplate.count(searchQuery);
        List<DeviceLog> appLogModels = elasticsearchTemplate.query(searchQuery, searchResponse -> {
            SearchHits hits = searchResponse.getHits();
            List<DeviceLog> list=new ArrayList<>();
            Arrays.stream(hits.getHits()).forEach(hit -> {
                Map<String, Object> sourceAsMap = hit.getSourceAsMap();
//                Object msgTime = sourceAsMap.remove("msgTime");
//                Date date = null;
//                if (Objects.nonNull(msgTime)) {
//                    try {
//                        date = DateTimeUtil.str2Time(((String) msgTime), DateFormat.CT_S);
//                    } catch (ParseException e) {
//                        e.printStackTrace();
//                    }
//                }
                String s = JSON.toJSONString(sourceAsMap);
                DeviceLog deviceLog = JSON.parseObject(s, DeviceLog.class);
//                deviceLog.setMsgTime(date);
                HighLightUtil.setHighLight(hit, "content", deviceLog);
                list.add(deviceLog);
            });
            return list;
        });
        PageModel page = new PageModel();
        page.setCount(count.intValue());
        page.setData(appLogModels);
        return page;
    }

}
