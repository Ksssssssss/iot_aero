package com.aero.ops.service.impl;

import com.aero.ops.entity.dto.KafkaMetricQueryDTO;
import com.aero.ops.entity.dto.MetricQueryDTO;
import com.aero.ops.entity.vo.*;
import com.aero.ops.service.IMetricService;
import com.aero.ops.utils.MapFlatUtil;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.index.query.*;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.StringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.query.*;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Pattern;

/**
 * @author 罗涛
 * @title MetricServiceImpl
 * @date 2020/8/12 10:18
 */
@Service
public class MetricServiceImpl implements IMetricService {
    @Autowired
    ElasticsearchTemplate elasticsearchTemplate;

    @Override
    public List<ServerInfoVO> getLastCpuMetric() {
        String serviceType = "system";
        String metricset = "cpu";
        String coreParaKey = "system.cpu.user.norm.pct";
        return getLastDurationRecords(serviceType,metricset,coreParaKey,5);
    }

    @Override
    public List<ServerInfoVO> getLastMemoryMetric() {
        String serviceType = "system";
        String metricset = "memory";
        String coreParaKey = "system.memory.used.pct";
        return getLastDurationRecords(serviceType,metricset,coreParaKey,5);
    }

    @Override
    public List<EchartVO> getRealtimeCpu() {
        String serviceType = "system";
        String metricset = "cpu";
        String coreParaKey = "system.cpu.user.norm.pct";
        return getRealtimeData(serviceType,metricset, coreParaKey,1);
    }


    @Override
    public List<EchartVO> getRealtimeMemory() {
        String serviceType = "system";
        String metricset = "memory";
        String coreParaKey = "system.memory.used.pct";
        return getRealtimeData(serviceType,metricset, coreParaKey,1);
    }


    @Override
    public MultiEchartVO getRealtimeMultiCpu() {
        String serviceType = "system";
        String metricset = "cpu";
        String coreParaKey = "system.cpu.user.norm.pct";
        return getMultiCharts(serviceType,metricset, coreParaKey,1);
    }

    @Override
    public MultiEchartVO getRealtimeMultiMemory() {
        String serviceType = "system";
        String metricSet = "memory";
        String coreParaKey = "system.memory.used.pct";
        return getMultiCharts(serviceType,metricSet, coreParaKey,1);
    }

    @Override
    public MultiEchartVO getRealtimeMultiSocket() {
        String serviceType = "system";
        String metricSet = "socket_summary";
        String coreParaKey = "system.socket.summary.tcp.all.established";
        return getMultiCharts(serviceType,metricSet, coreParaKey,1);
    }

    @Override
    public List<Object> getClassifyField(String fieldKey) {
        String serviceType = "kafka";
        String metricSet = "consumergroup";
        String consumerOffsetTopic = "__consumer_offsets";

        BoolQueryBuilder boolBuilder = QueryBuilders.boolQuery();
        //service.type 为 kafka
        TermQueryBuilder serviceTermQueryBuilder = QueryBuilders.termQuery("service.type", serviceType);
        boolBuilder.must(serviceTermQueryBuilder);

        //metricset.name 为 consumergroup
        TermQueryBuilder metricSetTermQueryBuilder = QueryBuilders.termQuery("metricset.name", metricSet);
        boolBuilder.must(metricSetTermQueryBuilder);

        //topic 排除__consumer_offsets
        TermQueryBuilder offsetTermQueryBuilder = QueryBuilders.termQuery("kafka.consumergroup.topic", consumerOffsetTopic);
        boolBuilder.mustNot(offsetTermQueryBuilder);

        TermsAggregationBuilder classifyByField = AggregationBuilders.terms("classifyByField").field(fieldKey);

        String indexPrefix = "metricbeat*";
        SearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withIndices(indexPrefix)
                .withQuery(boolBuilder)
                .addAggregation(classifyByField)
                .build();
        List<Object> classifies = elasticsearchTemplate.query(searchQuery, searchResponse -> {
            Aggregations aggregations = searchResponse.getAggregations();
            List<Object> consumergroups = new ArrayList<>();
            List<StringTerms.Bucket> buckets = ((StringTerms) aggregations.asList().get(0)).getBuckets();
            for(StringTerms.Bucket bucket : buckets){
                String bucketKey = bucket.getKeyAsString();
                consumergroups.add(bucketKey);
            }
            return consumergroups;
        });
        return classifies;
    }

    @Override
    public List<KafkaMetricVO> getLastKafkaMetric() {
        String serviceType = "kafka";
        String metricSet = "consumergroup";
        String consumerOffsetTopic = "__consumer_offsets";

        BoolQueryBuilder boolBuilder = QueryBuilders.boolQuery();
        //service.type 为 kafka
        TermQueryBuilder serviceTermQueryBuilder = QueryBuilders.termQuery("service.type", serviceType);
        boolBuilder.must(serviceTermQueryBuilder);
        //metricset.name 为 consumergroup
        TermQueryBuilder metricSetTermQueryBuilder = QueryBuilders.termQuery("metricset.name", metricSet);
        boolBuilder.must(metricSetTermQueryBuilder);

        //时间区间为最近5分钟
        RangeQueryBuilder rangeQuery = QueryBuilders.rangeQuery("@timestamp");
        long now = System.currentTimeMillis();
        long minutesAgo = now - (5*60*1000);
        boolBuilder.must(rangeQuery.gte(minutesAgo));

        //topic 排除__consumer_offsets
        TermQueryBuilder offsetTermQueryBuilder = QueryBuilders.termQuery("kafka.consumergroup.topic", consumerOffsetTopic);
        boolBuilder.mustNot(offsetTermQueryBuilder);

        //关心的字段
        String[] concernFields = new String[]{"@timestamp","kafka.consumergroup"};
        SourceFilter sourceFilter = new FetchSourceFilter(concernFields,null);
        //索引前缀为metricbeat*, 按时间倒序排序
        String indexPrefix = "metricbeat*";
        FieldSortBuilder order = SortBuilders.fieldSort("@timestamp").order(SortOrder.DESC);
        //10000为 elasticsearchTemplate框架允许的分页最大值
        SearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withIndices(indexPrefix)
                .withQuery(boolBuilder)
                .withSourceFilter(sourceFilter)
                .withSort(order)
                .withPageable(PageRequest.of(0, 10000))
                .build();
        List<KafkaMetricVO> kafkaNodeInfoVOS = elasticsearchTemplate.query(searchQuery, searchResponse -> {
            List<KafkaMetricVO> metrics = new ArrayList<>();
            SearchHits hits = searchResponse.getHits();
            Arrays.stream(hits.getHits()).forEach(hit -> {
                Map<String, Object> sourceAsMap = hit.getSourceAsMap();
                Map<String, Object> flatMap = MapFlatUtil.flat(sourceAsMap, null);
                int resultLag = (int) flatMap.get("kafka.consumergroup.consumer_lag");
                if (resultLag>0) {
                    KafkaMetricVO metric = new KafkaMetricVO();
                    String resultConsumer = (String) flatMap.get("kafka.consumergroup.id");
                    String resultTopic = (String) flatMap.get("kafka.consumergroup.topic");
                    int resultOffset = (int) flatMap.get("kafka.consumergroup.offset");
                    int resultPartition = (int) flatMap.get("kafka.consumergroup.partition");
                    String timeStr = (String) flatMap.get("@timestamp");
                    SimpleDateFormat dff = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS",Locale.ENGLISH);
                    try {
                        Date infoTime = dff.parse(timeStr);
                        long cnTimestamp = infoTime.getTime() + 8*60*60*1000;
                        metric.setLastMetricTime(new Date(cnTimestamp));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    metric.setConsumerGroup(resultConsumer);
                    metric.setTopic(resultTopic);
                    metric.setPartition(resultPartition);
                    metric.setOffset(resultOffset);
                    metric.setLag(resultLag);
                    metric.setBrokerAddr((String) flatMap.get("kafka.consumergroup.broker.address"));
                    metric.setClientHost((String) flatMap.get("kafka.consumergroup.client.host"));
                    metrics.add(metric);
                }
            });
            return metrics;
        });
        return kafkaNodeInfoVOS;
    }

    @Override
    public MultiEchartVO getRealtimeMultiKafka(KafkaMetricQueryDTO queryDTO) {
        String serviceType = "kafka";
        String metricSet = "consumergroup";
        String consumerGroup = queryDTO.getConsumerGroup();
        String topic = queryDTO.getTopic();
        Integer partition = queryDTO.getPartition();
        Integer rangeHours = queryDTO.getRangeHours();
        //默认展示1个小时的数据
        int hours = Objects.nonNull(rangeHours)?rangeHours:1;
        String consumerOffsetTopic = "__consumer_offsets";

        BoolQueryBuilder boolBuilder = QueryBuilders.boolQuery();
        //service.type 为 kafka
        TermQueryBuilder serviceTermQueryBuilder = QueryBuilders.termQuery("service.type", serviceType);
        boolBuilder.must(serviceTermQueryBuilder);
        //metricset.name 为 consumergroup
        TermQueryBuilder metricSetTermQueryBuilder = QueryBuilders.termQuery("metricset.name", metricSet);
        boolBuilder.must(metricSetTermQueryBuilder);

        //kafka.consumergroup.id
        if (StringUtils.isNotEmpty(consumerGroup)) {
            TermQueryBuilder consumerGroupTermQueryBuilder = QueryBuilders.termQuery("kafka.consumergroup.id", consumerGroup);
            boolBuilder.must(consumerGroupTermQueryBuilder);
        }

        //kafka.consumergroup.topic
        if (StringUtils.isNotEmpty(topic)) {
            TermQueryBuilder topicTermQueryBuilder = QueryBuilders.termQuery("kafka.consumergroup.topic", topic);
            boolBuilder.must(topicTermQueryBuilder);
        }

        //kafka.consumergroup.partition
        if(Objects.nonNull(partition)){
            TermQueryBuilder partitionTermQueryBuilder = QueryBuilders.termQuery("kafka.consumergroup.partition", partition);
            boolBuilder.must(partitionTermQueryBuilder);
        }

        //时间区间为最近几个小时
        RangeQueryBuilder rangeQuery = QueryBuilders.rangeQuery("@timestamp");
        long now = System.currentTimeMillis();
        long hoursAgo = now - (hours*60*60*1000);
        boolBuilder.must(rangeQuery.gte(hoursAgo));

        //topic 排除__consumer_offsets
        TermQueryBuilder offsetTermQueryBuilder = QueryBuilders.termQuery("kafka.consumergroup.topic", consumerOffsetTopic);
        boolBuilder.mustNot(offsetTermQueryBuilder);

        //关心的字段
        String[] concernFields = new String[]{"@timestamp","kafka.consumergroup"};
        SourceFilter sourceFilter = new FetchSourceFilter(concernFields,null);
        //索引前缀为metricbeat*, 按时间倒序排序
        String indexPrefix = "metricbeat*";
        FieldSortBuilder order = SortBuilders.fieldSort("@timestamp").order(SortOrder.DESC);
        //10000为 elasticsearchTemplate框架允许的分页最大值
        SearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withIndices(indexPrefix)
                .withQuery(boolBuilder)
                .withSourceFilter(sourceFilter)
                .withSort(order)
                .withPageable(PageRequest.of(0, 10000))
                .build();
        MultiEchartVO multiEchart = elasticsearchTemplate.query(searchQuery, searchResponse -> {
            SearchHits hits = searchResponse.getHits();
            MultiEchartVO chart = new MultiEchartVO();
            Map<String,List<EchartVO>> chartMap = new HashMap<>();
            Arrays.stream(hits.getHits()).forEach(hit -> {
                Map<String, Object> sourceAsMap = hit.getSourceAsMap();
                Map<String, Object> flatMap = MapFlatUtil.flat(sourceAsMap, null);
                int resultLag = (int) flatMap.get("kafka.consumergroup.consumer_lag");
                KafkaMetricVO metric = new KafkaMetricVO();
                String resultConsumer = (String) flatMap.get("kafka.consumergroup.id");
                String resultTopic = (String) flatMap.get("kafka.consumergroup.topic");
                int resultOffset = (int) flatMap.get("kafka.consumergroup.offset");
                int resultPartition = (int) flatMap.get("kafka.consumergroup.partition");
                String timeStr = (String) flatMap.get("@timestamp");
                String legendName = StringUtils.joinWith("-", resultTopic, resultPartition);
                SimpleDateFormat dff = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS",Locale.ENGLISH);
                try {
                    Date infoTime = dff.parse(timeStr);
                    long cnTimestamp = infoTime.getTime() + 8*60*60*1000;
                    metric.setLastMetricTime(new Date(cnTimestamp));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                metric.setConsumerGroup(resultConsumer);
                metric.setTopic(resultTopic);
                metric.setPartition(resultPartition);
                metric.setOffset(resultOffset);
                metric.setLag(resultLag);
                EchartVO kafka = new EchartVO();
                kafka.setName(legendName);
                kafka.setValue(new Object[]{metric.getLastMetricTime(), metric.getLag()});
                if (chartMap.containsKey(legendName)) {
                    List<EchartVO> list = chartMap.get(legendName);
                    list.add(kafka);
                }else {
                    List<EchartVO> list = new ArrayList<>();
                    list.add(kafka);
                    chartMap.put(legendName, list);
                }
            });
            //清除全为0的数据
            Set<String> zeroListKeys = new HashSet<>();
            for(String key:chartMap.keySet()){
                List<EchartVO> echartVOS = chartMap.get(key);
                if(allZeroList(echartVOS)){
                    zeroListKeys.add(key);
                }
            }
            if (!CollectionUtils.isEmpty(zeroListKeys)) {
                for(String key: zeroListKeys){
                    chartMap.remove(key);
                }
            }
            chart.setChartMap(chartMap);
            chart.setLegend(chartMap.keySet());
            return chart;
        });
        return multiEchart;
    }

    public boolean allZeroList(List<EchartVO> list){
        for(EchartVO e: list){
            if(((int) e.getValue()[1])!=0){
                return false;
            }
        }
        return true;
    }

    @Override
    public List<KafkaMetricVO> getKafkaMetric(KafkaMetricQueryDTO queryDTO) {
        String serviceType = "kafka";
        String metricSet = "consumergroup";
        String consumerGroup = queryDTO.getConsumerGroup();
        String topic = queryDTO.getTopic();
        Integer partition = queryDTO.getPartition();
        Integer rangeHours = queryDTO.getRangeHours();
        //默认展示1个小时的数据
        int hours = Objects.nonNull(rangeHours)?rangeHours:1;
        String consumerOffsetTopic = "__consumer_offsets";

        BoolQueryBuilder boolBuilder = QueryBuilders.boolQuery();
        //service.type 为 kafka
        TermQueryBuilder serviceTermQueryBuilder = QueryBuilders.termQuery("service.type", serviceType);
        boolBuilder.must(serviceTermQueryBuilder);
        //metricset.name 为 consumergroup
        TermQueryBuilder metricSetTermQueryBuilder = QueryBuilders.termQuery("metricset.name", metricSet);
        boolBuilder.must(metricSetTermQueryBuilder);

        //kafka.consumergroup.id
        if (StringUtils.isNotEmpty(consumerGroup)) {
            TermQueryBuilder consumerGroupTermQueryBuilder = QueryBuilders.termQuery("kafka.consumergroup.id", consumerGroup);
            boolBuilder.must(consumerGroupTermQueryBuilder);
        }

        //kafka.consumergroup.topic
        if (StringUtils.isNotEmpty(topic)) {
            TermQueryBuilder topicTermQueryBuilder = QueryBuilders.termQuery("kafka.consumergroup.topic", topic);
            boolBuilder.must(topicTermQueryBuilder);
        }

        //kafka.consumergroup.partition
        if(Objects.nonNull(partition)){
            TermQueryBuilder partitionTermQueryBuilder = QueryBuilders.termQuery("kafka.consumergroup.partition", partition);
            boolBuilder.must(partitionTermQueryBuilder);
        }

        //时间区间为最近几个小时
        RangeQueryBuilder rangeQuery = QueryBuilders.rangeQuery("@timestamp");
        long now = System.currentTimeMillis();
        long hoursAgo = now - (hours*60*60*1000);
        boolBuilder.must(rangeQuery.gte(hoursAgo));

        //topic 排除__consumer_offsets
        TermQueryBuilder offsetTermQueryBuilder = QueryBuilders.termQuery("kafka.consumergroup.topic", consumerOffsetTopic);
        boolBuilder.mustNot(offsetTermQueryBuilder);

        //关心的字段
        String[] concernFields = new String[]{"@timestamp","kafka.consumergroup"};
        SourceFilter sourceFilter = new FetchSourceFilter(concernFields,null);
        //索引前缀为metricbeat*, 按时间倒序排序
        String indexPrefix = "metricbeat*";
        FieldSortBuilder order = SortBuilders.fieldSort("@timestamp").order(SortOrder.DESC);
        //10000为 elasticsearchTemplate框架允许的分页最大值
        SearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withIndices(indexPrefix)
                .withQuery(boolBuilder)
                .withSourceFilter(sourceFilter)
                .withSort(order)
                .withPageable(PageRequest.of(0, 10000))
                .build();
        List<KafkaMetricVO> vos = elasticsearchTemplate.query(searchQuery, searchResponse -> {
            SearchHits hits = searchResponse.getHits();
            List<KafkaMetricVO> metrics = new ArrayList<>();
            Arrays.stream(hits.getHits()).forEach(hit -> {
                Map<String, Object> sourceAsMap = hit.getSourceAsMap();
                Map<String, Object> flatMap = MapFlatUtil.flat(sourceAsMap, null);
                KafkaMetricVO metric = new KafkaMetricVO();
                String resultConsumer = (String) flatMap.get("kafka.consumergroup.id");
                String resultTopic = (String) flatMap.get("kafka.consumergroup.topic");
                int resultPartition = (int) flatMap.get("kafka.consumergroup.partition");
                int resultOffset = (int) flatMap.get("kafka.consumergroup.offset");
                int resultLag = (int) flatMap.get("kafka.consumergroup.consumer_lag");
                String timeStr = (String) flatMap.get("@timestamp");
                SimpleDateFormat dff = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS",Locale.ENGLISH);
                try {
                    Date infoTime = dff.parse(timeStr);
                    long cnTimestamp = infoTime.getTime() + 8*60*60*1000;
                    metric.setLastMetricTime(new Date(cnTimestamp));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                metric.setConsumerGroup(resultConsumer);
                metric.setTopic(resultTopic);
                metric.setPartition(resultPartition);
                metric.setOffset(resultOffset);
                metric.setLag(resultLag);
                metrics.add(metric);
            });
            return metrics;
        });
        return vos;
    }

    public List<ServerInfoVO> getLastDurationRecords(String serviceType, String metricset, String coreParaKey, int recentMinutes){
        BoolQueryBuilder boolBuilder = QueryBuilders.boolQuery();
        //service.type 为 system
        TermQueryBuilder systemTermQueryBuilder = QueryBuilders.termQuery("service.type", serviceType);
        boolBuilder.must(systemTermQueryBuilder);
        //metricset.name 为 cpu
        TermQueryBuilder cpuTermQueryBuilder = QueryBuilders.termQuery("metricset.name", metricset);
        boolBuilder.must(cpuTermQueryBuilder);

        //时间区间为最近5分钟
        RangeQueryBuilder rangeQuery = QueryBuilders.rangeQuery("@timestamp");
        long now = System.currentTimeMillis();
        long minutesAgo = now - (recentMinutes*60*1000);
        boolBuilder.must(rangeQuery.gte(minutesAgo));
        boolBuilder.must(rangeQuery.lt(now));

        //只查询system.cpu.user.pct 和 @timestamp两个字段
        String[] concernFields = new String[]{coreParaKey,"@timestamp","host.name"};
        SourceFilter sourceFilter = new FetchSourceFilter(concernFields,null);

        //索引前缀为metricbeat*, 按时间倒序排序
        String indexPrefix = "metricbeat*";
        FieldSortBuilder order = SortBuilders.fieldSort("@timestamp").order(SortOrder.DESC);
        SearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withIndices(indexPrefix)
                .withQuery(boolBuilder)
                .withSourceFilter(sourceFilter)
                .withSort(order)
                .withPageable(PageRequest.of(0, 1))
                .build();
        List<ServerInfoVO> serverInfos = elasticsearchTemplate.query(searchQuery, searchResponse -> {
            SearchHits hits = searchResponse.getHits();
            List<ServerInfoVO> list=new ArrayList<>();
            Arrays.stream(hits.getHits()).forEach(hit -> {
                Map<String, Object> sourceAsMap = hit.getSourceAsMap();
                Map<String, Object> flatMap = MapFlatUtil.flat(sourceAsMap, null);
                ServerInfoVO serverInfo = new ServerInfoVO();
                Object pctTemp = flatMap.get(coreParaKey);
                String pctStr = pctTemp.toString();
                double pct = Double.parseDouble(pctStr);
                String hostName = (String) flatMap.get("host.name");
                serverInfo.setServer(hostName);
                serverInfo.setPercent(pct);
                String timeStr = (String) flatMap.get("@timestamp");
                try {
                    SimpleDateFormat dff = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS",Locale.ENGLISH);
                    Date infoTime = dff.parse(timeStr);
                    long cnTimestamp = infoTime.getTime() + 8*60*60*1000;
                    serverInfo.setTime(new Date(cnTimestamp));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                list.add(serverInfo);

            });
            return list;
        });
        return serverInfos;
    }


    public List<EchartVO> getRealtimeData(String serviceType, String metricset, String coreParaKey, int recentHours) {
        BoolQueryBuilder boolBuilder = QueryBuilders.boolQuery();
        //service.type 为 system
        TermQueryBuilder systemTermQueryBuilder = QueryBuilders.termQuery("service.type", serviceType);
        boolBuilder.must(systemTermQueryBuilder);
        //metricset.name 为 cpu
        TermQueryBuilder cpuTermQueryBuilder = QueryBuilders.termQuery("metricset.name", metricset);
        boolBuilder.must(cpuTermQueryBuilder);

        //时间区间为最近1天
        RangeQueryBuilder rangeQuery = QueryBuilders.rangeQuery("@timestamp");
        long now = System.currentTimeMillis();
        long hoursAgo = now - (recentHours*60*60*1000);
        boolBuilder.must(rangeQuery.gte(hoursAgo));
        boolBuilder.must(rangeQuery.lt(now));

        //只查询system.cpu.user.pct 和 @timestamp两个字段
        String[] concernFields = new String[]{coreParaKey,"@timestamp","host.name"};
        SourceFilter sourceFilter = new FetchSourceFilter(concernFields,null);

        //索引前缀为metricbeat*, 按时间倒序排序
        String indexPrefix = "metricbeat*";
        FieldSortBuilder order = SortBuilders.fieldSort("@timestamp").order(SortOrder.DESC);
        SearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withIndices(indexPrefix)
                .withQuery(boolBuilder)
                .withSourceFilter(sourceFilter)
                .withSort(order)
                .withPageable(PageRequest.of(0, 10000))
                .build();
        List<EchartVO> cpuInfos = elasticsearchTemplate.query(searchQuery, searchResponse -> {
            SearchHits hits = searchResponse.getHits();
            List<EchartVO> list=new ArrayList<>();
            Arrays.stream(hits.getHits()).forEach(hit -> {
                Map<String, Object> sourceAsMap = hit.getSourceAsMap();
                Map<String, Object> flatMap = MapFlatUtil.flat(sourceAsMap, null);
                EchartVO cpu = new EchartVO();
                Object pctTemp = flatMap.get(coreParaKey);
                String pctStr = pctTemp.toString();
                double cpuPct = Double.parseDouble(pctStr);
                String hostName = (String) flatMap.get("host.name");
                cpu.setName(hostName);
                String timeStr = (String) flatMap.get("@timestamp");
                try {
                    SimpleDateFormat dff = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS",Locale.ENGLISH);
                    Date infoTime = dff.parse(timeStr);
                    SimpleDateFormat cdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZZ",Locale.SIMPLIFIED_CHINESE);
                    long cnTimestamp = infoTime.getTime() + 8*60*60*1000;
                    String cnTime = cdf.format(cnTimestamp);
                    Object[] value = new Object[]{cnTime,cpuPct};
                    cpu.setValue(value);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                list.add(cpu);

            });
            return list;
        });
        return cpuInfos;
    }
    public MultiEchartVO getMultiCharts(String serviceType, String metricset, String coreParaKey, int recentHours){
        return getMultiCharts(serviceType,metricset,coreParaKey,recentHours,null);
    }

    public MultiEchartVO getMultiCharts(String serviceType, String metricset, String coreParaKey, int recentHours, String hostName) {
        BoolQueryBuilder boolBuilder = QueryBuilders.boolQuery();
        //service.type 为 system
        TermQueryBuilder systemTermQueryBuilder = QueryBuilders.termQuery("service.type", serviceType);
        boolBuilder.must(systemTermQueryBuilder);
        //metricset.name 为 cpu
        TermQueryBuilder cpuTermQueryBuilder = QueryBuilders.termQuery("metricset.name", metricset);
        boolBuilder.must(cpuTermQueryBuilder);

        ExistsQueryBuilder existsQueryBuilder = QueryBuilders.existsQuery(coreParaKey);
        boolBuilder.must(existsQueryBuilder);

        if(StringUtils.isNotEmpty(hostName)){
            TermQueryBuilder hostTermQuery = QueryBuilders.termQuery("host.name", hostName);
            boolBuilder.must(hostTermQuery);
        }

        //时间区间为最近1天
        RangeQueryBuilder rangeQuery = QueryBuilders.rangeQuery("@timestamp");
        long now = System.currentTimeMillis();
        long hoursAgo = now - (recentHours*60*60*1000);
        boolBuilder.must(rangeQuery.gte(hoursAgo));
        boolBuilder.must(rangeQuery.lt(now));

        //只查询system.cpu.user.pct 和 @timestamp两个字段
        String[] concernFields = new String[]{coreParaKey,"@timestamp","host.name"};
        if(StringUtils.countMatches(metricset,"process")>0){
            concernFields = new String[]{coreParaKey,"@timestamp","host.name", "process.pid"};
        }
        SourceFilter sourceFilter = new FetchSourceFilter(concernFields,null);

        //索引前缀为metricbeat*, 按时间倒序排序
        String indexPrefix = "metricbeat*";
        FieldSortBuilder order = SortBuilders.fieldSort("@timestamp").order(SortOrder.DESC);
        SearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withIndices(indexPrefix)
                .withQuery(boolBuilder)
                .withSourceFilter(sourceFilter)
                .withSort(order)
                .withPageable(PageRequest.of(0, 10000))
                .build();
        MultiEchartVO multiEchartVO = elasticsearchTemplate.query(searchQuery, searchResponse -> {
            SearchHits hits = searchResponse.getHits();
            MultiEchartVO multiEchart = new MultiEchartVO();
            Map<String,List<EchartVO>> chartMap = new HashMap<>();
            if(hits.totalHits==0){
                return multiEchart;
            }
            Arrays.stream(hits.getHits()).forEach(hit -> {
                Map<String, Object> sourceAsMap = hit.getSourceAsMap();
                Map<String, Object> flatMap = MapFlatUtil.flat(sourceAsMap, null);
                EchartVO cpu = new EchartVO();
                Object pctTemp = flatMap.get(coreParaKey);
                String pctStr = pctTemp.toString();
                double cpuPct = Double.parseDouble(pctStr);
                String host = (String) flatMap.get("host.name");
                if(StringUtils.equalsIgnoreCase(metricset,"process")){
                    Object pid = flatMap.get("process.pid");
                    String processKey = StringUtils.joinWith("-", host, pid);
                    cpu.setName(processKey);
                }else {
                    cpu.setName(host);
                }
                String timeStr = (String) flatMap.get("@timestamp");
                try {
                    SimpleDateFormat dff = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS",Locale.ENGLISH);
                    Date infoTime = dff.parse(timeStr);
                    SimpleDateFormat cdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZZ",Locale.SIMPLIFIED_CHINESE);
                    long cnTimestamp = infoTime.getTime() + 8*60*60*1000;
                    String cnTime = cdf.format(cnTimestamp);
                    Object[] value = new Object[]{cnTime,cpuPct};
                    cpu.setValue(value);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                if (chartMap.containsKey(cpu.getName())) {
                    List<EchartVO> list = chartMap.get(cpu.getName());
                    list.add(cpu);
                }else {
                    List<EchartVO> list = new ArrayList<>();
                    list.add(cpu);
                    chartMap.put(cpu.getName(), list);
                }
            });
            Set<String> legend = chartMap.keySet();
            multiEchart.setChartMap(chartMap);
            multiEchart.setLegend(legend);
            return multiEchart;
        });
        return multiEchartVO;
    }

    @Override
    public MultiEchartVO getRealtimeChart(MetricQueryDTO queryDTO) {
        String serviceType = queryDTO.getServiceType();
        String metricSet = queryDTO.getMetricSet();
        String coreParaKey = queryDTO.getCoreParaKey();
        String hostName = queryDTO.getHostName();
        Integer rangeHours = queryDTO.getRangeHours();
        return getMultiCharts(serviceType,metricSet,coreParaKey,rangeHours,hostName);
    }

    @Override
    public List<String> getSystemHosts(String serviceType, String metricSet, String classifyField) {
        BoolQueryBuilder boolBuilder = QueryBuilders.boolQuery();

        TermQueryBuilder serviceTermQueryBuilder = QueryBuilders.termQuery("service.type", serviceType);
        boolBuilder.must(serviceTermQueryBuilder);

        TermQueryBuilder metricSetTermQueryBuilder = QueryBuilders.termQuery("metricset.name", metricSet);
        boolBuilder.must(metricSetTermQueryBuilder);

        TermsAggregationBuilder classifyByField = AggregationBuilders.terms("classifyByField").field(classifyField);

        String indexPrefix = "metricbeat*";
        SearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withIndices(indexPrefix)
                .withQuery(boolBuilder)
                .addAggregation(classifyByField)
                .build();
        List<String> classifies = elasticsearchTemplate.query(searchQuery, searchResponse -> {
            Aggregations aggregations = searchResponse.getAggregations();
            List<String> servers = new ArrayList<>();
            List<StringTerms.Bucket> buckets = ((StringTerms) aggregations.asList().get(0)).getBuckets();
            for(StringTerms.Bucket bucket : buckets){
                String bucketKey = bucket.getKeyAsString();
                servers.add(bucketKey);
            }
            return servers;
        });
        return classifies;
    }


    @Override
    public Set<String> getAllFields(String serviceType, String metricset){
        BoolQueryBuilder boolBuilder = QueryBuilders.boolQuery();

        TermQueryBuilder serviceTypeTermQueryBuilder = QueryBuilders.termQuery("service.type", serviceType);
        boolBuilder.must(serviceTypeTermQueryBuilder);

        TermQueryBuilder metricSetTermQueryBuilder = QueryBuilders.termQuery("metricset.name", metricset);
        boolBuilder.must(metricSetTermQueryBuilder);

        //时间区间为最近5分钟
        RangeQueryBuilder rangeQuery = QueryBuilders.rangeQuery("@timestamp");
        long now = System.currentTimeMillis();
        long minutesAgo = now - (5*60*1000);
        boolBuilder.must(rangeQuery.gte(minutesAgo));
        boolBuilder.must(rangeQuery.lt(now));

        //索引前缀为metricbeat*, 按时间倒序排序
        String indexPrefix = "metricbeat*";
        FieldSortBuilder order = SortBuilders.fieldSort("@timestamp").order(SortOrder.DESC);
        SearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withIndices(indexPrefix)
                .withQuery(boolBuilder)
                .withSort(order)
                .withPageable(PageRequest.of(0, 1))
                .build();
        Set<String> allFields = elasticsearchTemplate.query(searchQuery, searchResponse -> {
            SearchHits hits = searchResponse.getHits();
            Set<String> fields = new HashSet<>();
            Arrays.stream(hits.getHits()).forEach(hit -> {
                Map<String, Object> sourceAsMap = hit.getSourceAsMap();
                Map<String, Object> flatMap = MapFlatUtil.flat(sourceAsMap, null);
                for (String key : flatMap.keySet()) {
                    Object value = flatMap.get(key);
                    boolean isNum = isNumber(value);
                    if (isNum) {
                        fields.add(key);
                    }
                }
            });
            return fields;
        });
        return allFields;
    }

    /**
     * 判断是否为数字格式不限制位数
     * @param o
     *     待校验参数
     * @return
     *     如果全为数字，返回true；否则，返回false
     */
    public static boolean isNumber(Object o){
        String str = String.valueOf(o);
        boolean isInt = Pattern.compile("^-?[1-9]\\d*$").matcher(str).find();
        boolean isDouble = Pattern.compile("^-?([1-9]\\d*\\.\\d*|0\\.\\d*[1-9]\\d*|0?\\.0+|0)$").matcher(str).find();
        return isInt || isDouble;
    }
}
