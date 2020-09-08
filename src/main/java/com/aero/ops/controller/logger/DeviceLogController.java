package com.aero.ops.controller.logger;

import com.aero.common.logger.DeviceLog;
import com.aero.common.model.PageModel;
import com.aero.common.model.ResponseModel;
import com.aero.common.model.ResultState;
import com.aero.ops.entity.dto.DeviceLogQueryDTO;
import com.aero.ops.service.IDevcieLogService;
import com.aero.ops.utils.LayTableUtil;
import com.aero.ops.utils.ResultUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping(value = "/deviceLog")
@Api(tags={"设备日志接口"}, value = "DeviceLogController")
public class DeviceLogController {

    @Autowired
    IDevcieLogService devcieLogService;

    @RequestMapping(value = "/getLogs", method = RequestMethod.POST)
    @ApiOperation(value = "查询微服务日志分页数据")
    public String getLogs(@RequestBody DeviceLogQueryDTO queryDTO){
        try {
            Date startTime = queryDTO.getStartTime();
            Date endTime = queryDTO.getEndTime();
            if(Objects.nonNull(startTime) && Objects.nonNull(endTime) && startTime.after(endTime)){
                ResponseModel error = ResultUtils.error(ResultState.PARAM_ERROR, "开始时间大于结束时间！");
                return LayTableUtil.me.convertResult2LayTable(error);
            }
            PageModel<List<DeviceLog>> listPageModel = devcieLogService.queryDeviceLogs(queryDTO);
            ResponseModel result = ResultUtils.me.success(listPageModel);
            return LayTableUtil.me.convertResult2LayTable(result);
        } catch (Exception e) {
            e.printStackTrace();
            ResponseModel error = ResultUtils.error(ResultState.EXCEPTION, e.getMessage());
            return LayTableUtil.me.convertResult2LayTable(error);
        }
    }
}
