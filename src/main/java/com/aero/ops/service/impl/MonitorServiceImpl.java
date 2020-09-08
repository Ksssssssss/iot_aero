package com.aero.ops.service.impl;

import com.aero.ops.entity.vo.MetricbeatTcpVO;
import com.aero.ops.entity.vo.MetricbeatVO;
import com.aero.ops.service.IMonitorService;
import com.alibaba.fastjson.JSON;
import org.elasticsearch.action.ActionFuture;
import org.elasticsearch.action.admin.indices.stats.IndicesStatsRequest;
import org.elasticsearch.action.admin.indices.stats.IndicesStatsResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.concurrent.ExecutionException;

@Service
public class MonitorServiceImpl implements IMonitorService {
    @Autowired
    TransportClient transportClient;
    /**
     * 获取所有index
     */
    public List<String> getAllIndices() {
        ActionFuture<IndicesStatsResponse> isr = transportClient.admin().indices().stats(new IndicesStatsRequest().all());
        Set<String> set = isr.actionGet().getIndices().keySet();
        return new ArrayList(set);
    }

    public String getSystemCPU(String indices) {
        TermQueryBuilder termQueryBuilder = QueryBuilders.termQuery("metricset.name", "process");
        BoolQueryBuilder boolBuilder = QueryBuilders.boolQuery();
        boolBuilder.must(termQueryBuilder);
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        // 起始条数
        sourceBuilder.from(0);
        // 显示数据条数
        sourceBuilder.size(10);
        sourceBuilder.sort("@timestamp", SortOrder.DESC);
        sourceBuilder.sort("system.process.cpu.user.norm.pct", SortOrder.DESC);
        sourceBuilder.query(boolBuilder);
        try {
            //查询索引对象
            SearchRequest searchRequest = new SearchRequest(indices);
            searchRequest.source(sourceBuilder);
            SearchResponse response = transportClient.search(searchRequest).get();
            SearchHits hits = response.getHits();
            Map<String,MetricbeatVO> metricbeatMap = new HashMap<>();
            for (SearchHit hit:hits) {
                Map<String, Object> sourceAsMap = hit.getSourceAsMap();
                MetricbeatVO metricbeat = new MetricbeatVO();
                int pid = (Integer) ((HashMap) sourceAsMap.get("process")).get("pid");
                metricbeat.setPid(pid);
                String name = ((HashMap) sourceAsMap.get("process")).get("name").toString();
                metricbeat.setName(name);
                Object cpu = ((HashMap)((HashMap)((HashMap) sourceAsMap.get("system")).get("process")).get("cpu")).get("total");
                double cpuSize = Double.parseDouble(((HashMap)cpu).get("value").toString());
                metricbeat.setCpuSize(cpuSize);
                double cpuPct = Double.parseDouble(((HashMap)cpu).get("pct").toString())*100;
                metricbeat.setCpuPct(cpuPct);
                String hostName = ((HashMap)sourceAsMap.get("host")).get("name").toString();
                metricbeat.setHostName(hostName);
                String hostId = ((HashMap)sourceAsMap.get("host")).get("id").toString();
                metricbeat.setHostId(hostId);
                metricbeatMap.put(String.valueOf(pid),metricbeat);
            }

            return JSON.toJSONString(metricbeatMap);
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return "";
    }

    public String getSystemMemory(String indices) {
        TermQueryBuilder termQueryBuilder = QueryBuilders.termQuery("metricset.name", "process");
        BoolQueryBuilder boolBuilder = QueryBuilders.boolQuery();
        boolBuilder.must(termQueryBuilder);
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        // 起始条数
        sourceBuilder.from(0);
        // 显示数据条数
        sourceBuilder.size(10);
        sourceBuilder.sort("@timestamp", SortOrder.DESC);
        sourceBuilder.sort("system.process.memory.size", SortOrder.DESC);
        sourceBuilder.query(boolBuilder);
        try {
            //查询索引对象
            SearchRequest searchRequest = new SearchRequest(indices);
            searchRequest.source(sourceBuilder);
            SearchResponse response = transportClient.search(searchRequest).get();
            SearchHits hits = response.getHits();
            Map<String,MetricbeatVO> metricbeatMap = new HashMap<>();
            for (SearchHit hit:hits) {
                Map<String, Object> sourceAsMap = hit.getSourceAsMap();
                MetricbeatVO metricbeat = new MetricbeatVO();
                int pid = (Integer) ((HashMap) sourceAsMap.get("process")).get("pid");
                metricbeat.setPid(pid);
                String name = ((HashMap) sourceAsMap.get("process")).get("name").toString();
                metricbeat.setName(name);
                Object memory = ((HashMap)((HashMap) sourceAsMap.get("system")).get("process")).get("memory");
                long memorySize = Long.parseLong(((HashMap)memory).get("size").toString())/1024/1024;
                metricbeat.setMemorySize(memorySize);
                Object rss = ((HashMap)memory).get("rss");
                double memoryPct = Double.parseDouble(((HashMap)rss).get("pct").toString());
                metricbeat.setMemoryPct(memoryPct);
                String hostName = ((HashMap)sourceAsMap.get("host")).get("name").toString();
                metricbeat.setHostName(hostName);
                String hostId = ((HashMap)sourceAsMap.get("host")).get("id").toString();
                metricbeat.setHostId(hostId);
                metricbeatMap.put(String.valueOf(pid),metricbeat);
            }

            return JSON.toJSONString(metricbeatMap);
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return "";
    }

    public String getSystemTCP(String indices) {
        TermQueryBuilder termQueryBuilder = QueryBuilders.termQuery("metricset.name", "socket_summary");
        BoolQueryBuilder boolBuilder = QueryBuilders.boolQuery();
        boolBuilder.must(termQueryBuilder);
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        // 起始条数
        sourceBuilder.from(0);
        // 显示数据条数
        sourceBuilder.size(1);
        sourceBuilder.sort("@timestamp", SortOrder.DESC);
        sourceBuilder.query(boolBuilder);
        try {
            //查询索引对象
            SearchRequest searchRequest = new SearchRequest(indices);
            searchRequest.source(sourceBuilder);
            SearchResponse response = transportClient.search(searchRequest).get();
            SearchHits hits = response.getHits();
            if(hits.getHits()==null || hits.getHits().length==0){
                return "";
            }
            SearchHit hit = hits.getHits()[0];
            Map<String, Object> sourceAsMap = hit.getSourceAsMap();
            MetricbeatTcpVO metricbeatTcp = new MetricbeatTcpVO();
            Object all = ((HashMap) ((HashMap) ((HashMap) ((HashMap) sourceAsMap.get("system")).get("socket")).get("summary")).get("tcp")).get("all");
            int established = Integer.parseInt(((HashMap) all).get("established").toString());
            metricbeatTcp.setEstablished(established);
            int closeWait = Integer.parseInt(((HashMap) all).get("close_wait").toString());
            metricbeatTcp.setCloseWait(closeWait);
            int timeWait = Integer.parseInt(((HashMap) all).get("time_wait").toString());
            metricbeatTcp.setTimeWait(timeWait);
            int listening = Integer.parseInt(((HashMap) all).get("listening").toString());
            metricbeatTcp.setListening(listening);
            String hostName = ((HashMap) sourceAsMap.get("host")).get("name").toString();
            metricbeatTcp.setHostName(hostName);
            String hostId = ((HashMap) sourceAsMap.get("host")).get("id").toString();
            metricbeatTcp.setHostId(hostId);

            return JSON.toJSONString(metricbeatTcp);
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return "";
    }
}
