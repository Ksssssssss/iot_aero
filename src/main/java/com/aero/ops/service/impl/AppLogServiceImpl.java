package com.aero.ops.service.impl;

import com.aero.common.model.PageModel;
import com.aero.ops.utils.HighLightUtil;
import com.alibaba.fastjson.JSON;
import com.aero.common.logger.LogLevel;
import com.aero.ops.entity.dto.AppLogQueryDTO;
import com.aero.ops.entity.po.AppLog;
import com.aero.ops.entity.vo.AppLogVO;
import com.aero.ops.service.IAppLogService;
import com.aero.ops.utils.SpringPageUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.*;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.SearchResultMapper;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.aggregation.impl.AggregatedPageImpl;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.stereotype.Service;

import java.lang.reflect.Method;
import java.util.*;

@Service
@Slf4j
public class AppLogServiceImpl implements IAppLogService {
    @Autowired
    ElasticsearchTemplate elasticsearchTemplate;

    @Override
    public List<String> getAllApps() {
        return null;
    }


    @Override
    public PageModel<List<AppLog>> queryAppLogs(AppLogQueryDTO queryDTO) {
        int pageIndex = queryDTO.getPageIndex();
        int pageSize = queryDTO.getPageSize();
        Pageable pageable = SpringPageUtil.buildSpringPageRequest(pageIndex,pageSize);

        String logLevel = queryDTO.getLogLevel();
        String threadName = queryDTO.getThreadName();
        String className = queryDTO.getClassName();
        String keyword = queryDTO.getKeyword();
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();

        HighlightBuilder.Field levelField = null;
        if(StringUtils.isNotEmpty(logLevel)){
            QueryBuilder matchQuery = QueryBuilders.matchQuery("level",logLevel);
            boolQueryBuilder.must(matchQuery);
            if(LogLevel.ERROR.name().equalsIgnoreCase(logLevel)){
                levelField = new HighlightBuilder.Field("level").preTags("<span style=\"color:red;font-weight:bold\">").postTags("</span>");
            }else if(LogLevel.WARN.name().equalsIgnoreCase(logLevel)){
                levelField = new HighlightBuilder.Field("level").preTags("<span style=\"color:DeepPink\">").postTags("</span>");
            }
        }

        if(StringUtils.isNotEmpty(threadName)){
            QueryBuilder matchQuery = QueryBuilders.matchQuery("thread", threadName);
            boolQueryBuilder.must(matchQuery);
        }

        if(StringUtils.isNotEmpty(className)){
            QueryBuilder matchQuery = QueryBuilders.matchQuery("classname", className);
            boolQueryBuilder.filter(matchQuery);
        }

        if(StringUtils.isNotEmpty(keyword)){
            QueryBuilder matchQuery = QueryBuilders.matchQuery("content", keyword);
            boolQueryBuilder.filter(matchQuery);
        }

        HighlightBuilder.Field contentField = new HighlightBuilder.Field("content").preTags("<span style=\"color:red\">").postTags("</span>");
        HighlightBuilder.Field classnameField = new HighlightBuilder.Field("classname").preTags("<span style=\"color:red\">").postTags("</span>");
        List<HighlightBuilder.Field> highlightFields = new ArrayList<>(Arrays.asList(contentField,classnameField));
        if(Objects.nonNull(levelField)){
            highlightFields.add(levelField);
        }
        HighlightBuilder.Field[] highlightFieldArray = new HighlightBuilder.Field[highlightFields.size()];
        highlightFields.toArray(highlightFieldArray);
        SearchQuery searchQuery = new NativeSearchQueryBuilder()
//                .withIndices("elk-aero-iot-acceptor", "elk-aero-ops")
                .withQuery(boolQueryBuilder)
                .withPageable(pageable)
                .withHighlightFields(highlightFieldArray)
                .build();

        AggregatedPage<AppLog> appLogModels = elasticsearchTemplate.queryForPage(searchQuery,AppLog.class, new SearchResultMapper() {
            @Override
            public <T> AggregatedPage<T> mapResults(SearchResponse response, Class<T> clazz, Pageable pageable) {
                List<AppLog> logs = new ArrayList<>();
                //命中记录
                SearchHits hits = response.getHits();
                for (SearchHit hit : hits){
                    if (hits.totalHits <= 0){
                        return null;
                    }

                    Map<String, Object> sourceAsMap = hit.getSourceAsMap();
//                    Object timestamp = sourceAsMap.remove("timestamp");
                    String s = JSON.toJSONString(sourceAsMap);
                    AppLog appLog = JSON.parseObject(s, AppLog.class);
//                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss,SSS");
//                    Date date = null;
//                    try {
//                        date = sdf.parse(((String) timestamp));
//                    } catch (ParseException e) {
//                        e.printStackTrace();
//                    }
//                    appLog.setTimestamp(date);

                    //时间高亮处理
                    StringBuilder sb = new StringBuilder();
                    sb.append("<span style=\"color:Orange\">");
                    sb.append(appLog.getTimestamp());
                    sb.append("</span>");
                    String highlightTime = sb.toString();
                    appLog.setTimestamp(highlightTime);

                    HighLightUtil.setHighLight(hit, "content", appLog);
                    HighLightUtil.setHighLight(hit, "classname", appLog);
                    HighLightUtil.setHighLight(hit, "level", appLog);

                    logs.add(appLog);
                }
                return new AggregatedPageImpl<>((List<T>)logs);
            }

            @Override
            public <T> T mapSearchHit(SearchHit searchHit, Class<T> aClass) {
                return null;
            }
        });
        PageModel page = new PageModel();
        page.setCount(((int) appLogModels.getTotalElements()));
        page.setData(appLogModels.getContent());
        return page;
    }


    @Override
    public PageModel<List<AppLogVO>> queryMergeLogs(AppLogQueryDTO queryDTO) {
        int pageIndex = queryDTO.getPageIndex();
        int pageSize = queryDTO.getPageSize();
        Pageable pageable = SpringPageUtil.buildSpringPageRequest(pageIndex,pageSize);

        QueryBuilder queryBuilder = null;

        String kql = queryDTO.getKql();
        if(StringUtils.isNotEmpty(kql)){
            WrapperQueryBuilder wrapperQueryBuilder = QueryBuilders.wrapperQuery(kql);
            queryBuilder = wrapperQueryBuilder;

        }else {
            String logLevel = queryDTO.getLogLevel();
            String threadName = queryDTO.getThreadName();
            String className = queryDTO.getClassName();
            String keyword = queryDTO.getKeyword();
            BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();


            if(StringUtils.isNotEmpty(logLevel)){
                QueryBuilder termsQuery = null;
                if(LogLevel.ERROR.name().equalsIgnoreCase(logLevel)){
                    termsQuery = QueryBuilders.termsQuery("level.keyword",logLevel);
                }else if(LogLevel.WARN.name().equalsIgnoreCase(logLevel)){
                    termsQuery = QueryBuilders.termsQuery("level.keyword",logLevel, LogLevel.ERROR.name());
                }else if(LogLevel.INFO.name().equalsIgnoreCase(logLevel)){
                    termsQuery = QueryBuilders.termsQuery("level.keyword",logLevel, LogLevel.WARN.name(), LogLevel.ERROR.name());
                }else{
                    termsQuery = QueryBuilders.termsQuery("level.keyword",logLevel, LogLevel.INFO.name(), LogLevel.WARN.name(), LogLevel.ERROR.name());
                }
                boolQueryBuilder.must(termsQuery);
            }

            if(StringUtils.isNotEmpty(threadName)){
                QueryBuilder matchQuery = QueryBuilders.matchQuery("thread", threadName);
                boolQueryBuilder.must(matchQuery);
            }

            if(StringUtils.isNotEmpty(className)){
                String pattern = StringUtils.join("*.", className);
                QueryBuilder wildcard = QueryBuilders.wildcardQuery("classname", pattern);
                boolQueryBuilder.filter(wildcard);
            }

            if(StringUtils.isNotEmpty(keyword)){
                QueryBuilder matchQuery = QueryBuilders.matchQuery("content", keyword);
                boolQueryBuilder.filter(matchQuery);
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

            queryBuilder = boolQueryBuilder;
        }

        HighlightBuilder.Field contentField = new HighlightBuilder.Field("content").preTags("<span style='color:red'>").postTags("</span>");
        HighlightBuilder.Field classnameField = new HighlightBuilder.Field("classname").preTags("<span style='color:red'>").postTags("</span>");
        FieldSortBuilder order = SortBuilders.fieldSort("@timestamp").order(SortOrder.DESC);
        if(StringUtils.isNotEmpty(queryDTO.getTimeSort()) && "asc".equalsIgnoreCase(queryDTO.getTimeSort())){
            order = SortBuilders.fieldSort("@timestamp").order(SortOrder.ASC);
        }

        String indexPrefix = StringUtils.join("elk-", queryDTO.getAppName());
        SearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withIndices(indexPrefix)
                .withQuery(queryBuilder)
                .withPageable(pageable)
                .withHighlightFields(contentField,classnameField)
                .withSort(order)
                .build();

        Long count = elasticsearchTemplate.count(searchQuery);
        List<AppLogVO> appLogModels = elasticsearchTemplate.query(searchQuery, searchResponse -> {
            SearchHits hits = searchResponse.getHits();
            List<AppLogVO> list=new ArrayList<>();
            Arrays.stream(hits.getHits()).forEach(hit -> {
                Map<String, Object> sourceAsMap = hit.getSourceAsMap();
                String s = JSON.toJSONString(sourceAsMap);
                AppLog appLog = JSON.parseObject(s, AppLog.class);
                //时间高亮处理
                StringBuilder sb = new StringBuilder();
                sb.append("<span style='color:Orange'>");
                sb.append(appLog.getTimestamp());
                sb.append("</span>");

                HighLightUtil.setHighLight(hit, "content", appLog);
                HighLightUtil.setHighLight(hit, "classname", appLog);

                sb.append(" {");
                sb.append(getBoldKeyAndData("server", appLog.getServerIp()));
                sb.append(getBoldKeyAndHighLogLevel(appLog.getLevel()));
                sb.append(getBoldKeyAndData("thread", appLog.getThread()));
                sb.append(getBoldKeyAndData("class", appLog.getClassname()));
                sb.append(getBoldKeyAndData("content", appLog.getContent()));
                sb.append("}");

                String logStr = sb.toString();
                list.add(new AppLogVO(logStr));
            });
            return list;
        });
        PageModel page = new PageModel();
        page.setCount(count.intValue());
        page.setData(appLogModels);
        return page;


//        AggregatedPage<AppLogVO> appLogModels = elasticsearchTemplate.queryForPage(searchQuery, AppLogVO.class, new SearchResultMapper() {
//            @Override
//            public <T> AggregatedPage<T> mapResults(SearchResponse response, Class<T> clazz, Pageable pageable) {
//                List<AppLogVO> logs = new ArrayList<>();
//                //命中记录
//                SearchHits hits = response.getHits();
//                for (SearchHit hit : hits){
//                    if (hits.totalHits <= 0){
//                        return null;
//                    }
//
//                    Map<String, Object> sourceAsMap = hit.getSourceAsMap();
//                    String s = JSON.toJSONString(sourceAsMap);
//                    AppLog appLog = JSON.parseObject(s, AppLog.class);
//
//                    //时间高亮处理
//                    StringBuilder sb = new StringBuilder();
//                    sb.append("<span style='color:Orange'>");
//                    sb.append(appLog.getTimestamp());
//                    sb.append("</span>");
//
//                    setHighLight(hit, "content", appLog);
//                    setHighLight(hit, "classname", appLog);
//                    setHighLight(hit, "level", appLog);
//
//                    sb.append(" {");
//                    sb.append(getBoldKeyAndData("server", appLog.getServerIp()));
//                    sb.append(getBoldKeyAndData("level", appLog.getLevel()));
//                    sb.append(getBoldKeyAndData("thread", appLog.getThread()));
//                    sb.append(getBoldKeyAndData("class", appLog.getClassname()));
//                    sb.append(getBoldKeyAndData("content", appLog.getContent()));
//                    sb.append("}");
//
//                    String logStr = sb.toString();
//                    logs.add(new AppLogVO(logStr));
//                }
//                return new AggregatedPageImpl(logs);
//            }
//
//            @Override
//            public <T> T mapSearchHit(SearchHit searchHit, Class<T> aClass) {
//                return null;
//            }
//        });
//        PageModel page = new PageModel();
//        page.setCount(((int) appLogModels.getTotalElements()));
//        page.setData(appLogModels.getContent());
//        return page;
    }

    private static String getBoldKeyAndData(String key,String value){
        StringBuilder sb = new StringBuilder();
        sb.append("<span style='font-weight:bold'>");
        sb.append(key);
        sb.append("</span>");
        sb.append(":");
        sb.append(value);
        if (!"content".equalsIgnoreCase(key)) {
            sb.append(" ");
        }
        return sb.toString();
    }

    private static String getBoldKeyAndHighLogLevel(String value){
        StringBuilder sb = new StringBuilder();
        sb.append("<span style='font-weight:bold'>");
        sb.append("level");
        sb.append("</span>");
        sb.append(":");
        if (LogLevel.ERROR.name().equalsIgnoreCase(value)) {
            sb.append("<span style='color:red;font-weight:bold'>");
            sb.append(value);
            sb.append("</span>");
        }else if(LogLevel.WARN.name().equalsIgnoreCase(value)){
            sb.append("<span style='color:blue;font-weight:bold'>");
            sb.append(value);
            sb.append("</span>");
        }else {
            sb.append(value);
        }
        sb.append(" ");
        return sb.toString();
    }
}
