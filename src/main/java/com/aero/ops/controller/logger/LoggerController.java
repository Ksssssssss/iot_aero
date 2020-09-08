package com.aero.ops.controller.logger;

import com.aero.common.model.PageModel;
import com.aero.common.model.ResponseModel;
import com.aero.common.model.ResultState;
import com.aero.ops.entity.dto.AppLogQueryDTO;
import com.aero.ops.entity.vo.AppLogVO;
import com.aero.ops.service.IAppLogService;
import com.aero.ops.utils.LayTableUtil;
import com.aero.ops.utils.ResultUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.elasticsearch.action.ActionFuture;
import org.elasticsearch.action.admin.indices.stats.IndexStats;
import org.elasticsearch.action.admin.indices.stats.IndicesStatsRequest;
import org.elasticsearch.action.admin.indices.stats.IndicesStatsResponse;
import org.elasticsearch.client.IndicesAdminClient;
import org.elasticsearch.client.transport.TransportClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@RestController
@RequestMapping(value = "/logger")
@Api(tags={"微服务日志接口"}, value = "LoggerController")
public class LoggerController {

    @Autowired
    IAppLogService appLogService;

    @Autowired
    private TransportClient client;

    @RequestMapping(value = "/apps", method = RequestMethod.GET)
    public ResponseModel<List<String>> apps(){
        IndicesAdminClient indicesAdminClient = client.admin().indices();
        ActionFuture<IndicesStatsResponse> isr = indicesAdminClient.stats(new IndicesStatsRequest().all());
        Map<String, IndexStats> indexStatsMap = isr.actionGet().getIndices();
        Set<String> set = indexStatsMap.keySet();
        List<String> apps = Collections.emptyList();
        if (!CollectionUtils.isEmpty(set)) {
            apps = new ArrayList<>();
            for (String index : set) {
                if(index.startsWith("elk-")){
                    String app = index.substring(4);
                    apps.add(app);
                }
            }
        }
        return ResultUtils.me.success(apps);
    }

    @RequestMapping(value = "/getLogs", method = RequestMethod.POST)
    @ApiOperation(value = "查询微服务日志分页数据")
    public String getLogs(@RequestBody AppLogQueryDTO queryDTO){
        try {
            Date startTime = queryDTO.getStartTime();
            Date endTime = queryDTO.getEndTime();
            if(Objects.nonNull(startTime) && Objects.nonNull(endTime) && startTime.after(endTime)){
                ResponseModel error = ResultUtils.error(ResultState.PARAM_ERROR, "开始时间大于结束时间！");
                return LayTableUtil.me.convertResult2LayTable(error);
            }
            PageModel<List<AppLogVO>> listPageModel = appLogService.queryMergeLogs(queryDTO);
            ResponseModel result = ResultUtils.me.success(listPageModel);
            return LayTableUtil.me.convertResult2LayTable(result);
        } catch (Exception e) {
            e.printStackTrace();
            ResponseModel error = ResultUtils.error(ResultState.EXCEPTION, e.getMessage());
            return LayTableUtil.me.convertResult2LayTable(error);
        }
    }
}
